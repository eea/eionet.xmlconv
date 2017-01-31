package eionet.gdem.web.spring.projects;

import eionet.gdem.XMLConvException;
import eionet.gdem.data.projects.Project;
import eionet.gdem.data.projects.ProjectService;
import eionet.gdem.data.schemata.Schema;
import eionet.gdem.data.schemata.SchemaLanguage;
import eionet.gdem.data.schemata.SchemaService;
import eionet.gdem.data.scripts.Script;
import eionet.gdem.data.scripts.ScriptService;
import eionet.gdem.data.scripts.ScriptType;
import eionet.gdem.data.transformations.Transformation;
import eionet.gdem.data.transformations.TransformationService;
import eionet.gdem.data.transformations.TransformationType;
import eionet.gdem.services.projects.export.ProjectExporter;
import eionet.gdem.services.projects.export.ProjectStorageService;
import eionet.gdem.services.projects.export.gson.ProjectExporterGson;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.time.LocalDateTime;
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

    private SchemaService schemaService;

    private ScriptService scriptService;

    private TransformationService transformationService;

    @Autowired
    private Validator validator;

    @Autowired
    ProjectsController(ProjectService projectService, SchemaService schemaService, ScriptService scriptService, TransformationService transformationService) {
        this.projectService = projectService;
        this.schemaService = schemaService;
        this.scriptService = scriptService;
        this.transformationService = transformationService;
    }

    /**
     * Projects Controller
     * @return Projects view.
     */
    @GetMapping
    public String findAll(Model model) {
        List<Project> projects = projectService.findAll();
        model.addAttribute("projects", projects);
        model.addAttribute("title", "Projects");
        return "projects/list";
    }

    @GetMapping("/{id}")
    public String showProject(@PathVariable Integer id, Model model) {
        Project project = projectService.findById(id);
        model.addAttribute("project", project);
        return "projects/show";
    }

    @GetMapping("/new")
    public String newProjectForm(Model model) {
        Project project = new Project();
        model.addAttribute(project);
        return "projects/new";
    }

    @PostMapping("/new")
    public String newProjectSubmit(@ModelAttribute Project project) {
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

    @GetMapping("/{id}/export")
    public String export(@PathVariable Integer id, Model model) {
        ProjectExporter projectExporter = new ProjectExporterGson();
        Project project = projectService.findById(id);
        projectExporter.export(project);
        model.addAttribute("project", project);
        model.addAttribute("id", id);
        return "projects/show";
    }

}
