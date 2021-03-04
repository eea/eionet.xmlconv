package eionet.gdem.services;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.qa.XQScript;

import java.sql.SQLException;

public interface JobOnDemandHandlerService {

    JobEntry createJobAndSendToRabbitMQ(XQScript xq, Integer scriptId) throws SQLException;
}
