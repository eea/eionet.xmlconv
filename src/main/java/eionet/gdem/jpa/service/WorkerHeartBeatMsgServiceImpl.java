package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.WorkerHeartBeatMsgRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkerHeartBeatMsgServiceImpl implements WorkerHeartBeatMsgService {

    private WorkerHeartBeatMsgRepository workerHeartBeatMsgRepository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerHeartBeatMsgServiceImpl.class);

    @Autowired
    public WorkerHeartBeatMsgServiceImpl(WorkerHeartBeatMsgRepository workerHeartBeatMsgRepository) {
        this.workerHeartBeatMsgRepository = workerHeartBeatMsgRepository;
    }

    @Override
    public WorkerHeartBeatMsgEntry save(WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry) {
        return workerHeartBeatMsgRepository.save(workerHeartBeatMsgEntry);
    }

    @Override
    public List<WorkerHeartBeatMsgEntry> findUnAnsweredHeartBeatMessages(Integer jobId) throws DatabaseException {
        try {
            return workerHeartBeatMsgRepository.findUnAnsweredHeartBeatMessages(jobId);
        } catch (Exception e) {
            LOGGER.error("Database error while searching for heart beat messages of job with id " +jobId);
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        WorkerHeartBeatMsgEntry entryToBeDeleted = workerHeartBeatMsgRepository.getById(id);
        if(entryToBeDeleted != null){
            workerHeartBeatMsgRepository.delete(entryToBeDeleted);
        }
    }
}
