package eionet.gdem.rabbitMQ.listeners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.rabbitMQ.service.HeartBeatMsgHandlerService;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.repositories.WorkerHeartBeatMsgRepository;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessage;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@Service
public class WorkerHeartBeatResponseReceiver implements MessageListener {

    @Autowired
    HeartBeatMsgHandlerService heartBeatMsgHandlerService;

    @Autowired
    WorkerHeartBeatMsgRepository workerHeartBeatMsgRepository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerHeartBeatResponseReceiver.class);
    /**
     * time in milliseconds
     */
    private static final Integer TIME_LIMIT = 10000;

    @Override
    public void onMessage(Message message) {
        WorkerHeartBeatMessage response = null;
        StopWatch timer = new StopWatch();
        try {
            ObjectMapper mapper =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            response = mapper.readValue(message.getBody(), WorkerHeartBeatMessage.class);

            LOGGER.info("Received heart beat response from worker " + response.getJobExecutorName() + " for job " + response.getJobId() + ". Job status is " + response.getJobStatus());

            WorkerHeartBeatMsgEntry oldEntry = null;
            Optional<WorkerHeartBeatMsgEntry> workerHeartBeatMsgEntryOptional = workerHeartBeatMsgRepository.findById(response.getId());
            if(workerHeartBeatMsgEntryOptional.isPresent()){
                oldEntry = workerHeartBeatMsgEntryOptional.get();
            }
            timer.start();
            while (oldEntry==null) {
                LOGGER.error("Could not retrieve heart beat message entry with id " + response.getId());
                workerHeartBeatMsgEntryOptional = workerHeartBeatMsgRepository.findById(response.getId());
                if(workerHeartBeatMsgEntryOptional.isPresent()){
                    oldEntry = workerHeartBeatMsgEntryOptional.get();
                }
                else{
                    oldEntry = null;
                }
                if (timer.getTime()>TIME_LIMIT) {
                    LOGGER.error("Could not update heart beat message entry with id " + response.getId());
                    return;
                }
            }
            oldEntry.setResponseTimestamp(new Timestamp(new Date().getTime()));
            oldEntry.setJobStatus(response.getJobStatus());
            LOGGER.info("Updating heart beat message entry with id " + response.getId());

            InternalSchedulingStatus internalStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
            //We will mark a job as Fatal Error, only when the heartbeat message Response
            // from the worker was JobNotFound, and the job is in processing status.
            heartBeatMsgHandlerService.updateHeartBeatJobAndQueryTables(oldEntry, response, Constants.XQ_FATAL_ERR, internalStatus);
        } catch (Exception e) {
            LOGGER.info("Error during jobExecutor message processing ", e);
        } finally {
            timer.stop();
        }
    }
}













