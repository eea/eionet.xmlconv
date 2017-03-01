package eionet.gdem.web.spring.generated;

import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.struts.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 */
@Controller
@RequestMapping("/generatedConversions")
public class GeneratedConversionsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratedConversionsController.class);
    private MessageService messageService;

    @Autowired
    public GeneratedConversionsController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String list(Model model, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();

        try {
            model.addAttribute(StylesheetListLoader.STYLESHEET_GENERATED_LIST_ATTR, StylesheetListLoader.getGeneratedList(request));
        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error getting stylesheet list", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/generatedStylesheetList.jsp";
    }



}
