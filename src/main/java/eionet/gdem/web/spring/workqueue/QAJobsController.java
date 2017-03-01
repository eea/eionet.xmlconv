package eionet.gdem.web.spring.workqueue;

import eionet.gdem.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 *
 */
@Controller
@RequestMapping("/workqueue")
public class QAJobsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAJobsController.class);
    private MessageService messageService;

    @Autowired
    public QAJobsController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String list(Model model) {
        return "/workqueue.jsp";
    }



}
