package eionet.gdem.web.spring;

import eionet.gdem.services.MessageService;
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
public class ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);

    private MessageService messageService;

    @Autowired
    public ErrorController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(path = "/error")
    public String error(HttpServletRequest request, Model model) {
        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String url = (String) request.getAttribute("javax.servlet.forward.request_uri");
        String message = "";
        switch (status) {
            case 404:
                message = messageService.getMessage("label.error.404", url);
                break;
            case 500:
                message = messageService.getMessage("label.error.500");
                break;
            default:
                message = "Unknown error";
                break;
        }
        model.addAttribute("status", status);
        model.addAttribute("reason", message);
        return "Error";
    }


}
