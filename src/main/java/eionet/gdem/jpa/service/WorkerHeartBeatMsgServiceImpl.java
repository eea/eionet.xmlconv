package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.repositories.WorkerHeartBeatMsgRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkerHeartBeatMsgServiceImpl implements WorkerHeartBeatMsgService {

    private WorkerHeartBeatMsgRepository repository;
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutorServiceImpl.class);

    @Autowired
    public WorkerHeartBeatMsgServiceImpl(WorkerHeartBeatMsgRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public void updateEntry(WorkerHeartBeatMsgEntry entry) {
        try {
            repository.updateResponseTimestampAndJobStatus(entry.getResponseTimestamp(), entry.getJobStatus(), entry.getJobId(), entry.getRequestTimestamp());
        } catch (Exception e) {
            LOGGER.error("Database exception when updating heart beat message for job with id " + entry.getId() + ", " + e.toString());
            throw e;
        }
    }
}













