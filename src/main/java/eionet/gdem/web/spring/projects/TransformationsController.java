package eionet.gdem.web.spring.projects;

import eionet.gdem.data.projects.Project;
import eionet.gdem.data.projects.ProjectService;
import eionet.gdem.data.schemata.SchemaService;
import eionet.gdem.data.scripts.ScriptService;
import eionet.gdem.data.transformations.Transformation;
import eionet.gdem.data.transformations.TransformationService;
import eionet.gdem.data.transformations.TransformationType;
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
public class TransformationsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransformationsController.class);

    private ProjectService projectService;

    private SchemaService schemaService;

    private ScriptService scriptService;

    private TransformationService transformationService;

    @Autowired
    private Validator validator;

    @Autowired
    TransformationsController(ProjectService projectService, SchemaService schemaService, ScriptService scriptService, TransformationService transformationService) {
        this.projectService = projectService;
        this.schemaService = schemaService;
        this.scriptService = scriptService;
        this.transformationService = transformationService;
    }

    @GetMapping("/transformations/{id}")
    public String showTransformation(@PathVariable Integer projectId, @PathVariable Integer id, Model model) {
        Transformation transformation = transformationService.findById(id);
        model.addAttribute("transformation", transformation);
        model.addAttribute("projectId", projectId);
        return "projects/transformations/show";
    }

    @GetMapping("/transformations/add")
    public String addTransformationForm(@PathVariable Integer projectId, Model model) {
        Transformation transformation = new Transformation();
        model.addAttribute("transformation", transformation);
        model.addAttribute("id", projectId);
        model.addAttribute("transformationTypes", TransformationType.getMap());
        return "projects/transformations/add";
    }

    @PostMapping("/transformations/add")
    public String addTransformationSubmit(@PathVariable Integer projectId, @ModelAttribute Transformation transformation, RedirectAttributes redirectAttributes) {
        Project pr = projectService.findById(projectId);
        transformation.setProject(pr);
        transformationService.insert(transformation);
        return "redirect:/projects/{projectId}";
    }

    @GetMapping("/transformations/{id}/edit")
    public String editTransformationForm(@PathVariable Integer projectId, @PathVariable Integer id, Model model) {
        Transformation transformation = transformationService.findById(id);
        model.addAttribute("transformation", transformation);
        model.addAttribute("projectId", projectId);
        model.addAttribute("transformationTypes", TransformationType.getMap());
        return "projects/transformations/edit";
    }

    @PostMapping("/transformations/{id}/edit")
    public String editScriptSubmit(@PathVariable Integer projectId, @PathVariable Integer id, @Valid @ModelAttribute Transformation updatedTransformation, BindingResult result, RedirectAttributes redirectAttributes) {
        Transformation transformation = transformationService.findById(id);
        if (result.hasErrors()) {
            List<String> messages = new ArrayList<>();
            for (FieldError error: result.getFieldErrors()) {
                messages.add(error.getDefaultMessage());
            }
            redirectAttributes.addFlashAttribute("messages", messages);
            return "redirect:/projects/{projectId}/transformations/{id}/edit";
        } else if (!result.hasErrors()) {
            transformation.setName(updatedTransformation.getName());
            transformation.setDescription(updatedTransformation.getDescription());
            transformation.setType(updatedTransformation.getType());
            transformation.setActive(updatedTransformation.isActive());
            transformation.setRemotePath(updatedTransformation.getRemotePath());
            Transformation s = transformationService.update(transformation);
        }
        return "redirect:/projects/{projectId}/transformations/{id}";
    }
}
