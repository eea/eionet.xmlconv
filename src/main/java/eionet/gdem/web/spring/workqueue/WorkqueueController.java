package eionet.gdem.web.spring.workqueue;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.business.WorkqueueManager;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.spring.SpringMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    public WorkqueueController(MessageService messageService) {
        this.messageService = messageService;
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
            eionet.gdem.services.db.dao.IXQJobDao jobDao = GDEMServices.getDaoService().getXQJobDao();
            list = jobDao.getJobData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String tmpFolder = Constants.TMP_FOLDER;
        String queriesFolder = Constants.QUERIES_FOLDER;


        List<JobMetadata> jobsList = new ArrayList<>();

        // XXX: Refactor soon
        eionet.gdem.services.db.dao.IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
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

            job.setJobId(jobId);
            /*job.setUrl(url);*/
            job.setFileName(xqLongFileName);
            job.setScriptFile(xqFile);

            job.setStatus(status);
            job.setTimestamp(timeStamp);
            job.setScriptId(xqStringID);
            job.setInstance(instance);
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


            if (status == Constants.XQ_RECEIVED || status == Constants.XQ_DOWNLOADING_SRC || status == Constants.XQ_PROCESSING)
                resultFile = null;
            job.setResultFile(resultFile);

            //TODO Status name, maybe better to move to some Java common class
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

            job.setStatusName(statusName);
            if (url.indexOf(Constants.GETSOURCE_URL) > 0 && url.indexOf(Constants.SOURCE_URL_PARAM) > 0) {
                int idx = url.indexOf(Constants.SOURCE_URL_PARAM);
                url = url.substring(idx + Constants.SOURCE_URL_PARAM.length() + 1);
            }
            String urlName = (url.length() > Constants.URL_TEXT_LEN ? url.substring(0, Constants.URL_TEXT_LEN) + "..." : url);
            job.setUrl(urlName);

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
        return "/workqueue";
    }

    @PostMapping("/actions")
    public String actions(@ModelAttribute WorkqueueForm form, @RequestParam String action, HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        WorkqueueManager workqueueManager = new WorkqueueManager();

        String user = (String) session.getAttribute("user");

        List<String> jobs = form.getJobs();

        if ("delete".equals(action)) {
            try {
                if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "d")) {
                    LOGGER.error("Access denied for qa job delete action");
                    errors.add("You don't have permissions to delete jobs!");
                    return "redirect:/workqueue";
                }
            } catch (SignOnException e) {
                LOGGER.error("Error while reading permissions", e);
                errors.add("Error while reading permissions");
                return "redirect:/workqueue";
            }

            try {
                workqueueManager.deleteJobs(jobs.toArray(new String[0]));
            } catch (XMLConvException e) {
                LOGGER.error("Could not delete jobs!" + e.getMessage());
                errors.add("Cannot delete job: " + e.toString());
            }

        } else if ("restart".equals(action)) {
            try {
                if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "u")) {
                    LOGGER.error("Access denied for qa job restart action");
                    errors.add("You don't have permissions to restart the jobs!");
                    return "redirect:/workqueue";
                }
            } catch (SignOnException e) {
                LOGGER.error("Error while reading permissions", e);
                errors.add("Error while reading permissions");
                return "redirect:/workqueue";
            }

            try {
                workqueueManager.restartJobs(jobs.toArray(new String[0]));
            } catch (XMLConvException e) {
                LOGGER.error("Could not restart jobs!" + e.getMessage());
                errors.add("Error while reading permissions");
                return "redirect:/workqueue";
            }
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/workqueue";
    }

}
