package eionet.gdem.rabbitMQ.service;

import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;

public interface RabbitmqMsgPriorityService {

    /**
     * OnDemand jobs have higher priority than other jobs. OnDemand jobs created through api have higher priority
     * than those created from UI.
     * @param message
     * @return
     */
    Integer getMsgPriorityBasedOnJobType(WorkerJobRabbitMQRequestMessage message);
}
