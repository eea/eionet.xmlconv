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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String edit(Model model) {
        SystemForm form = new SystemForm();
        form.setCmdXGawk(Properties.xgawkCommand);
        form.setQaTimeout(Properties.qaTimeout);
        model.addAttribute("form", form);
        return "/config/system";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute SystemForm form, RedirectAttributes redirectAttributes, HttpSession session) {
        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String cmdXGawk = form.getCmdXGawk();
        Long qaTimeout = form.getQaTimeout();
        String user = (String) session.getAttribute("user");

        try {

            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                errors.add(messageService.getMessage(("label.autorization.config.update")));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/config/system";
            }
            if (qaTimeout == null || "".equals(qaTimeout.toString()) || qaTimeout <= 0) {
                errors.add(messageService.getMessage(("label.config.system.qatimeout.validation")));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/config/system";
            }

            DcmProperties dcmProp = new DcmProperties();

            dcmProp.setSystemParams(qaTimeout, cmdXGawk);

        } catch (SignOnException | DCMException e) {
            LOGGER.error("SystemAction error", e);
            errors.add(messageService.getMessage(e.getMessage()));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/config/system";
        }

        messages.add(messageService.getMessage(("label.editParam.system.saved")));
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/config/system";
    }

}