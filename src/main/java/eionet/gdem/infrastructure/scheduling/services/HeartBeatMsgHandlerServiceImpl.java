package eionet.gdem.infrastructure.scheduling.services;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.service.WorkerHeartBeatMsgService;
import eionet.gdem.rabbitMQ.model.WorkerHeartBeatMessageInfo;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HeartBeatMsgHandlerServiceImpl implements HeartBeatMsgHandlerService {

    private WorkerHeartBeatMsgService workerHeartBeatMsgService;
    private RabbitMQMessageSender rabbitMQMessageSender;

    @Autowired
    public HeartBeatMsgHandlerServiceImpl(WorkerHeartBeatMsgService workerHeartBeatMsgService, RabbitMQMessageSender rabbitMQMessageSender) {
        this.workerHeartBeatMsgService = workerHeartBeatMsgService;
        this.rabbitMQMessageSender = rabbitMQMessageSender;
    }

    @Transactional
    @Override
    public void saveMsgAndSendToRabbitMQ(WorkerHeartBeatMessageInfo heartBeatMsgInfo, WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry) {
        workerHeartBeatMsgEntry = workerHeartBeatMsgService.save(workerHeartBeatMsgEntry);
        heartBeatMsgInfo.setId(workerHeartBeatMsgEntry.getId());
        rabbitMQMessageSender.sendHeartBeatMessage(heartBeatMsgInfo);
    }
}
