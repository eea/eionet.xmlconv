package eionet.gdem.api;

import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.qa.utils.ScriptUtils;
import eionet.gdem.services.JobHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping("/worker")
public class WorkerAndJobStatusController {

    private JobService jobService;
    private JobExecutorService jobExecutorService;
    private JobHistoryService jobHistoryService;
    private JobExecutorHistoryService jobExecutorHistoryService;

    @Autowired
    public WorkerAndJobStatusController(JobService jobService, JobExecutorService jobExecutorService, JobHistoryService jobHistoryService,
                                        JobExecutorHistoryService jobExecutorHistoryService) {
        this.jobService = jobService;
        this.jobExecutorService = jobExecutorService;
        this.jobHistoryService = jobHistoryService;
        this.jobExecutorHistoryService = jobExecutorHistoryService;
    }

    @PostMapping("/fail")
    public void changeJobAndWorkerStatusToFailed(HttpSession session) throws DatabaseException {
        Integer jobId = (Integer) session.getAttribute("jobId");
        if (jobId!=null) {
            try {
                JobEntry jobEntry = jobService.findById(jobId);
                if (jobEntry.getJobExecutorName()!=null) {
                    JobExecutor jobExecutor = jobExecutorService.findByName(jobEntry.getJobExecutorName());
                    jobExecutor.setStatus(SchedulingConstants.WORKER_FAILED);
                    jobExecutorService.saveOrUpdateJobExecutor(jobExecutor);
                    JobExecutorHistory entry = new JobExecutorHistory(jobEntry.getJobExecutorName(), jobExecutor.getContainerId(), SchedulingConstants.WORKER_FAILED, jobId, new Timestamp(new Date().getTime()), jobExecutor.getHeartBeatQueue());
                    jobExecutorHistoryService.saveJobExecutorHistoryEntry(entry);
                }
                jobService.changeNStatus(jobId, Constants.CANCELLED_BY_USER);
                InternalSchedulingStatus internalStatus = new InternalSchedulingStatus().setId(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                jobService.changeIntStatusAndJobExecutorName(internalStatus, jobEntry.getJobExecutorName(), new Timestamp(new Date().getTime()), jobId);
                XQScript script = ScriptUtils.createScriptFromJobEntry(jobEntry);
                jobHistoryService.updateStatusesAndJobExecutorName(script, Constants.CANCELLED_BY_USER, SchedulingConstants.INTERNAL_STATUS_CANCELLED, jobEntry.getJobExecutorName(), jobEntry.getJobType());
            } finally {
                session.removeAttribute("jobId");
            }
        }
    }

}






















