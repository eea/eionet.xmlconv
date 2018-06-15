package eionet.gdem.web.spring.admin;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.web.spring.scripts.BackupManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.spring.SpringMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/admin/purge")
public class PurgeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurgeController.class);
    private MessageService messageService;

    @Autowired
    public PurgeController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String edit(@ModelAttribute("form") PurgeForm purgeForm, HttpServletRequest httpServletRequest) {
        String user = (String) httpServletRequest.getSession().getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }
        return "/admin/purge";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute("form") PurgeForm form,
                             BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();

        Integer nofDays = Integer.parseInt(form.getNofDays());

        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_QUERIES_PATH, "u")) {
                throw new AccessDeniedException(messageService.getMessage("label.autorization.config.purge"));
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        new PurgeFormValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/admin/purge";
        }
        int deleted = 0;
        try {
            BackupManager bm = new BackupManager();
            deleted = bm.purgeBackup(nofDays);
        } catch (DCMException e) {
            throw new RuntimeException("Unknown error: " + messageService.getMessage(e.getErrorCode()));
        }
        String[] numbers = {String.valueOf(nofDays.intValue()), String.valueOf(deleted)};

        messages.add(messageService.getMessage("label.admin.purge.successful", numbers));
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/admin/purge";
    }

}