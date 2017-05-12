package eionet.gdem.web.spring.conversions;

import eionet.gdem.services.MessageService;
import eionet.gdem.services.db.dao.IRootElemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        return "/listConv.jsp";
    }

    @GetMapping("/search")
    public String searchXML(Model model) {
        ConversionForm form = new ConversionForm();
        model.addAttribute("form", form);
        return "/crConversion.jsp";
    }

    @GetMapping("/excel2Xml")
    public String excel2xml(Model model) {
        ConversionForm form = new ConversionForm();
        model.addAttribute("form", form);
        return "/excel2XmlConv.jsp";
    }

    @GetMapping("/json2Xml")
    public String json2xml(Model model) {
        ConversionForm form = new ConversionForm();
        model.addAttribute("form", form);
        return "/json2Xml.jsp";
    }

}
