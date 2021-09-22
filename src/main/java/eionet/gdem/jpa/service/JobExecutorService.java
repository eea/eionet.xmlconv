package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;

import java.util.List;

public interface JobExecutorService {

    JobExecutor findByName(String jobExecutorName) throws DatabaseException;

    void saveOrUpdateJobExecutor(JobExecutor jobExecutor) throws DatabaseException;

    List<JobExecutor> listJobExecutor() throws DatabaseException;

    void deleteByContainerId(String containerId) throws DatabaseException;

    List<JobExecutor> findByStatus(Integer status);

    void deleteByName(String name) throws DatabaseException;

    List<JobExecutor> findExecutorsByJobId(Integer jobId) throws DatabaseException;
}
