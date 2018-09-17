package eionet.gdem.web.spring.admin.users;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.security.acl.AccessListService;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.*;

@Controller
@RequestMapping("/admin/users")
public class UsersController {

    private MessageService messageService;
    private AccessListService accessListService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    public UsersController(MessageService messageService, AccessListService accessListService) {
        this.messageService = messageService;
        this.accessListService = accessListService;
    }

    @GetMapping
    public String list(Model model, HttpSession httpSession) throws SignOnException, SQLException {

        String user = (String) httpSession.getAttribute("user");
        if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
            throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
        }

        HashMap<String, List<String>> groups = accessListService.getGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("form", new UsersForm());
        return "/admin/users/list";
    }

    @PostMapping
    public String submit(UsersForm form, HttpSession httpSession) throws SignOnException {
        String user = (String) httpSession.getAttribute("user");
        if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
            throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
        }

        List<Group> groups = form.getGroups();
        accessListService.writeGroups(groups);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit")
    public String edit(Model model, HttpSession httpSession) throws SQLException, SignOnException {

        String user = (String) httpSession.getAttribute("user");
        if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_CONFIG_PATH, "u")) {
            throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
        }

        HashMap<String, List<String>> groups = accessListService.getGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("form", new UsersForm());
        return "/admin/users/edit";
    }


}
