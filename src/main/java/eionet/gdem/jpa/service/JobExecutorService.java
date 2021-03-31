package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;

import java.util.List;

public interface JobExecutorService {

    JobExecutor findByName(String jobExecutorName);

    void saveOrUpdateJobExecutor(JobExecutor jobExecutor);

    List<JobExecutor> listJobExecutor();
}
