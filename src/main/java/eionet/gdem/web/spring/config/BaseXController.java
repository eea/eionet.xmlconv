package eionet.gdem.web.spring.config;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.security.model.WebUser;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.spring.SpringMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 *
 *
 */
@Controller
@RequestMapping("/config/basex")
public class BaseXController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseXController.class);
    private MessageService messageService;

    @Autowired
    public BaseXController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String edit(Model model) {
        BaseXForm form = new BaseXForm();
        form.setHost(Properties.basexServerHost);
        form.setPort(Properties.basexServerPort);
        form.setPassword(Properties.basexServerPassword);
        form.setUser(Properties.basexServerUser);
        model.addAttribute("form", form);
        return "/config/basex";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute BaseXForm form, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String host = form.getHost();
        String port = form.getPort();
        String basexUser = form.getUser();
        String password = form.getPassword();

        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                errors.add(messageService.getMessage("label.autorization.config.update", null, LocaleContextHolder.getLocale()));
                return "redirect:/config/basex";
            } else {
                DcmProperties dcmProp = new DcmProperties();
                dcmProp.setBasexParams(host, port, basexUser, password);
            }
        } catch (DCMException e) {
            errors.add(messageService.getMessage("label.exception.unknown", null, LocaleContextHolder.getLocale()));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/config/basex";
        } catch (SignOnException e) {
            errors.add(messageService.getMessage("label.autorization.config.update", null, LocaleContextHolder.getLocale()));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/config/basex";
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/config/basex";
    }
}
