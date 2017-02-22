package eionet.gdem.web.spring.config;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.security.model.WebUser;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.spring.SpringMessages;
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

    @Autowired
    MessageSource messageSource;

    @GetMapping
    public String edit(Model model) {
        BaseXForm form = new BaseXForm();
        form.setHost(Properties.basexServerHost);
        form.setPort(Properties.basexServerPort);
        form.setPassword(Properties.basexServerPassword);
        form.setUser(Properties.basexServerUser);
        model.addAttribute("form", form);
        return "config/basex";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute BaseXForm updatedModel, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages errors = new SpringMessages();

        String host = updatedModel.getHost();
        String port = updatedModel.getPort();
        String basexUser = updatedModel.getUser();
        String password = updatedModel.getPassword();


        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                errors.add(messageSource.getMessage("label.autorization.config.update", null, LocaleContextHolder.getLocale()));
            } else {
                DcmProperties dcmProp = new DcmProperties();
                throw new DCMException("test");
            }
            //dcmProp.setBasexParams(host, port, user, password);
        } catch (DCMException e) {
            errors.add(messageSource.getMessage("label.exception.unknown", null, LocaleContextHolder.getLocale()));
        } catch (SignOnException e) {
            errors.add(messageSource.getMessage("label.autorization.config.update", null, LocaleContextHolder.getLocale()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "redirect:/web/config/basex";
    }
}
