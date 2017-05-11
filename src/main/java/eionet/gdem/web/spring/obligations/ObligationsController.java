package eionet.gdem.web.spring.obligations;

import eionet.gdem.data.obligations.Obligation;
import eionet.gdem.data.obligations.ObligationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
@Controller
@RequestMapping("/obligations")
public class ObligationsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObligationsController.class);

    private ObligationService obligationService;

    @Autowired
    public ObligationsController(ObligationService obligationService) {
        this.obligationService = obligationService;
    }

    @GetMapping
    public String findAll(Model model) {
        List<Obligation> obligations = obligationService.findAll();
        List<Obligation> deleteObligations = new ArrayList<>();
        model.addAttribute("obligations", obligations);
        model.addAttribute("deleteObligations", obligations);
        return "obligations/list";
    }

    @GetMapping("/new")
    public String addForm(Model model) {
        Obligation obligation = new Obligation();
        model.addAttribute("obligation", obligation);
        return "obligations/new";
    }

    @PostMapping("/new")
    public String addSubmit(@ModelAttribute Obligation obligation, BindingResult result, RedirectAttributes redirectAttributes) {
        obligationService.insert(obligation);
        return "redirect:/obligations/list";
    }

    @PostMapping("/delete")
    public String delete(@ModelAttribute List<Obligation> deleteObligations, BindingResult result, RedirectAttributes redirectAttributes) {
        obligationService.deleteList(deleteObligations);
        return "redirect:/obligations/list";
    }

}
