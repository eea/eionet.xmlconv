package eionet.gdem.rabbitMQ.listeners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.jpa.service.WorkerHeartBeatMsgService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.qa.utils.ScriptUtils;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
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
public class WorkerHeartBeatResponseReceiver implements MessageListener {

    @Autowired
    JobService jobService;

    @Autowired
    JobHistoryService jobHistoryService;

    @Autowired
    WorkerHeartBeatMsgService workerHeartBeatMsgService;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerHeartBeatResponseReceiver.class);

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            WorkerHeartBeatMessageInfo response = mapper.readValue(message.getBody(), WorkerHeartBeatMessageInfo.class);

            LOGGER.info("Received heart beat response from worker " + response.getJobExecutorName() + " for job job " + response.getJobId());
            WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry = new WorkerHeartBeatMsgEntry(response.getJobId(), response.getJobExecutorName(), response.getRequestTimestamp(), new Timestamp(new Date().getTime()), response.getJobStatus());
            LOGGER.info(workerHeartBeatMsgEntry.toString());
            workerHeartBeatMsgService.updateEntry(workerHeartBeatMsgEntry);

            JobEntry jobEntry = jobService.findById(response.getJobId());
            if (jobEntry.getnStatus()==Constants.XQ_PROCESSING && response.getJobStatus().equals(Constants.JOB_NOT_FOUND_IN_WORKER)) {
                jobService.changeNStatus(response.getJobId(), Constants.XQ_FATAL_ERR);
                InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                jobService.changeIntStatusAndJobExecutorName(internalStatus, response.getJobExecutorName(), new Timestamp(new Date().getTime()), jobEntry.getId());
                XQScript script = ScriptUtils.createScriptFromJobEntry(jobEntry);
                jobHistoryService.updateStatusesAndJobExecutorName(script, Constants.XQ_FATAL_ERR, SchedulingConstants.INTERNAL_STATUS_CANCELLED, jobEntry.getJobExecutorName(), jobEntry.getJobType());
            }
        } catch (Exception e) {
            LOGGER.info("Error during jobExecutor message processing: ", e.getMessage());
            return;
        }
    }
}













