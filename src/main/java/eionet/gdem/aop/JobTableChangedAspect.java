package eionet.gdem.aop;

import eionet.gdem.SpringApplicationContext;
import eionet.gdem.api.websockets.WebSocketWorkqueueController;
import eionet.gdem.jpa.service.JobService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JobTableChangedAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobTableChangedAspect.class);

    @AfterReturning(pointcut = "execution(* eionet.gdem.jpa.service.JobService.changeNStatus(..))")
    public void jobChangeNStatus() {
        LOGGER.info("Invocation of Aspect to change NStatus of job entry.");
        WebSocketWorkqueueController.setChangedJobTable(true);
    }

    @AfterReturning(pointcut = "execution(* eionet.gdem.jpa.service.JobService.saveOrUpdate(..))")
    public void jobSaveOrUpdate() {
        LOGGER.info("Invocation of Aspect to save or update job.");
        WebSocketWorkqueueController.setChangedJobTable(true);
    }

    @AfterReturning(pointcut = "execution(* eionet.gdem.jpa.service.JobService.deleteJobById(..))")
    public void jobDeleteJobById() {
        LOGGER.info("Invocation of Aspect to delete job.");
        WebSocketWorkqueueController.setChangedJobTable(true);
    }

    @AfterReturning(pointcut = "execution(* eionet.gdem.jpa.service.JobService.changeJobStatusAndTimestampByStatus(..))")
    public void jobChangeJobStatusAndTimestampByStatus() {
        LOGGER.info("Invocation of Aspect to change status and timestamp by status.");
        WebSocketWorkqueueController.setChangedJobTable(true);
    }

    @AfterReturning(pointcut = "execution(* eionet.gdem.jpa.service.JobServiceImpl.updateJobDurationsByIds(..))")
    public void jobUpdateJobDurationsByIds() {
        LOGGER.info("Invocation of Aspect to update job durations.");
        WebSocketWorkqueueController.setChangedJobTable(true);
    }
}
