package eionet.gdem.web.spring.config;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dcm.conf.DbTest;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.spring.SpringMessages;
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

    @Autowired
    MessageSource messageSource;

    @GetMapping
    public String edit(Model model) {
        DatabaseForm form = new DatabaseForm();
        form.setUrl(Properties.dbUrl);
        form.setUser(Properties.dbUser);
        form.setPassword(Properties.dbPwd);
        model.addAttribute("form", form);
        return "config/database";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute DatabaseForm updatedModel, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages errors = new SpringMessages();
        SpringMessages success = new SpringMessages();

        String dbUrl = (String) updatedModel.getUrl();
        String dbUser = (String) updatedModel.getUser();
        String dbPwd = (String) updatedModel.getPassword();
        String user = (String) session.getAttribute("user");

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                errors.add(messageSource.getMessage("label.autorization.config.update", null, LocaleContextHolder.getLocale()));
            } else {
                if (dbUrl == null || dbUrl.equals("")) {
                    errors.add(messageSource.getMessage("label.config.ldap.url.validation", null, LocaleContextHolder.getLocale()));
                } else {
                    DbTest dbTest = new DbTest();
                    dbTest.tstDbParams(dbUrl, dbUser, dbPwd);

                    DcmProperties dcmProp = new DcmProperties();

                    dcmProp.setDbParams(dbUrl, dbUser, dbPwd);
                    success.add(messageSource.getMessage("label.editParam.db.saved", null, LocaleContextHolder.getLocale()));
                }

            }
            //TODO FIX messages
        } catch (SignOnException e) {
            errors.add(messageSource.getMessage("label.autorization.config.update", null, LocaleContextHolder.getLocale()));
        } catch (DCMException e) {
            errors.add(messageSource.getMessage("label.autorization.config.update", null, LocaleContextHolder.getLocale()));
        } catch (SQLException e) {
            errors.add(messageSource.getMessage("label.autorization.config.update", null, LocaleContextHolder.getLocale()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, success);
        return "redirect:/web/config/database";
    }

}