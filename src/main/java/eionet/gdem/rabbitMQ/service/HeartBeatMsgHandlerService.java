package eionet.gdem.rabbitMQ.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessage;

public interface HeartBeatMsgHandlerService {

    void saveMsgAndSendToRabbitMQ(WorkerHeartBeatMessage heartBeatMsgInfo, WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry);

    void updateHeartBeatJobAndQueryTables(WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry, WorkerHeartBeatMessage message, Integer nStatus, InternalSchedulingStatus internalStatus) throws DatabaseException, InterruptedException;
}
