package eionet.gdem.web.spring.status;

import eionet.gdem.XMLConvException;
import eionet.gdem.api.serverstatus.web.service.ServerStatusObject;
import eionet.gdem.api.serverstatus.web.service.ServerStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 *
 */
@Controller
@RequestMapping("/webstatus")
public class ServerStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStatusController.class);
    private ServerStatusService serverStatusService;

    @Autowired
    public ServerStatusController(ServerStatusService serverStatusService) {
        this.serverStatusService = serverStatusService;
    }

    @GetMapping
    public String status(Model model) throws XMLConvException {
        ServerStatusObject status = serverStatusService.getServerStatus();
        model.addAttribute("status", status);
        return "/webstatus";
    }

}
