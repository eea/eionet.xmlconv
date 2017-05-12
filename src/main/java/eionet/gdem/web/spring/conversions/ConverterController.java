package eionet.gdem.web.spring.conversions;

import eionet.gdem.services.MessageService;
import eionet.gdem.services.db.dao.IRootElemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */
@Controller
@RequestMapping("/converter")
public class ConverterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsController.class);

    private MessageService messageService;

    private IRootElemDao rootElemDao;

    @Autowired
    public ConverterController(MessageService messageService, IRootElemDao rootElemDao) {
        this.messageService = messageService;
        this.rootElemDao = rootElemDao;
    }

    @GetMapping
    public String list(Model model) {
        ConversionForm form = new ConversionForm();
        model.addAttribute("form", form);
        return "/converter/list";
    }

    @GetMapping("/search")
    public String searchXML(Model model) {
        ConversionForm form = new ConversionForm();
        model.addAttribute("form", form);
        return "/converter/search";
    }

    @GetMapping("/excel2xml")
    public String excel2xml(Model model) {
        ConversionForm form = new ConversionForm();
        model.addAttribute("form", form);
        return "/converter/excel2xml";
    }

    @GetMapping("/json2xml")
    public String json2xml(Model model) {
        ConversionForm form = new ConversionForm();
        model.addAttribute("form", form);
        return "/converter/json2xml";
    }

    @PostMapping("json2xml")
    public String json2xmlSubmit(@ModelAttribute ConversionForm form) {

        return "redirect:/old/converter/json2xml";
    }

}
