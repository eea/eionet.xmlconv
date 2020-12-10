package eionet.gdem.rabbitMQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkersJobsResultsMessageReceiver {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkersJobsResultsMessageReceiver.class);

    public void handleMessage(String message) {
        LOGGER.info(message);
    }
}
