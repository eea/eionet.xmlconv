package eionet.gdem.web.spring.login;

import eionet.acl.AppUser;
import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    public static final String AFTER_LOGIN_ATTR_NAME = "afterLogin";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private MessageService messageService;

    public LoginController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String getForm(Model model, HttpSession session) {

        return "";
    }

    @GetMapping("/afterLogin")
    public String afterLogin(Model model, HttpServletRequest httpServletRequest) {
        AppUser aclUser = SecurityUtil.getUser(httpServletRequest, Constants.USER_ATT);

        // remove session data, that contains permission related attributes
        QAScriptListLoader.loadPermissions(httpServletRequest);
        StylesheetListLoader.loadPermissions(httpServletRequest);

        String afterLogin = (String) httpServletRequest.getSession().getAttribute(AFTER_LOGIN_ATTR_NAME);

        if (afterLogin != null && !afterLogin.toLowerCase().contains("/tiles/layout.jsp")) {
            return "redirect:" + afterLogin;
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(Model model, HttpServletRequest httpServletRequest) throws XMLConvException {
        /*httpServletRequest.setCharacterEncoding("UTF-8");*/
        AppUser user = SecurityUtil.getUser(httpServletRequest, Constants.USER_ATT);
        QAScriptListLoader.clearPermissions(httpServletRequest);
        httpServletRequest.getSession().invalidate();

        String logoutURL = SecurityUtil.getLogoutURL(httpServletRequest);
        if (logoutURL != null) {
            return "redirect:" + logoutURL;
        }
        return "redirect:/";
    }

    @GetMapping("/local")
    public String local(Model model) {
        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        LoginForm form = new LoginForm();
        model.addAttribute("form", form);
        return "/login/login";
    }

    @PostMapping("/local")
    public String localSubmit(@ModelAttribute LoginForm form, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession) {

        SpringMessages errors = new SpringMessages();

        String username = form.getUsername();
        String password = form.getPassword();

        if (Utils.isNullStr(username) || Utils.isNullStr(password)) {
            return "redirect:/login/login";
        }

        try {
                AppUser aclUser = new AppUser();
                if (!Utils.isNullStr(username)) {
                    aclUser.authenticate(username, password);
                }

                httpSession.setAttribute(Constants.USER_ATT, aclUser);
                httpSession.setAttribute(Constants.TICKET_ATT, Utils.getEncodedAuthentication(username, password));

            LOGGER.debug("Success login");
            httpSession.setAttribute("user", username);
            return "redirect:/";
        } catch (SignOnException | IOException e) {
            LOGGER.error("Cannot login", e);
            form.setPassword("");
            errors.add(messageService.getMessage("label.login.error.invalid"));
        }

        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);

        // go back to the previous page
        String afterLogin = (String) httpSession.getAttribute("afterLogin");
        if (afterLogin != null && !afterLogin.toLowerCase().contains("/tiles/layout.jsp")) {
            return "redirect:" + afterLogin;
        }

        return "redirect:/";
    }

}
