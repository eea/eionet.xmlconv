package eionet.gdem.web.spring.config;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.dcm.conf.LdapTest;
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
@RequestMapping("/config/ldap")
public class LdapController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapController.class);
    private MessageService messageService;

    @Autowired
    public LdapController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String edit(@ModelAttribute("form") LdapForm form, HttpServletRequest httpServletRequest) {
        String user = (String) httpServletRequest.getSession().getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }
        form.setUrl(Properties.ldapUrl);
        form.setContext(Properties.ldapContext);
        form.setAttrUid(Properties.ldapAttrUid);
        form.setUserDir(Properties.ldapUserDir);
        return "/config/ldap";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute("form") LdapForm form,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages messages = new SpringMessages();

        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        String url = form.getUrl();
        String context = form.getContext();
        String userDir = form.getUserDir();
        String attrUid = form.getAttrUid();

        new LdapFormValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/config/ldap";
        }

        try {
            LdapTest lt = new LdapTest(url);
            if (!lt.test()) {
                bindingResult.reject("label.editParam.ldap.testFailed");
                return "/config/ldap";
            }

            DcmProperties dcmProp = new DcmProperties();
            dcmProp.setLdapParams(url, context, userDir, attrUid);
        } catch (DCMException e) {
            throw new RuntimeException("Ldap parameters saving error: " + messageService.getMessage(e.getErrorCode()));
        }
        messages.add(messageService.getMessage("label.editParam.ldap.saved"));
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/config/ldap";
    }

}
