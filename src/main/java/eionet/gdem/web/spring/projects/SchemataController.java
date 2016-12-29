package eionet.gdem.web.spring.projects;

import eionet.gdem.data.projects.Project;
import eionet.gdem.data.projects.ProjectService;
import eionet.gdem.data.schemata.Schema;
import eionet.gdem.data.schemata.SchemaLanguage;
import eionet.gdem.data.schemata.SchemaService;
import eionet.gdem.data.scripts.ScriptService;
import eionet.gdem.data.transformations.TransformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Controller
@RequestMapping("/projects/{projectId}")
public class SchemataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemataController.class);

    private ProjectService projectService;

    private SchemaService schemaService;

    private ScriptService scriptService;

    private TransformationService transformationService;

    @Autowired
    private Validator validator;

    @Autowired
    SchemataController(ProjectService projectService, SchemaService schemaService, ScriptService scriptService, TransformationService transformationService) {
        this.projectService = projectService;
        this.schemaService = schemaService;
        this.scriptService = scriptService;
        this.transformationService = transformationService;
    }

    @GetMapping("/schemata/{id}")
    public String showSchema(@PathVariable Integer projectId, @PathVariable Integer id, Model model) {
        Schema schema = schemaService.findById(id);
        model.addAttribute("schema", schema);
        model.addAttribute("projectId", projectId);
        return "projects/schemata/show";
    }

    @GetMapping("/schemata/add")
    public String addSchemaForm(@PathVariable Integer projectId, Model model) {
        Schema schema = new Schema();
        model.addAttribute("schema", schema);
        model.addAttribute("id", projectId);
        model.addAttribute("schemaLanguages", SchemaLanguage.getList());
        return "projects/schemata/add";
    }

    @PostMapping("/schemata/add")
    public String addSchemaSubmit(@PathVariable Integer projectId, @ModelAttribute Schema schema, RedirectAttributes redirectAttributes) {
        Project pr = projectService.findById(projectId);
        schema.setProject(pr);
        schemaService.insert(schema);
        return "redirect:/web/projects/{projectId}";
    }

    @GetMapping("/schemata/{id}/edit")
    public String editSchemaForm(@PathVariable Integer projectId, @PathVariable Integer id, Model model) {
        Schema schema = schemaService.findById(id);
        model.addAttribute("schema", schema);
        model.addAttribute("projectId", projectId);
        model.addAttribute("schemaLanguages", SchemaLanguage.getList());
        return "projects/schemata/edit";
    }

    @PostMapping("/schemata/{id}/edit")
    public String editSchemaSubmit(@PathVariable Integer projectId, @PathVariable Integer id, @Valid @ModelAttribute Schema updatedSchema, BindingResult result, RedirectAttributes redirectAttributes) {
        Schema schema = schemaService.findById(id);
        if (result.hasErrors()) {
            List<String> messages = new ArrayList<>();
            for (FieldError error: result.getFieldErrors()) {
                messages.add(error.getDefaultMessage());
            }
            redirectAttributes.addFlashAttribute("messages", messages);
            return "redirect:/web/projects/{projectId}/schemata/{id}/edit";
        } else if (!result.hasErrors()) {
            schema.setUrl(updatedSchema.getUrl());
            schema.setDescription(updatedSchema.getDescription());
            schema.setSchemaLanguage(updatedSchema.getSchemaLanguage());
            schema.setValidation(updatedSchema.isValidation());
            schema.setBlocking(updatedSchema.isBlocking());
            Schema s = schemaService.update(schema);
        }
        return "redirect:/web/projects/{projectId}/schemata/{id}";
    }
}
