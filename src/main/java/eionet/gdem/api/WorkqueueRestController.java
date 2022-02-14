package eionet.gdem.api;

import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.Entities.PropertiesEntry;
import eionet.gdem.services.impl.JobEntryAndJobHistoryEntriesService;
import eionet.gdem.web.spring.workqueue.JobMetadata;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "restart", method = RequestMethod.POST)
    public String restart(HttpServletRequest request, HttpServletResponse response, @RequestBody JobMetadata[] selectedJobs) {
        //check permissions
        /*
        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "u")) {
                throw new AccessDeniedException("Access denied for qa job restart action");
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

         */
        WorkqueueManager workqueueManager = new WorkqueueManager();
        try {
            String[] jobIds = new String[selectedJobs.length];
            for(int i=0; i<selectedJobs.length; i++){
                jobIds[i] = selectedJobs[i].getJobId();
            }
            workqueueManager.restartJobs(jobIds);
        } catch (XMLConvException e) {
            throw new RuntimeException("Could not restart jobs! " + e.getMessage());
        }
        return Properties.getMessage("label.workqueue.jobrestarted");
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String delete(HttpServletRequest request, HttpServletResponse response, @RequestBody JobMetadata[] selectedJobs) {
        //check permissions
        /*
        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "u")) {
                throw new AccessDeniedException("Access denied for qa job restart action");
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }
         */
        WorkqueueManager workqueueManager = new WorkqueueManager();
        try {
            String[] jobIds = new String[selectedJobs.length];
            for(int i=0; i<selectedJobs.length; i++){
                jobIds[i] = selectedJobs[i].getJobId();
            }
            workqueueManager.deleteJobs(jobIds, false);
        } catch (XMLConvException e) {
            throw new RuntimeException("Could not delete jobs! " + e.getMessage());
        }
        return Properties.getMessage("label.workqueue.jobdeleted");
    }
}
