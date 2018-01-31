package eionet.gdem.web.spring.converter;

import eionet.gdem.XMLConvException;
import eionet.gdem.qa.functions.Json;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.schemas.IRootElemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
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
    public String json2xml(Model model) {
        Json2xmlForm form = new Json2xmlForm();
        model.addAttribute("form", form);
        return "/converter/json2xml";
    }

    @PostMapping("json2xml")
    public String json2xmlSubmit(@ModelAttribute Json2xmlForm form, RedirectAttributes redirectAttributes) {

        String content = form.getContent();
        String xml = null;
        try {
            if (content == null) {
                throw new XMLConvException("Missing request parameter: ");
            }
            //TODO update JSON library.
            xml = Json.jsonString2xml(content);

        } catch (XMLConvException ge) {
            LOGGER.error("Unable to convert JSON to XML. " + ge.toString());
        } catch (Exception e) {
            LOGGER.error("Unable to convert JSON to XML. ");
        }
        redirectAttributes.addFlashAttribute("xml", xml);
        return "redirect:/converter/json2xml";
    }
}
