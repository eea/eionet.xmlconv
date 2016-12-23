package eionet.gdem.web.spring.projects;

import eionet.gdem.XMLConvException;
import eionet.gdem.data.projects.Project;
import eionet.gdem.data.projects.ProjectService;
import eionet.gdem.data.schemata.Schema;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Controller
@RequestMapping("/projects")
public class ProjectsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectsController.class);

    private ProjectService projectService;

    @Autowired
    private Validator validator;

    @Autowired
    ProjectsController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Projects Controller
     * @return Projects view.
     */
    @GetMapping
    public String findAll(HttpServletRequest request, Model model) {
        List<Project> projects = projectService.findAll();
        String loginUrl = null;
        try {
            loginUrl = SecurityUtil.getLoginURL(request);
        } catch (XMLConvException e) {
            // do nothing
        }
        model.addAttribute("projects", projects);
        model.addAttribute("loginUrl", loginUrl);
        model.addAttribute("title", "Projects");
        return "projects/list";
    }

    @GetMapping("/{id}")
    public String find(@PathVariable Integer id, Model model) {
        Project project = projectService.findById(id);
        model.addAttribute("project", project);
        return "projects/show";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Project project = new Project();
        model.addAttribute(project);
        return "projects/new";
    }

    @PostMapping("/new")
    public String createSubmit(@ModelAttribute Project project) {
        Project pr = projectService.insert(project);
        return "redirect:/web/projects/" + pr.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Project project = projectService.findById(id);
        model.addAttribute("project", project);
        model.addAttribute("id", id);
        return "projects/edit";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Integer id, @ModelAttribute Project updatedProject, BindingResult result, RedirectAttributes redirectAttributes) {
        Set<ConstraintViolation<Project>> errors = validator.validate(updatedProject);

        Project project = projectService.findById(id);
        if (errors.size() > 0) {
            List<String> messages = new ArrayList<>();
            for (ConstraintViolation<Project> error : errors) {
                messages.add(error.getMessage());
            }
            redirectAttributes.addFlashAttribute("messages", messages);
            return "redirect:/web/projects/{id}/edit";
        } else if (!result.hasErrors()) {
            project.setName(updatedProject.getName());
            Project pr = projectService.update(project);
        }
        return "redirect:/web/projects/{id}";
    }

    @GetMapping("/{id}/schemata/add")
    public String addSchemaForm(@PathVariable Integer id, Model model) {
        Schema schema = new Schema();
        model.addAttribute("schema", schema);
        model.addAttribute("id", id);
        return "projects/schemata/add";
    }

    @PostMapping("/{id}/schemata/add")
    public String addSchemaSubmit(@PathVariable Integer id, @ModelAttribute Schema schema, RedirectAttributes redirectAttributes) {
        return "redirect:/web/projects/{id}";
    }
}
