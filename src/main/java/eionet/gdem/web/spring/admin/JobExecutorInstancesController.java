package eionet.gdem.web.spring.admin;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/jobExecutorInstancesView")
public class JobExecutorInstancesController {

    private MessageService messageService;

    private JobExecutorService jobExecutorService;

    private JobExecutorHistoryService jobExecutorHistoryService;

    @Autowired
    public JobExecutorInstancesController(MessageService messageService, JobExecutorService jobExecutorService, JobExecutorHistoryService jobExecutorHistoryService) {
        this.messageService = messageService;
        this.jobExecutorService = jobExecutorService;
        this.jobExecutorHistoryService = jobExecutorHistoryService;
    }

    @GetMapping
    public String view(Model model, HttpSession httpSession) throws SignOnException, DatabaseException {
        String user = (String) httpSession.getAttribute("user");
        if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_ADMIN_PATH, "u")) {
            throw new AccessDeniedException(messageService.getMessage("label.authorization.jobExecutorInstances.view"));
        }

        List<JobExecutor> instances = jobExecutorService.listJobExecutor();
        model.addAttribute("instances", instances);
        return "/admin/jobExecutorInstancesView";
    }

    @PostMapping(value ="/getJobExecutorDetails/{containerId}")
    @ResponseBody
    public List<JobExecutorHistory> getJobExecutorHistoryEntriesById(@PathVariable String containerId) throws DatabaseException {
        return jobExecutorHistoryService.getJobExecutorHistoryEntriesById(containerId);
    }
}
