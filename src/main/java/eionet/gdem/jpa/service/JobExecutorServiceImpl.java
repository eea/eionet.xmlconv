package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.repositories.JobExecutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobExecutorServiceImpl implements JobExecutorService {

    private JobExecutorRepository jobExecutorRepository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutorServiceImpl.class);

    @Autowired
    public JobExecutorServiceImpl(JobExecutorRepository jobExecutorRepository) {
        this.jobExecutorRepository = jobExecutorRepository;
    }

    @Override
    public JobExecutor findByName(String jobExecutorName) {
        JobExecutor jobExecutor = null;
        try {
            jobExecutor = jobExecutorRepository.findByName(jobExecutorName);
        } catch (Exception e) {
            LOGGER.info("Database exception during retrieval of jobExecutor with name " + jobExecutorName);
            throw e;
        }
        return jobExecutor;
    }

    @Transactional
    @Override
    public void updateJobExecutor(Integer status, Integer jobId, String name, String containerId, String heartBeatQueue) {
        try {
            JobExecutor jobExec = jobExecutorRepository.findByName(name);
            if (jobExec!=null) {
                jobExecutorRepository.updateStatusAndJobId(status, jobId, name);
            } else {
                JobExecutor exec = new JobExecutor(name, status, jobId, containerId, heartBeatQueue);
                jobExecutorRepository.save(exec);
            }
        } catch (Exception e) {
            LOGGER.error("Database exception when updating worker with name " + name + ", " + e.toString());
            throw e;
        }
    }

    @Transactional
    @Override
    public void saveJobExecutor(JobExecutor jobExecutor) {
        try {
            JobExecutor jobExec = jobExecutorRepository.findByName(jobExecutor.getName());
            if (jobExec!=null) {
                jobExecutorRepository.updateStatusAndJobId(jobExecutor.getStatus(), jobExecutor.getJobId(), jobExecutor.getName());
            } else {
                jobExecutorRepository.save(jobExecutor);
            }
        } catch (Exception e) {
            LOGGER.error("Database exception when saving worker with name " + jobExecutor.getName() + ", " + e.toString());
            throw e;
        }
    }

    @Transactional
    @Override
    public List<JobExecutor> listJobExecutor() {
        try {
            return jobExecutorRepository.findAll();
        } catch (Exception e) {
            LOGGER.error("Database exception when retrieving entries from JOB_EXECUTOR table");
            throw e;
        }
    }
}























