package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;

import java.util.List;

public interface JobExecutorService {

    void updateJobExecutor(Integer status, Integer jobId, String jobExecutorName, String containerId);

    void saveJobExecutor(JobExecutor jobExecutor);

    List<JobExecutor> listJobExecutor();
}
