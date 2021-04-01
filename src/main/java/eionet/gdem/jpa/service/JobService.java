package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.qa.XQScript;

import java.sql.Timestamp;
import java.util.List;

public interface JobService {

    void changeNStatus(Integer jobId, Integer status) throws DatabaseException;

    void changeIntStatusAndJobExecutorName(InternalSchedulingStatus intStatus, String jobExecutorName, Timestamp timestamp, Integer jobId) throws DatabaseException;

    JobEntry findById(Integer id);

    List<JobEntry> findByIntSchedulingStatus(InternalSchedulingStatus intSchedulingStatus);

    List<JobEntry> findProcessingJobs();
}
