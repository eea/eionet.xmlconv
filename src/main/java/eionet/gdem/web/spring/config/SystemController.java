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
@RequestMapping("/config/system")
public class SystemController {

    @Autowired
    MessageSource messageSource;

    @GetMapping
    public String edit(Model model) {
        SystemForm form = new SystemForm();
        form.setCmdXGawk(Properties.xgawkCommand);
        form.setQaTimeout(Properties.qaTimeout);
        model.addAttribute("form", form);
        return "config/system";
    }

    @PostMapping
    public String editSubmit(@ModelAttribute BaseXForm updatedModel, RedirectAttributes redirectAttributes, HttpSession session) {

        return "redirect:/web/config/system";
    }

}