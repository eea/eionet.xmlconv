package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.utils.JobExecutorType;

import java.util.List;

public interface JobExecutorService {

    JobExecutor findByName(String jobExecutorName) throws DatabaseException;

    void saveOrUpdateJobExecutor(boolean update, JobExecutor jobExecutor) throws DatabaseException;

    List<JobExecutor> listJobExecutor() throws DatabaseException;

    void deleteByContainerId(String containerId) throws DatabaseException;

    List<JobExecutor> findByStatus(Integer status);

    List<JobExecutor> findByStatusAndJobExecutorType(Integer status, JobExecutorType jobExecutorType);

    void deleteByName(String name) throws DatabaseException;

    List<JobExecutor> findExecutorsByJobId(Integer jobId) throws DatabaseException;
}
