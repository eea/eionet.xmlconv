package eionet.gdem.rabbitMQ;

import org.springframework.amqp.core.Message;

public class WorkersJobsResultsMessageReceiver {

    public void handleMessage(String message) {
        System.out.println("Message Received: " + message);
    }
}
