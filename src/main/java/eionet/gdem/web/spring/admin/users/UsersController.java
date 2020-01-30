package eionet.gdem.web.spring.admin.users;

import eionet.acl.AccessController;
import eionet.acl.AclInitializerImpl;
import eionet.acl.AclProperties;
import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.configuration.CopyAclFiles;
import eionet.gdem.security.acl.AccessListService;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.propertyplaceholderresolver.CircularReferenceException;
import eionet.propertyplaceholderresolver.ConfigurationPropertyResolver;
import eionet.propertyplaceholderresolver.UnresolvedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.security.Security;
import java.sql.SQLException;
import java.util.*;

@Controller
@RequestMapping("/admin/users")
public class UsersController {

    private MessageService messageService;
    private AccessListService accessListService;


    //@Value("${owner.permission}")
    private String  ownerPermission ;
    //   @Value("${anonymous.access}")
    private String  anonymousAccess ;

    //  @Value("${authenticated.access}")
    private String  authenticatedAccess ;

    //  @Value("${defaultdoc.permissions}")
    private String  defaultdocPermissions ;

    //  @Value("${persistence.provider}")
    private String  persistenceProvider ;

    // @Value("${initial.admin}")
    private String  initialAdmin ;

    //  @Value("${file.aclfolder}")
    private String  fileAclfolder ;

    //  @Value("${file.localusers}")
    private String  fileLocalusers ;

    //@Value("${file.localgroups}")
    private String  fileLocalgroups ;

    //   @Value("${file.permissions}")
    private String  filePermissions ;

    // @Value("${acl.db.driver}")
    private String  dbDriver ;

    // @Value("${acl.db.url}")
    private String  dbUrl ;

    // @Value("${acl.db.user}")
    private String  dbUser ;

    //  @Value("${acl.db.pwd}")
    private String  dbPwd ;


    @Autowired
    SpringApplicationContext springApplicationContext;

    @Autowired
    ConfigurationPropertyResolver configurationPropertyResolver;
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    public UsersController(MessageService messageService, AccessListService accessListService) {
        this.messageService = messageService;
        this.accessListService = accessListService;
    }

    @GetMapping
    public String list(Model model, HttpSession httpSession) throws SignOnException, SQLException {

        String user = (String) httpSession.getAttribute("user");
        if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_ADMIN_PATH, "u")) {
            throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
        }

        HashMap<String, List<String>> groups = accessListService.getGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("form", new UsersForm());
        return "/admin/users/list";
    }

    @PostMapping
    public String submit(UsersForm form, HttpSession httpSession) throws SignOnException, UnresolvedPropertyException, CircularReferenceException {
        String user = (String) httpSession.getAttribute("user");
        if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_ADMIN_PATH, "u")) {
            throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
        }
        List<Group> groups = form.getGroups();
        accessListService.writeGroups(groups);

        AclProperties aclProperties = new AclProperties();
        aclProperties.setOwnerPermission(configurationPropertyResolver.resolveValue("owner.permission"));
        aclProperties.setAnonymousAccess(configurationPropertyResolver.resolveValue("anonymous.access"));
        aclProperties.setAuthenticatedAccess(configurationPropertyResolver.resolveValue("authenticated.access"));
        aclProperties.setDefaultdocPermissions(configurationPropertyResolver.resolveValue("defaultdoc.permissions"));
        aclProperties.setPersistenceProvider(configurationPropertyResolver.resolveValue("persistence.provider"));
        aclProperties.setInitialAdmin(configurationPropertyResolver.resolveValue("initial.admin"));
        aclProperties.setFileAclfolder(configurationPropertyResolver.resolveValue("file.aclfolder"));
        aclProperties.setFileLocalgroups(configurationPropertyResolver.resolveValue("file.localgroups"));
        aclProperties.setFileLocalusers(configurationPropertyResolver.resolveValue("file.localusers"));
        aclProperties.setFilePermissions(configurationPropertyResolver.resolveValue("file.permissions"));
        aclProperties.setDbDriver(configurationPropertyResolver.resolveValue("db.driver"));
        aclProperties.setDbUrl(configurationPropertyResolver.resolveValue("db.url"));
        aclProperties.setDbUser(configurationPropertyResolver.resolveValue("db.user"));
        aclProperties.setDbPwd(configurationPropertyResolver.resolveValue("db.pwd"));

        try {
            AccessController accessController = new AccessController(aclProperties);
            Method initAclsMethod = AccessController.class.getDeclaredMethod("initAcls");
            initAclsMethod.setAccessible(true);
            initAclsMethod.invoke(accessController);
            initAclsMethod.setAccessible(false);
        } catch (Exception ex){
            LOGGER.error("Could not refresh acl in memory");

        }

        return "redirect:/admin/users";
    }

    @GetMapping("/edit")
    public String edit(Model model, HttpSession httpSession) throws SQLException, SignOnException {

        String user = (String) httpSession.getAttribute("user");
        if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_ADMIN_PATH, "u")) {
            throw new AccessDeniedException(messageService.getMessage("label.autorization.config.update"));
        }

        HashMap<String, List<String>> groups = accessListService.getGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("form", new UsersForm());
        return "/admin/users/edit";
    }


}
