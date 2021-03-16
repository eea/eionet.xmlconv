package eionet.gdem.rabbitMQ.listeners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerJobExecutionInfo;
import eionet.gdem.services.JobHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class WorkerJobExecutionResponseReceiver implements MessageListener {

    @Autowired
    JobService jobService;

    @Autowired
    JobHistoryService jobHistoryService;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerJobExecutionResponseReceiver.class);

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            WorkerJobExecutionInfo response = mapper.readValue(message.getBody(), WorkerJobExecutionInfo.class);

            JobEntry jobEntry = jobService.findById(response.getJobId());
            if (jobEntry.getnStatus()==Constants.XQ_PROCESSING && !response.isExecuting()) {
                jobService.changeNStatus(response.getJobId(), Constants.XQ_FATAL_ERR);
                InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                jobService.changeIntStatusAndJobExecutorName(internalStatus, response.getJobExecutorName(), new Timestamp(new Date().getTime()), jobEntry.getId());
                XQScript script = createScriptFromJobEntry(jobEntry);
                jobHistoryService.updateStatusesAndJobExecutorName(script, Constants.XQ_FATAL_ERR, SchedulingConstants.INTERNAL_STATUS_CANCELLED, jobEntry.getJobExecutorName(), jobEntry.getJobType());
            }
        } catch (Exception e) {
            LOGGER.info("Error during jobExecutor message processing: ", e.getMessage());
            return;
        }
    }

    protected XQScript createScriptFromJobEntry(JobEntry jobEntry) {
        XQScript script = new XQScript();
        script.setJobId(jobEntry.getId().toString());
        script.setSrcFileUrl(jobEntry.getUrl());
        script.setScriptFileName(jobEntry.getFile());
        script.setStrResultFile(jobEntry.getResultFile());
        script.setScriptType(jobEntry.getType());
        return script;
    }

}













