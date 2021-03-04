package eionet.gdem.api;

import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.JobHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/worker")
public class WorkerStatusController {

    private JobService jobService;
    private JobExecutorService jobExecutorService;
    private JobHistoryService jobHistoryService;

    @Autowired
    public WorkerStatusController(JobService jobService, JobExecutorService jobExecutorService, JobHistoryService jobHistoryService) {
        this.jobService = jobService;
        this.jobExecutorService = jobExecutorService;
        this.jobHistoryService = jobHistoryService;
    }

    @PostMapping("/fail")
    public void changeWorkerStatusToFailed(HttpSession session) {
        Integer jobId = (Integer) session.getAttribute("jobId");
        if (jobId!=null) {
            try {
                JobEntry jobEntry = jobService.findById(jobId);
                jobExecutorService.updateJobExecutor(SchedulingConstants.WORKER_FAILED, jobId, jobEntry.getJobExecutorName());
                jobService.changeNStatus(jobId, Constants.CANCELLED_BY_USER);
                XQScript script = getScript(jobId, jobEntry);
                jobHistoryService.updateStatusesAndJobExecutorName(script, Constants.CANCELLED_BY_USER, SchedulingConstants.INTERNAL_STATUS_PROCESSING, jobEntry.getJobExecutorName(), jobEntry.getJobType());
            } finally {
                session.removeAttribute("jobId");
            }
        }
    }

    protected XQScript getScript(Integer jobId, JobEntry jobEntry) {
        XQScript script = new XQScript();
        script.setJobId(jobId.toString());
        script.setSrcFileUrl(jobEntry.getUrl());
        script.setScriptFileName(jobEntry.getFile());
        script.setStrResultFile(jobEntry.getResultFile());
        script.setScriptType(jobEntry.getType());
        return script;
    }
}























