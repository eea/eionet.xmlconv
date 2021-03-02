package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.qa.XQScript;

import java.sql.Timestamp;

public interface JobService {

    void changeNStatus(XQScript xqScript, Integer status);

    void changeIntStatusAndJobExecutorName(InternalSchedulingStatus intStatus, String jobExecutorName, Timestamp timestamp, Integer jobId);

    JobEntry findById(Integer id);
}
