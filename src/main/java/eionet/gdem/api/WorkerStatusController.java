package eionet.gdem.api;

import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
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

    @Autowired
    public WorkerStatusController(JobService jobService, JobExecutorService jobExecutorService) {
        this.jobService = jobService;
        this.jobExecutorService = jobExecutorService;
    }

    @PostMapping("/fail")
    public void changeWorkerStatusToFailed(HttpSession session) {
        Integer jobId = (Integer) session.getAttribute("jobId");
        if (jobId!=null) {
            try {
                JobEntry jobEntry = jobService.findById(jobId);
                jobExecutorService.updateJobExecutor(SchedulingConstants.WORKER_FAILED, jobId, jobEntry.getJobExecutorName());
            } finally {
                session.removeAttribute("jobId");
            }
        }
    }
}























