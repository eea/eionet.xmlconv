package eionet.gdem.rabbitMQ;

public class WorkersJobsResultsMessageReceiver {

    public void handleMessage(String message) {
        System.out.println("Message Received: " + message);
    }
}
