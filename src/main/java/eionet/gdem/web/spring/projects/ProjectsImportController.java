package eionet.gdem.web.spring.projects;

import eionet.gdem.services.projects.export.ProjectImportValidator;
import eionet.gdem.services.projects.export.ProjectImportWrapper;
import eionet.gdem.services.projects.export.ProjectStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 */
@Controller
@RequestMapping("/projects/import")
public class ProjectsImportController {

    private final ProjectStorageService projectStorageService;

    @Autowired
    private ProjectImportValidator projectImportValidator;

    @Autowired
    public ProjectsImportController(ProjectStorageService projectStorageService) {
        this.projectStorageService = projectStorageService;
    }

    @GetMapping
    public String importProjectForm(Model model) {
        ProjectImportWrapper fileWrapper = new ProjectImportWrapper();
        model.addAttribute("fileWrapper", fileWrapper);
        return "projects/import";
    }

    @PostMapping
    public String importProjectSubmit(@ModelAttribute ProjectImportWrapper fileWrapper, BindingResult result, RedirectAttributes redirectAttributes) {
        Integer projectId = projectStorageService.importProject(fileWrapper);
        return "redirect:/web/projects";
    }

}
