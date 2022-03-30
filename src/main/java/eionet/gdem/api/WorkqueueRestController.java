package eionet.gdem.api;

import com.opencsv.CSVWriter;
import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.web.spring.workqueue.*;
import org.springframework.security.access.AccessDeniedException;
import eionet.gdem.services.impl.JobEntryAndJobHistoryEntriesService;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

    @GetMapping("/getWorkqueuePageInfo")
    public WorkqueuePageInfo getWorkqueuePageInfo(HttpSession session, @RequestParam(value = "page") Integer page, @RequestParam(value = "itemsPerPage") Integer itemsPerPage,
                                                  @RequestParam(value = "sortBy") String sortBy, @RequestParam(value = "sortDesc") Boolean sortDesc,
                                                  @RequestParam(value = "searchParam") String searchParam, @RequestParam(value = "keyword") String keyword,
                                                  @RequestParam(value = "statuses") String[] searchedStatuses) {
        String userName = null;
        if(session != null) {
            if (session.getAttribute("user") != null) {
                userName = session.getAttribute("user").toString();
            }
        }
        boolean wqdPrm = false;
        boolean wquPrm = false;
        boolean wqvPrm = false;
        boolean logvPrm = false;
        try {
            if (userName != null) {
                wqdPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_WQ_PATH, "d");
                wquPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_WQ_PATH, "u");
                wqvPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_WQ_PATH, "v");
                logvPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_LOGFILE_PATH, "v");
            }
        } catch (SignOnException e) {
            LOGGER.error("Error with permissions. Exception message: " + e.getMessage());
        }

        WorkqueuePermissions permissions = new WorkqueuePermissions();
        permissions.setWqdPrm(wqdPrm);
        permissions.setWquPrm(wquPrm);
        permissions.setWqvPrm(wqvPrm);
        permissions.setLogvPrm(logvPrm);
        EntriesForPageObject entriesForPageObject = jobEntryAndJobHistoryEntriesService.getSortedJobsForPage(page, itemsPerPage, sortBy, sortDesc, searchParam, keyword, searchedStatuses);
        WorkqueuePageInfo workqueuePageInfo = new WorkqueuePageInfo(entriesForPageObject.getJobMetadataEntriesForPage(), entriesForPageObject.getTotalNumberOfJobEntries(), permissions, userName);
        return workqueuePageInfo;
    }

    @RequestMapping(value = "restart", method = RequestMethod.POST)
    public String restart(HttpSession session, @RequestBody JobMetadata[] selectedJobs) {
        //check permissions
        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "u")) {
                throw new AccessDeniedException("Access denied for qa job restart action");
            }
        } catch (SignOnException e) {
            throw new RuntimeException(Properties.getMessage("label.exception.unknown"));
        }

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
    public String delete(HttpSession session, @RequestBody JobMetadata[] selectedJobs) {
        //check permissions
        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "u")) {
                throw new AccessDeniedException("Access denied for qa job delete action");
            }
        } catch (SignOnException e) {
            throw new RuntimeException(Properties.getMessage("label.exception.unknown"));
        }

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

    @RequestMapping(value = "exportToCsv", method = RequestMethod.POST)
    public void exportToCsv(HttpServletRequest request, HttpServletResponse response, @RequestBody JobMetadata[] jobEntries) throws IOException {
        List<String[]> csvData = new ArrayList<>();
        String[] header = {"Job ID", "Document URL", "XQuery script", "Job Result", "Status", "Started at", "Instance", "Duration", "Job type", "Worker"};
        csvData.add(header);
        for(JobMetadata entry: jobEntries){
            String[] row = {entry.getJobId(), entry.getUrl(), entry.getScript_file(), (entry.getResult_file() != null) ? entry.getResult_file() : "*** Not ready ***",
                    entry.getStatusName(), entry.getTimestamp(), (entry.getInstance()!= null) ? entry.getInstance() : "",
                    (entry.getDurationInProgress() != null) ? entry.getDurationInProgress() : "", (entry.getJobType() != null) ? entry.getJobType() : "",
                    (entry.getJobExecutorName() != null) ? entry.getJobExecutorName() : ""};
            csvData.add(row);
        }

        // default all fields are enclosed in double quotes
        // default separator is a comma
        // init stream writer
        OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        CSVWriter writer = new CSVWriter(osw);
        writer.writeAll(csvData);

        // set response content type
        response.setContentType("text/csv; charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment; filename=QA jobs workqueue.csv");

        // flush and close stream writer
        writer.flush();
        osw.flush();
        writer.close();
        osw.close();
    }

    @GetMapping(value ="/getJobDetails/{jobId}")
    @ResponseBody
    public List<JobHistoryMetadata> getJobDetails(@PathVariable String jobId) throws DatabaseException {
        return jobEntryAndJobHistoryEntriesService.getJobHistoryMetadata(jobId);
    }
}
