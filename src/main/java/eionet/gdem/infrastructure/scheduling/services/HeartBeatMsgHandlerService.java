package eionet.gdem.infrastructure.scheduling.services;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;

public interface HeartBeatMsgHandlerService {

    void saveMsgAndSendToRabbitMQ(WorkerHeartBeatMessageInfo heartBeatMsgInfo, WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry);
}
