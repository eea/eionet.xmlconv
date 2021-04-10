package eionet.gdem.api;

import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
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
    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;

    @Autowired
    public WorkerAndJobStatusController(JobService jobService, JobExecutorService jobExecutorService,
                                        WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService) {
        this.jobService = jobService;
        this.jobExecutorService = jobExecutorService;
        this.workerAndJobStatusHandlerService = workerAndJobStatusHandlerService;
    }

    @PostMapping("/fail")
    public void changeJobAndWorkerStatusToFailed(HttpSession session) throws DatabaseException {
        Integer jobId = (Integer) session.getAttribute("jobId");
        if (jobId!=null) {
            try {
                JobEntry jobEntry = jobService.findById(jobId);
                workerAndJobStatusHandlerService.handleCancelledJob(jobEntry);
            } finally {
                session.removeAttribute("jobId");
            }
        }
    }

}























