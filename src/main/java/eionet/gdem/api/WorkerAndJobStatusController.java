package eionet.gdem.api;

import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

//this controller is called in case of GUI runscript, when user closes the window
@RestController
@RequestMapping("/worker")
public class WorkerAndJobStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerAndJobStatusController.class);
    private JobService jobService;
    private WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService;

    @Autowired
    public WorkerAndJobStatusController(JobService jobService, WorkerAndJobStatusHandlerService workerAndJobStatusHandlerService) {
        this.jobService = jobService;
        this.workerAndJobStatusHandlerService = workerAndJobStatusHandlerService;
    }

    @PostMapping("/fail")
    public void changeJobAndWorkerStatusToFailed(HttpSession session) throws DatabaseException {
        Integer jobId = (Integer) session.getAttribute("jobId");
        String user = (String) session.getAttribute("user");
        if (jobId!=null) {
            try {
                JobEntry jobEntry = jobService.findById(jobId);
                InternalSchedulingStatus internalStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                LOGGER.info("Job with id " + jobId + " is cancelled by user " + user);
                workerAndJobStatusHandlerService.handleCancelledJob(jobEntry, SchedulingConstants.WORKER_FAILED, Constants.CANCELLED_BY_USER, internalStatus);
            } finally {
                session.removeAttribute("jobId");
            }
        }
    }

}























