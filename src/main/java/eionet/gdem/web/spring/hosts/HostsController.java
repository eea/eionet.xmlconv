package eionet.gdem.web.spring.hosts;

import eionet.acl.SignOnException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.spring.SpringMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 *
 */
@Controller
@RequestMapping("/hosts")
public class HostsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostsController.class);

    private MessageService messageService;
    private IHostDao hostDao;

    @Autowired
    public HostsController(MessageService messageService, IHostDao hostDao) {
        this.messageService = messageService;
        this.hostDao = hostDao;
    }

    @GetMapping
    public String list(@ModelAttribute("form") HostForm form, Model model, HttpSession session) {

        List<HostDto> hosts = new ArrayList();
        String user = (String) session.getAttribute("user");
        try {
            if (SecurityUtil.hasPerm(user, "/host", "v")) {
                Vector list = hostDao.getHosts(null);
                for (int i = 0; i < list.size(); i++) {
                    Hashtable host = (Hashtable) list.get(i);
                    HostDto h = new HostDto();
                    h.setId((String) host.get("host_id"));
                    h.setHostname((String) host.get("host_name"));
                    h.setUsername((String) host.get("user_name"));
                    hosts.add(h);
                }
            } else {
                throw new AccessDeniedException(messageService.getMessageInt("error.vnoperm", "label.hosts"));
            }
        } catch (SignOnException | SQLException e) {
            LOGGER.error("Access denied", e);
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }
        model.addAttribute("hosts", hosts);
        return "/hosts/list";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model, HttpSession session) {

        HostForm hostForm = new HostForm();
        String user = (String) session.getAttribute("user");
        SpringMessages errors = new SpringMessages();

        try {
            if (SecurityUtil.hasPerm(user, "/host", "u")) {
                Vector hosts = hostDao.getHosts(id);

                if (hosts != null) {
                    Hashtable host = (Hashtable) hosts.get(0);
                    hostForm.setId((String) host.get("host_id"));
                    hostForm.setHost((String) host.get("host_name"));
                    hostForm.setUsername((String) host.get("user_name"));
                    hostForm.setPassword((String) host.get("pwd"));
                }
            } else {
                throw new AccessDeniedException(messageService.getMessage("error.unoperm", "label.hosts"));
            }
        } catch (SQLException | SignOnException e) {
            LOGGER.error("Error: ", e);
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        if (errors.size() > 0) {
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }
        model.addAttribute("form", hostForm);
        return "/hosts/edit";
    }

    @PostMapping(params = {"update"})
    public String editSubmit(@ModelAttribute("form") @Valid HostForm updatedForm, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages messages = new SpringMessages();

        String hostId = updatedForm.getId();
        String host = updatedForm.getHost();
        String username = updatedForm.getUsername();
        String password = updatedForm.getPassword();

        String user = (String) session.getAttribute("user");

        try {
            if (SecurityUtil.hasPerm(user, "/host", "u")) {
                hostDao.updateHost(hostId, host, username, password);
                messages.add(messageService.getMessage("label.hosts.updated"));
            } else {
                throw new AccessDeniedException(messageService.getMessage("error.unoperm", "label.hosts"));
            }
        } catch (SignOnException | SQLException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/hosts/" + hostId;
    }

    @GetMapping("/add")
    public String add(Model model, HttpSession session) {

        String user = (String) session.getAttribute("user");
        HostForm form = new HostForm();

        try {
            if (!SecurityUtil.hasPerm(user, "/host", "i")) {
                throw new AccessDeniedException(messageService.getMessage("error.inoperm", "label.hosts"));
            }
        } catch (SignOnException e) {
            LOGGER.error("Access denied", e);
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        model.addAttribute("form", form);
        return "/hosts/add";
    }

    @PostMapping(params = {"add"})
    public String addSubmit(@ModelAttribute("form") HostForm form, HttpSession session, RedirectAttributes redirectAttributes) {
        SpringMessages messages = new SpringMessages();

        String host = form.getHost();
        String username = form.getUsername();
        String password = form.getPassword();

        String user = (String) session.getAttribute("user");

        try {
            if (SecurityUtil.hasPerm(user, "/host", "i")) {
                hostDao.addHost(host, username, password);
                messages.add(messageService.getMessage("label.hosts.inserted"));
            } else {
                throw new AccessDeniedException(messageService.getMessage("error.inoperm"));
            }
        } catch (SignOnException | SQLException e) {
            LOGGER.error("Access denied");
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/hosts";
    }

    @PostMapping(params = {"delete"})
    public String delete(@ModelAttribute("form") HostForm form, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages messages = new SpringMessages();

        String user = (String) session.getAttribute("user");


        try {
            if (SecurityUtil.hasPerm(user, "/host", "d")) {
                hostDao.removeHost(form.getId());
                messages.add(messageService.getMessage("label.hosts.deleted"));
            } else {
                throw new AccessDeniedException(messageService.getMessage("error.dnoperm", "label.hosts"));
            }
        } catch (SignOnException | SQLException e) {
            LOGGER.error("", e);
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/hosts";
    }

}
