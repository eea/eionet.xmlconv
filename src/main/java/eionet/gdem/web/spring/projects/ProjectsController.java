package eionet.gdem.web.spring.projects;

import eionet.gdem.data.obligations.Obligation;
import eionet.gdem.data.obligations.ObligationService;
import eionet.gdem.data.projects.Project;
import eionet.gdem.data.projects.ProjectService;
import eionet.gdem.data.schemata.SchemaService;
import eionet.gdem.data.scripts.ScriptService;
import eionet.gdem.data.transformations.TransformationService;
import eionet.gdem.services.projects.export.ProjectExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Controller
@RequestMapping({"/", "/projects"})
public class ProjectsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectsController.class);

    private ProjectService projectService;

    private SchemaService schemaService;

    private ScriptService scriptService;

    private TransformationService transformationService;

    private ObligationService obligationService;

    @Autowired
    private ProjectExporter projectExporter;

    @Autowired
    private Validator validator;

    @Autowired
    ProjectsController(ProjectService projectService, SchemaService schemaService, ScriptService scriptService, TransformationService transformationService, ObligationService obligationService) {
        this.projectService = projectService;
        this.schemaService = schemaService;
        this.scriptService = scriptService;
        this.transformationService = transformationService;
        this.obligationService = obligationService;
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
        List<Obligation> obligations = obligationService.findAll();
        model.addAttribute("project", project);
        model.addAttribute("id", id);
        model.addAttribute("obligations", obligations);
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
            project.setObligations(updatedProject.getObligations());
            Project pr = projectService.update(project);
        }
        return "redirect:/web/projects/{id}";
    }

    @GetMapping(value = "/{id}/export", produces = "application/zip")
    public void export(@PathVariable Integer id, Model model, HttpServletResponse response) {

        Project project = projectService.findById(id);
        File responseFile = projectExporter.export(project);
        if (responseFile.isFile()) {
            try {
                response.setHeader("Content-Disposition", "attachment; filename=" + responseFile.getName());
                OutputStream out = response.getOutputStream();
                FileCopyUtils.copy(FileCopyUtils.copyToByteArray(responseFile), out);
            } catch (IOException e) {
                LOGGER.error("Export failed: ", e);
            }
        } else {
            LOGGER.error("Exported file not found");
            return;
        }
        /*model.addAttribute("project", project);
        model.addAttribute("id", id);
        return "projects/show";*/
    }

    @GetMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, Model model) {
        projectService.delete(id);
        return "projects/list";
    }
}
