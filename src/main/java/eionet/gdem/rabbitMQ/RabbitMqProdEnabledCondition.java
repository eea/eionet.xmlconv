package eionet.gdem.rabbitMQ;

import eionet.gdem.Properties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RabbitMqProdEnabledCondition  implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        if(Properties.getRabbitMqEnabled()){
            return true;
        }else {
            return false;
        }
    }
}
