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
@RequestMapping("/config/ldap")
public class LdapController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapController.class);
    private MessageService messageService;

    @Autowired
    public LdapController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String edit(Model model) {
        LdapForm form = new LdapForm();
        form.setUrl(Properties.ldapUrl);
        form.setContext(Properties.ldapContext);
        form.setAttrUid(Properties.ldapAttrUid);
        form.setUserDir(Properties.ldapUserDir);
        model.addAttribute("form", form);
        return "/config/ldap";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute LdapForm form, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String url = form.getUrl();
        String context = form.getContext();
        String userDir = form.getUserDir();
        String attrUid = form.getAttrUid();

        String user = (String) session.getAttribute("user");

        try {

            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
                errors.add(messageService.getMessage("label.autorization.config.update"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/config/ldap";
            }

            if (url == null || url.equals("")) {
                errors.add(messageService.getMessage("label.config.ldap.url.validation"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/config/ldap";
            }

            LdapTest lt = new LdapTest(url);
            if (!lt.test()) {
                errors.add(messageService.getMessage("label.editParam.ldap.testFailed"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/config/ldap";
            }

            DcmProperties dcmProp = new DcmProperties();

            dcmProp.setLdapParams(url, context, userDir, attrUid);
        } catch (SignOnException | DCMException e) {
            e.printStackTrace();
            LOGGER.error("Ldap parameters saving error", e);
            errors.add(messageService.getMessage(e.getMessage()));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }
        messages.add(messageService.getMessage("label.editParam.ldap.saved"));

        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/config/ldap";
    }

}
