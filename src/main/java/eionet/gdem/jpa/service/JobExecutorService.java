package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;

import java.util.List;

public interface JobExecutorService {

    JobExecutor findByName(String jobExecutorName);

    void updateJobExecutor(Integer status, Integer jobId, String jobExecutorName, String containerId, String heartBeatQueue);

    void saveJobExecutor(JobExecutor jobExecutor);

    List<JobExecutor> listJobExecutor();
}
