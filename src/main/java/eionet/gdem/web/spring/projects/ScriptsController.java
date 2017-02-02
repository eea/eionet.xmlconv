package eionet.gdem.web.spring.projects;

import eionet.gdem.data.projects.Project;
import eionet.gdem.data.projects.ProjectService;
import eionet.gdem.data.schemata.SchemaService;
import eionet.gdem.data.scripts.Script;
import eionet.gdem.data.scripts.ScriptService;
import eionet.gdem.data.scripts.ScriptType;
import eionet.gdem.data.transformations.TransformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Controller
@RequestMapping("/projects/{projectId}")
public class ScriptsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptsController.class);

    private ProjectService projectService;

    private SchemaService schemaService;

    private ScriptService scriptService;

    private TransformationService transformationService;

    @Autowired
    private Validator validator;

    @Autowired
    ScriptsController(ProjectService projectService, SchemaService schemaService, ScriptService scriptService, TransformationService transformationService) {
        this.projectService = projectService;
        this.schemaService = schemaService;
        this.scriptService = scriptService;
        this.transformationService = transformationService;
    }

    @GetMapping("/scripts/{id}")
    public String showScript(@PathVariable Integer projectId, @PathVariable Integer id, Model model) {
        Script script = scriptService.findById(id);
        model.addAttribute("script", script);
        model.addAttribute("projectId", projectId);
        return "projects/scripts/show";
    }

    @GetMapping("/scripts/add")
    public String addSriptForm(@PathVariable Integer projectId, Model model) {
        Script script = new Script();
        model.addAttribute("script", script);
        model.addAttribute("id", projectId);
        model.addAttribute("scriptTypes", ScriptType.getMap());
        model.addAttribute("projectSchemata", schemaService.findByProjectId(projectId));
        return "projects/scripts/add";
    }

    @PostMapping("/scripts/add")
    public String addScriptSubmit(@PathVariable Integer projectId, @ModelAttribute Script script, RedirectAttributes redirectAttributes) {
        Project pr = projectService.findById(projectId);
        script.setProject(pr);
        script.setLastModified(LocalDateTime.now());
        scriptService.insert(script);
        return "redirect:/web/projects/{projectId}";
    }

    @GetMapping("/scripts/{id}/edit")
    public String editScriptForm(@PathVariable Integer projectId, @PathVariable Integer id, Model model) {
        Script script = scriptService.findById(id);
        model.addAttribute("script", script);
        model.addAttribute("projectId", projectId);
        model.addAttribute("scriptTypes", ScriptType.getMap());
        model.addAttribute("projectSchemata", schemaService.findByProjectId(projectId));
        return "projects/scripts/edit";
    }

    @PostMapping("/scripts/{id}/edit")
    public String editScriptSubmit(@PathVariable Integer projectId, @PathVariable Integer id, @Valid @ModelAttribute Script updatedScript, BindingResult result, RedirectAttributes redirectAttributes) {
        Script script = scriptService.findById(id);
        if (result.hasErrors()) {
            List<String> messages = new ArrayList<>();
            for (FieldError error: result.getFieldErrors()) {
                messages.add(error.getDefaultMessage());
            }
            redirectAttributes.addFlashAttribute("messages", messages);
            return "redirect:/web/projects/{projectId}/scripts/{id}/edit";
        } else if (!result.hasErrors()) {
            script.setName(updatedScript.getName());
            script.setDescription(updatedScript.getDescription());
            script.setType(updatedScript.getType());
            script.setLinkedSchemata(updatedScript.getLinkedSchemata());
            script.setActive(updatedScript.isActive());
            script.setRemotePath(updatedScript.getRemotePath());
            Script s = scriptService.update(script);
        }
        return "redirect:/web/projects/{projectId}/scripts/{id}";
    }
}
