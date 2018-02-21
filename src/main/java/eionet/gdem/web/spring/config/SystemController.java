package eionet.gdem.web.spring.config;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.spring.SpringMessage;
import eionet.gdem.web.spring.SpringMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 */
@Controller
@RequestMapping("/config/system")
public class SystemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);
    private MessageService messageService;

    @Autowired
    public SystemController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String edit(@ModelAttribute("form") SystemForm form, HttpServletRequest httpServletRequest) {
        String user = (String) httpServletRequest.getSession().getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }
        form.setCmdXGawk(Properties.xgawkCommand);
        form.setQaTimeout(Properties.qaTimeout);
        return "/config/system";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute("form") SystemForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {
        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String cmdXGawk = form.getCmdXGawk();
        Long qaTimeout = form.getQaTimeout();
        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                throw new AccessDeniedException(messageService.getMessage(("label.autorization.config.update")));
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        new SystemFormValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/config/system";
        }

        try {
            DcmProperties dcmProp = new DcmProperties();
            dcmProp.setSystemParams(qaTimeout, cmdXGawk);
        } catch (DCMException e) {
            throw new RuntimeException("Unknown error: " + messageService.getMessage(e.getErrorCode()));
        }

        messages.add(messageService.getMessage(("label.editParam.system.saved")));
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/config/system";
    }

}