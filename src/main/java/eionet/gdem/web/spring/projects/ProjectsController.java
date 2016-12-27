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
        model.addAttribute("schemaLanguages", SchemaLanguage.getList());
        return "projects/schemata/add";
    }

    @PostMapping("/{projectId}/schemata/add")
    public String addSchemaSubmit(@PathVariable Integer projectId, @ModelAttribute Schema schema, RedirectAttributes redirectAttributes) {
        Project pr = projectService.findById(projectId);
        schema.setProject(pr);
        schemaService.insert(schema);
        return "redirect:/web/projects/{projectId}";
    }

    @GetMapping("/{id}/scripts/add")
    public String addSriptForm(@PathVariable Integer id, Model model) {
        Script script = new Script();
        model.addAttribute("script", script);
        model.addAttribute("id", id);
        model.addAttribute("scriptTypes", ScriptType.getMap());
        return "projects/scripts/add";
    }

    @PostMapping("/{projectId}/scripts/add")
    public String addScriptSubmit(@PathVariable Integer projectId, @ModelAttribute Script script, RedirectAttributes redirectAttributes) {
        Project pr = projectService.findById(projectId);
        script.setProject(pr);
        script.setLastModified(LocalDateTime.now());
        scriptService.insert(script);
        return "redirect:/web/projects/{projectId}";
    }

    @GetMapping("/{id}/transformations/add")
    public String addTransformationForm(@PathVariable Integer id, Model model) {
        Transformation transformation = new Transformation();
        model.addAttribute("transformation", transformation);
        model.addAttribute("id", id);
        model.addAttribute("transformationTypes", TransformationType.getMap());
        return "projects/transformations/add";
    }

    @PostMapping("/{projectId}/transformations/add")
    public String addTransformationSubmit(@PathVariable Integer projectId, @ModelAttribute Transformation transformation, RedirectAttributes redirectAttributes) {
        Project pr = projectService.findById(projectId);
        transformation.setProject(pr);
        transformationService.insert(transformation);
        return "redirect:/web/projects/{projectId}";
    }

    @GetMapping("/schemata/{id}/edit")
    public String editSchemaForm(@PathVariable Integer id, Model model) {
        Schema schema = schemaService.findById(id);
        model.addAttribute("schema", schema);
        return "projects/schemata/edit";
    }

    @PostMapping("/schemata/{id}/edit")
    public String editSchemaSubmit(@PathVariable Integer id, @ModelAttribute Schema updatedSchema, BindingResult result, RedirectAttributes redirectAttributes) {
        Set<ConstraintViolation<Schema>> errors = validator.validate(updatedSchema);

        Schema schema = schemaService.findById(id);
        if (errors.size() > 0) {
            List<String> messages = new ArrayList<>();
            for (ConstraintViolation<Schema> error : errors) {
                messages.add(error.getMessage());
            }
            redirectAttributes.addFlashAttribute("messages", messages);
            return "redirect:/web/projects/schema/{id}/edit";
        } else if (!result.hasErrors()) {
            Schema s = schemaService.update(schema);
        }
        return "redirect:/web/projects/schema/{id}/show";
    }

}
