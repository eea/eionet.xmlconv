package eionet.gdem.web.spring.admin;

import eionet.acl.SignOnException;
import eionet.gdem.security.acl.AccessListService;
import eionet.gdem.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.acl.Group;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    public String list(@ModelAttribute GroupForm form, Model model) throws SignOnException, SQLException {

        HashMap<String, Group> groupz = accessListService.getGroups();
        HashMap<String, ArrayList<?>> groups = new HashMap<>();
        for (Map.Entry<String, Group> group : groupz.entrySet()) {
            groups.put(group.getKey(), Collections.list(group.getValue().members()));
        }
        model.addAttribute("groups", groups);
        model.addAttribute("form", form);
        return "/admin/users/list";
    }

    @PostMapping
    public String submit(@ModelAttribute("form") GroupForm form) {
        form.getGroups();
        return "redirect:/admin/users";
    }

    @GetMapping("/edit")
    public String edit(@ModelAttribute GroupForm form, Model model) throws SQLException, SignOnException {
        HashMap<String, Group> groupz = accessListService.getGroups();
        HashMap<String, ArrayList<?>> groups = new HashMap<>();
        for (Map.Entry<String, Group> group : groupz.entrySet()) {
            groups.put(group.getKey(), Collections.list(group.getValue().members()));
        }
        model.addAttribute("groups", groups);
        model.addAttribute("form", form);
        return "/admin/users/edit";
    }


}
