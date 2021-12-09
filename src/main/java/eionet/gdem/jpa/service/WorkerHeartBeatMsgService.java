package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;

import java.util.List;

public interface WorkerHeartBeatMsgService {

    WorkerHeartBeatMsgEntry save(WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry);

    List<WorkerHeartBeatMsgEntry> findUnAnsweredHeartBeatMessages(Integer jobId) throws DatabaseException;

    void delete(Integer id);
}
