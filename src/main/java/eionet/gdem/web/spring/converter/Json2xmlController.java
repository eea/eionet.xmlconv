package eionet.gdem.web.spring.converter;

import eionet.gdem.XMLConvException;
import eionet.gdem.utils.json.Json;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.schemas.IRootElemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * Json to XML converter controller.
 *
 */
@Controller
@RequestMapping("/converter")
public class Json2xmlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Json2xmlController.class);
    private MessageService messageService;
    private IRootElemDao rootElemDao;

    @Autowired
    public Json2xmlController(MessageService messageService, IRootElemDao rootElemDao) {
        this.messageService = messageService;
        this.rootElemDao = rootElemDao;
    }

    @GetMapping("/json2xml")
    public String json2xml(@ModelAttribute("form") Json2xmlForm form) {
        return "/converter/json2xml";
    }

    @PostMapping("/json2xml")
    public String json2xmlSubmit(@ModelAttribute("form") @Valid Json2xmlForm form, BindingResult bindingResult, Model model) {

        String content = form.getContent();
        if (bindingResult.hasErrors()) {
            return "/converter/json2xml";
        }

        String xml = Json.jsonString2xml(content);

        model.addAttribute("xml", xml);
        return "/converter/json2xml";
    }
}
