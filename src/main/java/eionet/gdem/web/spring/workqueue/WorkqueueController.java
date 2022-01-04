package eionet.gdem.web.spring.workqueue;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.MessageService;
import eionet.gdem.services.impl.JobEntryAndJobHistoryEntriesService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
@Controller
@RequestMapping("/workqueue")
public class WorkqueueController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkqueueController.class);
    private MessageService messageService;
    public JobEntryAndJobHistoryEntriesService jobEntryAndJobHistoryEntriesService;

    @Autowired
    public WorkqueueController(MessageService messageService, JobEntryAndJobHistoryEntriesService jobEntryAndJobHistoryEntriesService) {
        this.messageService = messageService;
        this.jobEntryAndJobHistoryEntriesService = jobEntryAndJobHistoryEntriesService;
    }

    @GetMapping
    public String list(Model model, HttpServletRequest httpServletRequest) {

        WorkqueueForm form = new WorkqueueForm();


        String userName = (String) httpServletRequest.getSession().getAttribute("user");
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
            LOGGER.error("Error");
        }

        String[][] list = null;
        try {
            IXQJobDao jobDao = GDEMServices.getDaoService().getXQJobDao();
            list = jobDao.getJobData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String tmpFolder = Constants.TMP_FOLDER;
        String queriesFolder = Constants.QUERIES_FOLDER;


        List<JobMetadata> jobsList = new ArrayList<>();

        // XXX: Refactor soon
        IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
        for (int i = 0; i < list.length; i++) {
            JobMetadata job = new JobMetadata();
            String jobId = list[i][0];
            String url = list[i][1];
            String xqLongFileName = list[i][2];
            String xqFile = list[i][2].substring(list[i][2].lastIndexOf(File.separatorChar) + 1);
            String resultFile = list[i][3].substring(list[i][3].lastIndexOf(File.separatorChar) + 1);
            int status = Integer.parseInt(list[i][4]);
            String timeStamp = list[i][5];
            String xqStringID = list[i][6];
            String instance = list[i][7];
            String durationMs = list[i][8];
            String jobType = list[i][9];
            String jobExecutorName = list[i][10];

            job.setJobId(jobId);
            job.setFileName(xqLongFileName);
            job.setScriptFile(xqFile);

            job.setStatus(status);
            job.setTimestamp(timeStamp);
            job.setScriptId(xqStringID);
            job.setInstance(instance);
            job.setJobType(jobType);
            job.setJobExecutorName(jobExecutorName);
            int xqID = 0;
            String scriptType = "";
            try {
                xqID = Integer.parseInt(xqStringID);
                java.util.HashMap query = queryDao.getQueryInfo(xqStringID);
                if (query != null) {
                    scriptType = (String) query.get("script_type");
                }
            } catch (NumberFormatException n) {
                xqID = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            job.setScriptType(scriptType);

            String xqFileURL = "";
            String xqText = "Show script";
            if (xqID == Constants.JOB_VALIDATION) {
                xqText = "Show XML Schema";
                xqFileURL = xqLongFileName;
            } else if (xqID == Constants.JOB_FROMSTRING) {
                xqFileURL = tmpFolder + xqFile;
            } else {
                xqFileURL = queriesFolder + xqFile;
            }


            if (status == Constants.XQ_RECEIVED || status == Constants.XQ_DOWNLOADING_SRC || status == Constants.XQ_PROCESSING ||
                    status == Constants.XQ_INTERRUPTED || status == Constants.CANCELLED_BY_USER || status == Constants.DELETED)
                resultFile = null;
            job.setResultFile(resultFile);

            String statusName = "-- Unknown --";

            if (status == Constants.XQ_RECEIVED)
                statusName = "JOB RECEIVED";
            if (status == Constants.XQ_DOWNLOADING_SRC)
                statusName = "DOWNLOADING SOURCE";
            if (status == Constants.XQ_PROCESSING)
                statusName = "PROCESSING";
            if (status == Constants.XQ_READY)
                statusName = "READY";
            if (status == Constants.XQ_FATAL_ERR)
                statusName = "FATAL ERROR";
            if (status == Constants.XQ_LIGHT_ERR)
                statusName = "RECOVERABLE ERROR";
            if (status == Constants.XQ_INTERRUPTED)
                statusName = "INTERRUPTED";
            if (status == Constants.CANCELLED_BY_USER)
                statusName = "CANCELLED BY USER";
            if (status == Constants.DELETED)
                statusName = "DELETED";

            job.setStatusName(statusName);
            if (url.indexOf(Constants.GETSOURCE_URL) > 0 && url.indexOf(Constants.SOURCE_URL_PARAM) > 0) {
                int idx = url.indexOf(Constants.SOURCE_URL_PARAM);
                url = url.substring(idx + Constants.SOURCE_URL_PARAM.length() + 1);
            }
            job.setUrl(url);
            String urlName = (url.length() > Constants.URL_TEXT_LEN ? url.substring(0, Constants.URL_TEXT_LEN) + "..." : url);
            job.setUrlName(urlName);

            //Set duration of job id status is in PROCESSING
            if (durationMs != null) {
                Long duration = Long.parseLong(durationMs);
                job.setDurationInProgress(Utils.createFormatForMs(duration));
            }

            jobsList.add(job);
        }
        WorkqueuePermissions permissions = new WorkqueuePermissions();
        permissions.setWqdPrm(wqdPrm);
        permissions.setWquPrm(wquPrm);
        permissions.setWqvPrm(wqvPrm);
        permissions.setLogvPrm(logvPrm);

        model.addAttribute("permissions", permissions);
        model.addAttribute("jobList", jobsList);
        model.addAttribute("form", form);
        model.addAttribute("username", userName);
        return "/workqueue";
    }

    @PostMapping(params = "delete")
    public String delete(@ModelAttribute("form") WorkqueueForm form, BindingResult bindingResult,
                         HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();

        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "d")) {
                throw new AccessDeniedException("Access denied for qa job delete action");
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        new WorkqueueFormValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            // todo improve this
            return "redirect:/workqueue";
        }

        List<String> jobs = form.getJobs();

        try {
            WorkqueueManager workqueueManager = new WorkqueueManager();
            workqueueManager.deleteJobs(jobs.toArray(new String[0]), false);
            messages.add(messageService.getMessage("label.workqueue.jobdeleted"));
        } catch (XMLConvException e) {
            throw new RuntimeException("Could not delete jobs! " + e.getMessage());
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/workqueue";
    }

    @PostMapping(params = "restart")
    public String restart(@ModelAttribute("form") WorkqueueForm form, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();

        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "u")) {
                throw new AccessDeniedException("Access denied for qa job restart action");
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        new WorkqueueFormValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            // todo improve this
            return "redirect:/workqueue";
        }

        List<String> jobs = form.getJobs();

        try {
            WorkqueueManager workqueueManager = new WorkqueueManager();
            workqueueManager.restartJobs(jobs.toArray(new String[0]));
            messages.add(messageService.getMessage("label.workqueue.jobrestarted"));
        } catch (XMLConvException e) {
            throw new RuntimeException("Could not restart jobs! " + e.getMessage());
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/workqueue";
    }

    @PostMapping(value ="/getJobDetails/{jobId}")
    @ResponseBody
    public JobEntryAndJobHistoryEntriesObject getJobDetails(@PathVariable String jobId) {
        return jobEntryAndJobHistoryEntriesService.getJobEntryAndJobHistoryEntriesOfJob(jobId);
    }
}
