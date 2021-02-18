package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;

public interface JobExecutorService {

    void updateJobExecutor(Integer status, Integer jobId, String jobExecutorName);

    void saveJobExecutor(JobExecutor jobExecutor);
}
