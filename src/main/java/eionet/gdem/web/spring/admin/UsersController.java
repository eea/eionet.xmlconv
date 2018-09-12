package eionet.gdem.web.spring.admin;

import eionet.acl.SignOnException;
import eionet.gdem.security.acl.AccessListService;
import eionet.gdem.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.acl.Group;
import java.sql.SQLException;
import java.util.HashMap;

@Controller
@RequestMapping("/admin/users")
public class UsersController {

    private MessageService messageService;
    private AccessListService accessListService;

    @Autowired
    public UsersController(MessageService messageService, AccessListService accessListService) {
        this.messageService = messageService;
        this.accessListService = accessListService;
    }

    @GetMapping
    public String list(@ModelAttribute UsersForm form, Model model) throws SignOnException, SQLException {

        HashMap<String, Group> groups = accessListService.getGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("form", form);
        return "/admin/users/list";
    }

    @GetMapping
    public String add(@ModelAttribute UsersForm form, Model model) {
        return "/admin/users/add";
    }

    @GetMapping
    public String edit(@ModelAttribute UsersForm form, Model model) {
        return "/admin/users/edit";
    }


}
