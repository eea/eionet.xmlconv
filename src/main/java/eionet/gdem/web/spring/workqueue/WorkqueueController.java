package eionet.gdem.web.spring.workqueue;

import eionet.gdem.Properties;
import eionet.gdem.utils.ThymeleafUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

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
