package eionet.gdem.jpa;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.rabbitMQ.model.WorkerJobInfoRabbitMQResponse;
import eionet.gdem.services.JobHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class JobUtils {

    private static JobService jobService;
    private static JobExecutorService jobExecutorService;
    private static JobExecutorHistoryService jobExecutorHistoryService;
    private static JobHistoryService jobHistoryService;

    @Autowired
    public JobUtils(JobService jobService, JobExecutorService jobExecutorService, JobExecutorHistoryService jobExecutorHistoryService, JobHistoryService jobHistoryService) {
        this.jobService = jobService;
        this.jobExecutorService = jobExecutorService;
        this.jobExecutorHistoryService = jobExecutorHistoryService;
        this.jobHistoryService = jobHistoryService;
    }

    public static void updateJobAndJobExecTables(Integer nStatus, Integer internalStatus, WorkerJobInfoRabbitMQResponse response, String containerId, JobEntry jobEntry) throws DatabaseException {
        XQScript script = response.getScript();
        jobService.changeNStatus(Integer.parseInt(script.getJobId()), nStatus);
        InternalSchedulingStatus intStatus = new InternalSchedulingStatus().setId(internalStatus);
        jobService.changeIntStatusAndJobExecutorName(intStatus, response.getJobExecutorName(), new Timestamp(new Date().getTime()), Integer.parseInt(script.getJobId()));
        jobHistoryService.updateStatusesAndJobExecutorName(script, nStatus, internalStatus, response.getJobExecutorName(), jobEntry.getJobType());
        JobExecutor jobExecutor = new JobExecutor(response.getJobExecutorName(), response.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), containerId, response.getHeartBeatQueue());
        jobExecutorService.saveOrUpdateJobExecutor(jobExecutor);
        JobExecutorHistory entry = new JobExecutorHistory(response.getJobExecutorName(), containerId, response.getJobExecutorStatus(), Integer.parseInt(script.getJobId()), new Timestamp(new Date().getTime()), response.getHeartBeatQueue());
        jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
    }
}
