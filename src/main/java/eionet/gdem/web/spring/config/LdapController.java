package eionet.gdem.web.spring.config;

import eionet.gdem.Properties;
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

        @Autowired
        MessageSource messageSource;

        @GetMapping
        public String edit(Model model) {
            LdapForm form = new LdapForm();
            form.setUrl(Properties.ldapUrl);
            form.setContext(Properties.ldapContext);
            form.setAttrUid(Properties.ldapAttrUid);
            form.setUserDir(Properties.ldapUserDir);
            model.addAttribute("form", form);
            return "config/ldap";
        }

        @PostMapping
        public String editSubmit(@ModelAttribute BaseXForm updatedModel, RedirectAttributes redirectAttributes, HttpSession session) {

            return "redirect:/config/ldap";
        }

    }
