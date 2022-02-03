package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.XMLConvException;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.Entities.ScriptRulesEntry;
import eionet.gdem.jpa.enums.ScriptRuleField;
import eionet.gdem.jpa.enums.ScriptRuleMatch;
import eionet.gdem.jpa.enums.ScriptRuleType;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefineJobQueueByScriptAndScriptRulesImpl implements DefineJobQueueAndSendToRabbitMQTemplate {

    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;
    private JobService jobService;
    private RabbitMQMessageSender rabbitMQLightMessageSender;
    private RabbitMQMessageSender rabbitMQHeavyMessageSender;
    private static int BYTES_TRANSFORM = 1048576;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefineJobQueueByScriptAndScriptRulesImpl.class);

    @Autowired
    public DefineJobQueueByScriptAndScriptRulesImpl(WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService, JobService jobService,
                                                    @Qualifier("lightJobRabbitMessageSenderImpl") RabbitMQMessageSender rabbitMQLightMessageSender,
                                                    @Qualifier("heavyJobRabbitMessageSenderImpl") RabbitMQMessageSender rabbitMQHeavyMessageSender) {
        this.workerAndJobStatusHandlerService = workerAndJobStatusHandlerService;
        this.jobService = jobService;
        this.rabbitMQLightMessageSender = rabbitMQLightMessageSender;
        this.rabbitMQHeavyMessageSender = rabbitMQHeavyMessageSender;
    }

    @Override
    public void checkHeavyOrLight(QueryEntry queryEntry, JobEntry jobEntry) {
        if (jobEntry.getQueryId() == 0) return;
        if (queryEntry != null && queryEntry.getMarkedHeavy()) {
            jobEntry.setHeavy(true);
        }
    }

    @Override
    public void checkRules(QueryEntry queryEntry, JobEntry jobEntry) throws XMLConvException {
        boolean collectionPath = false, xmlFileSizeGreater = false, xmlFileSizeSmaller = false, collectionPathRule = false, xmlFileSizeGreaterGreaterRule = false, xmlFileSizeSmallerRule = false;
        if (jobEntry.getQueryId() == 0) return;
        if (!jobEntry.isHeavy()) {
            List<ScriptRulesEntry> scriptRulesEntries = queryEntry.getRulesEntryList().stream().filter(e -> e.isEnabled()).collect(Collectors.toList());
            if (queryEntry.getRuleMatch()!=null && queryEntry.getRuleMatch().equals(ScriptRuleMatch.ALL.getValue())) {
                for (ScriptRulesEntry rule : scriptRulesEntries) {
                    if (rule.getType().equals(ScriptRuleType.INCLUDES.getValue())) {
                        collectionPathRule = true;
                        collectionPath = checkCollectionPath(jobEntry.getUrl(), rule.getValue());
                        if (!collectionPath) return;
                    } else if (rule.getType().equals(ScriptRuleType.GREATER_THAN.getValue())) {
                        xmlFileSizeGreaterGreaterRule = true;
                        xmlFileSizeGreater = checkXmlFileSizeGreater(jobEntry.getUrl(), rule.getValue());
                        if (!xmlFileSizeGreater) return;
                    } else if (rule.getType().equals(ScriptRuleType.SMALLER_THAN.getValue())) {
                        xmlFileSizeSmallerRule = true;
                        xmlFileSizeSmaller = checkXmlFileSizeSmaller(jobEntry.getUrl(), rule.getValue());
                        if (!xmlFileSizeSmaller) return;
                    }
                }
                if (!collectionPathRule) collectionPath = true;
                if (!xmlFileSizeGreaterGreaterRule) xmlFileSizeGreater = true;
                if (!xmlFileSizeSmallerRule) xmlFileSizeSmaller = true;
                if (collectionPath && xmlFileSizeGreater && xmlFileSizeSmaller) {
                    jobEntry.setHeavy(true);
                }
            } else if (queryEntry.getRuleMatch()!=null && queryEntry.getRuleMatch().equals(ScriptRuleMatch.AT_LEAST_ONE.getValue())) {
                for (ScriptRulesEntry rule : scriptRulesEntries) {
                    if (rule.getField().equals(ScriptRuleField.COLLECTION_PATH.getValue())) {
                        if (checkCollectionPath(jobEntry.getUrl(), rule.getValue())) {
                            jobEntry.setHeavy(true);
                            return;
                        }
                    } else if (rule.getType().equals(ScriptRuleType.GREATER_THAN.getValue())) {
                        if (checkXmlFileSizeGreater(jobEntry.getUrl(), rule.getValue())) {
                            jobEntry.setHeavy(true);
                            return;
                        }
                    } else if (rule.getType().equals(ScriptRuleType.SMALLER_THAN.getValue())) {
                        if (checkXmlFileSizeSmaller(jobEntry.getUrl(), rule.getValue())) {
                            jobEntry.setHeavy(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean checkCollectionPath(String url, String ruleValue) {
        if (url.contains(ruleValue)) {
            return true;
        }
        return false;
    }

    private boolean checkXmlFileSizeGreater(String url, String ruleValue) throws XMLConvException {
        long xmlFileSize = getXmlFileSize(url);
        BigDecimal result = new BigDecimal(Double.parseDouble(ruleValue) * BYTES_TRANSFORM);
        BigDecimal xmlSize = new BigDecimal(xmlFileSize);
        if (xmlSize.compareTo(result) > 0) {
            return true;
        }
        return false;
    }

    private boolean checkXmlFileSizeSmaller(String url, String ruleValue) throws XMLConvException {
        long xmlFileSize = getXmlFileSize(url);
        BigDecimal result = new BigDecimal(Double.parseDouble(ruleValue) * BYTES_TRANSFORM);
        BigDecimal xmlSize = new BigDecimal(xmlFileSize);
        if (xmlSize.compareTo(result) < 0) {
            return true;
        }
        return false;
    }

    /**
     * returns size in bytes
     *
     * @param url
     * @return
     * @throws XMLConvException
     */
    public long getXmlFileSize(String url) throws XMLConvException {
        int index = url.indexOf("https");
        String finalUrl = url.substring(index);
        long sourceSize = HttpFileManager.getSourceURLSize(null, finalUrl, true);
        return sourceSize;
    }

    @Override
    public void updateDatabase(JobEntry jobEntry) throws DatabaseException {
        try {
            Integer retryCounter = jobService.getRetryCounter(jobEntry.getId());
            jobEntry.setInstance(Properties.getHostname()).setRetryCounter(retryCounter + 1).setnStatus(Constants.XQ_PROCESSING).setIntSchedulingStatus(new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_QUEUED));
            workerAndJobStatusHandlerService.updateJobAndJobHistoryEntries(jobEntry);
        } catch (DatabaseException e) {
            LOGGER.error("Database exception when updating job with id " + jobEntry.getId() + ". " + e.toString());
            throw e;
        }
    }

    @Override
    public void sendMsgToRabbitMQ(JobEntry jobEntry, WorkerJobRabbitMQRequestMessage message) {
        if (jobEntry.isHeavy()) {
            rabbitMQHeavyMessageSender.sendMessageToRabbitMQ(message);
        } else {
            rabbitMQLightMessageSender.sendMessageToRabbitMQ(message);
        }
    }
}
