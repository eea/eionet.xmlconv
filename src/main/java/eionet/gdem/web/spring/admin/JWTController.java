package eionet.gdem.web.spring.admin;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.api.jwt.service.JWTService;
import eionet.gdem.api.jwt.service.impl.JWTServiceImpl;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/generateJWTToken")
public class JWTController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTController.class);
    private MessageService messageService;
    private final JWTService jwtService = new JWTServiceImpl();
    private String token = null;

    @Autowired
    public JWTController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String view(Model model){
        model.addAttribute("token", getToken());
        return "/admin/generateJWTToken";
    }

    @GetMapping("/generateToken")
    public String generateToken(Model model, HttpSession httpSession) throws SignOnException, JWTException {
        String user = (String) httpSession.getAttribute("user");
        if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_ADMIN_PATH, "u")) {
            throw new AccessDeniedException(messageService.getMessage("label.authorization.generate.token"));
        }
        String token = jwtService.generateJWTToken();
        setToken(token);
        model.addAttribute("token", token);
        return "redirect:/admin/generateJWTToken";
    }

    private String getToken() {
        return token;
    }

    private void setToken(String token) {
        this.token = token;
    }
}
