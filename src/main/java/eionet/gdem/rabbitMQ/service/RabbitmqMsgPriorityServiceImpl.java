package eionet.gdem.rabbitMQ.service;

import eionet.gdem.Constants;
import eionet.gdem.rabbitMQ.enums.RabbitMQPriority;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import org.springframework.stereotype.Service;

@Service
public class RabbitmqMsgPriorityServiceImpl implements RabbitmqMsgPriorityService {

    @Override
    public Integer getMsgPriorityBasedOnJobType(WorkerJobRabbitMQRequestMessage message) {
        if (message.getJobType()!=null && message.getJobType().equals(Constants.ON_DEMAND_TYPE)) {
            if (message.isApi()) return RabbitMQPriority.ON_DEMAND_API.getId();
            else return RabbitMQPriority.ON_DEMAND_UI.getId();
        }
        return RabbitMQPriority.OTHER.getId();
    }
}
