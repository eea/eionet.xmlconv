package eionet.gdem.api;

import eionet.gdem.services.impl.JobEntryAndJobHistoryEntriesService;
import eionet.gdem.web.spring.workqueue.JobMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/workqueueData")
public class WorkqueueRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkqueueRestController.class);

    public JobEntryAndJobHistoryEntriesService jobEntryAndJobHistoryEntriesService;

    @Autowired
    public WorkqueueRestController(JobEntryAndJobHistoryEntriesService jobEntryAndJobHistoryEntriesService) {
        this.jobEntryAndJobHistoryEntriesService = jobEntryAndJobHistoryEntriesService;
    }

    @GetMapping("/getAllJobs")
    public List<JobMetadata> getAllJobs() {
        try {
            return jobEntryAndJobHistoryEntriesService.retrieveAllJobsWithMetadata();
        } catch (SQLException e) {
            LOGGER.error("Could not retrieve jobs from T_XQJOBS table. Exception message: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
