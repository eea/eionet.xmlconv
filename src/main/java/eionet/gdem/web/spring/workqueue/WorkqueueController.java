package eionet.gdem.web.spring.workqueue;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.services.MessageService;
import eionet.gdem.services.impl.JobEntryAndJobHistoryEntriesService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.ThymeleafUtils;
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

        //Setup headerVariables
        model = ThymeleafUtils.setUpTitleAndLogin(model, Properties.getStringProperty("label.workqueue.title"), httpServletRequest);
        //Setup breadcrumbs
        model = ThymeleafUtils.setUpBreadCrumbsForWorkqueuePage(model, Properties.getStringProperty("label.workqueue.breadcrumb"));

        return "workqueue/workqueue";
    }

    //TODO delete this
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

    //TODO delete this
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
}
