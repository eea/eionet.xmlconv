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
    public String edit(@ModelAttribute("form") DatabaseForm form, HttpServletRequest httpServletRequest) {
        String user = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }
        form.setUrl(Properties.dbUrl);
        form.setUser(Properties.dbUser);
        form.setPassword(Properties.dbPwd);
        return "/WEB-INF/view/config/database.html";
    }

    @PostMapping
    public String submit(@ModelAttribute("form") DatabaseForm form,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages messages = new SpringMessages();

        String dbUrl = form.getUrl();
        String dbUser = form.getUser();
        String dbPwd = form.getPassword();
        String user = (String) session.getAttribute("user");

        new DatabaseFormValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/WEB-INF/view/config/database.html";
        }

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
            }
        } catch (SignOnException e1) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        try {
            DbTest dbTest = new DbTest();
            dbTest.tstDbParams(dbUrl, dbUser, dbPwd);
            DcmProperties dcmProp = new DcmProperties();
            dcmProp.setDbParams(dbUrl, dbUser, dbPwd);
        } catch (SQLException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        } catch (DCMException e) {
            throw new RuntimeException(messageService.getMessage(e.getErrorCode()));
        }
        messages.add(messageService.getMessage("label.editParam.db.saved"));
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/config/database";
    }
}