package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.repositories.JobExecutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobExecutorServiceImpl implements JobExecutorService {

    private JobExecutorRepository jobExecutorRepository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutorServiceImpl.class);

    @Autowired
    public JobExecutorServiceImpl(JobExecutorRepository jobExecutorRepository) {
        this.jobExecutorRepository = jobExecutorRepository;
    }

    @Transactional
    @Override
    public void updateJobExecutor(Integer status, Integer jobId, String name) {
        try {
            jobExecutorRepository.updateStatusAndJobId(status, jobId, name);
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
                return;
            } else {
                jobExecutorRepository.save(jobExecutor);
            }
        } catch (Exception e) {
            LOGGER.error("Database exception when saving worker with name " + jobExecutor.getName() + ", " + e.toString());
            throw e;
        }
    }
}























