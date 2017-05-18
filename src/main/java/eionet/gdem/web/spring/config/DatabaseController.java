package eionet.gdem.web.spring.config;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dcm.conf.DbTest;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.spring.SpringMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

/**
 *
 */
@Controller
@RequestMapping("/config/database")
public class DatabaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseController.class);
    private MessageService messageService;

    @Autowired
    public DatabaseController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String edit(Model model) {
        DatabaseForm form = new DatabaseForm();
        form.setUrl(Properties.dbUrl);
        form.setUser(Properties.dbUser);
        form.setPassword(Properties.dbPwd);
        model.addAttribute("form", form);
        return "/config/database";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute DatabaseForm updatedModel, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String dbUrl = updatedModel.getUrl();
        String dbUser = updatedModel.getUser();
        String dbPwd = updatedModel.getPassword();
        String user = (String) session.getAttribute("user");

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                errors.add(messageService.getMessage("label.autorization.config.update", null, LocaleContextHolder.getLocale()));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/config/database";
            } else {
                if (dbUrl == null || dbUrl.equals("")) {
                    errors.add(messageService.getMessage("label.config.ldap.url.validation", null, LocaleContextHolder.getLocale()));
                    redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                    return "redirect:/config/database";
                } else {
                    DbTest dbTest = new DbTest();
                    dbTest.tstDbParams(dbUrl, dbUser, dbPwd);

                    DcmProperties dcmProp = new DcmProperties();
                    dcmProp.setDbParams(dbUrl, dbUser, dbPwd);
                }
            }
        } catch (SignOnException | SQLException | DCMException e) {
            errors.add(messageService.getMessage("label.autorization.config.update", null, LocaleContextHolder.getLocale()));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/config/database";
        }
        messages.add(messageService.getMessage("label.editParam.db.saved", null, LocaleContextHolder.getLocale()));
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/config/database";
    }
}