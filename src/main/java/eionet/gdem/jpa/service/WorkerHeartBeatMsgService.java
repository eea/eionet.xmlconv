package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;

import java.util.List;

public interface WorkerHeartBeatMsgService {

    WorkerHeartBeatMsgEntry save(WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry);

    List<WorkerHeartBeatMsgEntry> findUnAnsweredHeartBeatMessages(Integer jobId);
}
