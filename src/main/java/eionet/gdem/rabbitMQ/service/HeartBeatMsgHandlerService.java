package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessage;

public interface HeartBeatMsgHandlerService {

    void saveMsgAndSendToRabbitMQ(WorkerHeartBeatMessage heartBeatMsgInfo, WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry);

    void updateHeartBeatAndJobTables(WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry, Integer jobId, Integer jobStatus, Integer nStatus, InternalSchedulingStatus internalStatus) throws DatabaseException;
}
