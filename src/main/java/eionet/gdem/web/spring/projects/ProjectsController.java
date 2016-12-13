package eionet.gdem.web.spring.projects;

import eionet.gdem.data.projects.Project;
import eionet.gdem.data.projects.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String test() {
        List<Project> projects = projectService.getAllProjects();
        return "projects";
    }
}
