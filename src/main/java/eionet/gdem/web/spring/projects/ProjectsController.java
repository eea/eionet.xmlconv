package eionet.gdem.web.spring.projects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */
@Controller
public class ProjectsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectsController.class);

    /**
     * Projects Controller
     * @return Projects view.
     */
    @RequestMapping("projects")
    public String test() {
        return "projects";
    }
}
