package eionet.gdem.rabbitMQ.service;

import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorType;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.rabbitMQ.listeners.DeadLetterQueueMessageReceiver;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HandleHeavyJobsServiceImpl implements HandleHeavyJobsService {

    private RabbitMQMessageSender rabbitMQMessageSender;
    private JobExecutorService jobExecutorService;

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleHeavyJobsServiceImpl.class);

    @Autowired
    public HandleHeavyJobsServiceImpl(RabbitMQMessageSender rabbitMQMessageSender, JobExecutorService jobExecutorService) {
        this.rabbitMQMessageSender = rabbitMQMessageSender;
        this.jobExecutorService = jobExecutorService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(WorkerJobRabbitMQRequest workerJobRabbitMQRequest) throws DatabaseException {
        LOGGER.info("Handling heavy job " + workerJobRabbitMQRequest.getScript().getJobId());
        rabbitMQMessageSender.sendJobInfoToHeavyRabbitmqQueue(workerJobRabbitMQRequest);
        checkWorkerStatus(Integer.parseInt(workerJobRabbitMQRequest.getScript().getJobId()));
    }

    void checkWorkerStatus(Integer jobId) throws DatabaseException {
        List<JobExecutor> jobExecutors = jobExecutorService.findExecutorsByJobId(jobId);
        try {
            if (jobExecutors.size()==0) Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jobExecutors = jobExecutorService.findExecutorsByJobId(jobId);
        System.out.println("found jobexecutors: " + jobExecutors);
        jobExecutors.stream().forEach(jobExecutor -> System.out.println(jobExecutor));
        jobExecutors.get(0).setStatus(SchedulingConstants.WORKER_FAILED);
        LOGGER.info("Setting jobExecutor with name " + jobExecutors.get(0).getName() + " to failed");
        jobExecutorService.saveOrUpdateJobExecutor(jobExecutors.get(0));


//        if (jobExecutors.size()==1 && jobExecutors.get(0).getType()== JobExecutorType.Heavy) {
//            return;
//        } else {
//            List<JobExecutor> lightJobExecutors = jobExecutors.stream().filter(j -> j.getType() == JobExecutorType.Light).collect(Collectors.toList());
//            JobExecutor lightWorker = lightJobExecutors.get(0);
//            if (lightJobExecutors!=null && lightWorker.getStatus()==0) {
//                //set the status of light worker to failed because of heavy job. The worker has to be deleted as its status remains 0 although heavy
//                //job was sent to heavy queue and it incorrectly responds to heart beat messages that it executes the heavy job
//                lightWorker.setStatus(SchedulingConstants.WORKER_FAILED);
//                jobExecutorService.saveOrUpdateJobExecutor(lightWorker);
//            }
//        }
    }
}
