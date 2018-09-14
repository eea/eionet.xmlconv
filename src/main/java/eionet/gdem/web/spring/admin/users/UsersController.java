package eionet.gdem.web.spring.admin.users;

import eionet.acl.SignOnException;
import eionet.gdem.security.acl.AccessListService;
import eionet.gdem.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public String list(Model model) throws SignOnException, SQLException {

        HashMap<String, List<String>> groups = accessListService.getGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("form", new UsersForm());
        return "/admin/users/list";
    }

    @PostMapping
    public String submit(UsersForm form) throws SignOnException {
        List<Group> groups = form.getGroups();
        accessListService.writeGroups(groups);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit")
    public String edit(Model model) throws SQLException, SignOnException {
        HashMap<String, List<String>> groups = accessListService.getGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("form", new UsersForm());
        return "/admin/users/edit";
    }


}
