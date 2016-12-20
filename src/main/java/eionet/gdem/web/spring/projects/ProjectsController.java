package eionet.gdem.web.spring.projects;

import eionet.gdem.XMLConvException;
import eionet.gdem.data.projects.Project;
import eionet.gdem.data.projects.ProjectService;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
@Controller
public class ProjectsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectsController.class);

    private ProjectService projectService;

    @Autowired
    ProjectsController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Projects Controller
     * @return Projects view.
     */
    @RequestMapping("projects")
    public String test(HttpServletRequest request, Model model) {
        List<Project> projects = projectService.getAllProjects();
        String loginUrl = null;
        try {
            loginUrl = SecurityUtil.getLoginURL(request);
        } catch (XMLConvException e) {
            // do nothing
        }
        model.addAttribute("projects", projects);
        model.addAttribute("loginUrl", loginUrl);
        model.addAttribute("title", "Projects");
        return "projects";
    }

}
