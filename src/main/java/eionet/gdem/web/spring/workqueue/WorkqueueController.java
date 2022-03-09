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
    @Autowired
    public WorkqueueController(){
    }

    @GetMapping
    public String list(Model model, HttpServletRequest httpServletRequest) {

        //Setup headerVariables
        model = ThymeleafUtils.setUpTitleAndLogin(model, Properties.getStringProperty("label.workqueue.title"), httpServletRequest);
        //Setup breadcrumbs
        model = ThymeleafUtils.setUpBreadCrumbsForWorkqueuePage(model, Properties.getStringProperty("label.workqueue.breadcrumb"));

        return "workqueue/workqueue";
    }
}
