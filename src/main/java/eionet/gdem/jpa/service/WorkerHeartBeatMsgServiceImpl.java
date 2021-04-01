package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.repositories.WorkerHeartBeatMsgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerHeartBeatMsgServiceImpl implements WorkerHeartBeatMsgService {

    private WorkerHeartBeatMsgRepository workerHeartBeatMsgRepository;

    @Autowired
    public WorkerHeartBeatMsgServiceImpl(WorkerHeartBeatMsgRepository workerHeartBeatMsgRepository) {
        this.workerHeartBeatMsgRepository = workerHeartBeatMsgRepository;
    }

    @Override
    public WorkerHeartBeatMsgEntry save(WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry) {
        return workerHeartBeatMsgRepository.save(workerHeartBeatMsgEntry);
    }

    @Override
    public List<WorkerHeartBeatMsgEntry> findUnAnsweredHeartBeatMessages(Integer jobId) {
        return workerHeartBeatMsgRepository.findUnAnsweredHeartBeatMessages(jobId);
    }
}
