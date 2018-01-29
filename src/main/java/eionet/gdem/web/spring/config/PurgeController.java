package eionet.gdem.web.spring.config;

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
@RequestMapping("/config/purge")
public class PurgeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurgeController.class);
    private MessageService messageService;

    @Autowired
    public PurgeController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String edit(Model model) {
        PurgeForm form = new PurgeForm();
        model.addAttribute("form", form);
        return "/config/purge";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute PurgeForm form, HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        Integer nofDays = Integer.parseInt(form.getNofDays());
        String user = (String) session.getAttribute("user");
        int deleted = 0;

        try {

            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_QUERIES_PATH, "u")) {
                errors.add(messageService.getMessage("label.autorization.config.purge"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/config/purge";
            }
            if (nofDays == null || "".equals(nofDays.toString()) || nofDays <= 0) {
                errors.add(messageService.getMessage("label.config.purge.validation"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/config/purge";
            }
            BackupManager bm = new BackupManager();
            deleted = bm.purgeBackup(nofDays);

        } catch (SignOnException | DCMException e) {
            LOGGER.error("SystemAction error", e);
            errors.add(messageService.getMessage(e.getMessage()));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/config/purge";
        }
        String[] numbers = {String.valueOf(nofDays.intValue()), String.valueOf(deleted)};
        messages.add(messageService.getMessage("label.config.purge.successful", numbers));

        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/config/purge";
    }

}