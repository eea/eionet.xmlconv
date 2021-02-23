package eionet.gdem.services;

import eionet.gdem.XMLConvException;
import org.quartz.SchedulerException;

import java.sql.SQLException;

public interface SchedulerService {
    void scheduleJob (String JobID, long sizeInBytes, String scriptType ) throws SchedulerException;
    void rescheduleJob(String JobID) throws SchedulerException, SQLException, XMLConvException;
}
