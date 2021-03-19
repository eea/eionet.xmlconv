package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;

public interface WorkerHeartBeatMsgService {

    /**
     * updates a WorkerHeartBeatMsgEntry
     * @param workerHeartBeatMsgEntry
     */
    void updateEntry(WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry);
}
