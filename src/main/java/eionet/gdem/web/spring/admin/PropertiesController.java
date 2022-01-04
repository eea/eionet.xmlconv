package eionet.gdem.web.spring.admin;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.ThymeleafUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.file.AccessDeniedException;

@Controller
@RequestMapping("/admin/viewAndEditProperties")
public class PropertiesController {

    private MessageService messageService;

    @Autowired
    public PropertiesController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String view(Model model, HttpSession session, HttpServletRequest request) throws SignOnException, AccessDeniedException {
        //Setup headerVariables
        model = ThymeleafUtils.setUpTitleAndLogin(model, Properties.getStringProperty("label.admin.properties"), request);
        //Setup breadcrumbs
        model = ThymeleafUtils.setUpBreadCrumbsForAdminPages(model, Properties.getStringProperty("label.admin.properties"));

        String user = (String) session.getAttribute("user");
        if (user==null || (user!=null && !SecurityUtil.hasPerm(user, "/" + Constants.ACL_ADMIN_PATH, "u"))) {
            model.addAttribute("reason", HttpStatus.UNAUTHORIZED.value() + " - " + messageService.getMessage("label.authorization.properties.view"));
            return "error/error";
        }
        return "/old/admin/html/viewAndEditProperties";
    }
}
