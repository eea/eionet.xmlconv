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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String json2xml(@ModelAttribute("form") Json2xmlForm form, Model model) {
        model.addAttribute("form", form);
        return "/converter/json2xml";
    }

    @PostMapping("json2xml")
    public String json2xmlSubmit(@ModelAttribute("form") Json2xmlForm form, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        String content = form.getContent();
        String xml = null;
        try {
            if (content == null) {
                throw new XMLConvException("Missing request parameter: ");
            }
            //TODO update JSON library.
            xml = Json.jsonString2xml(content);

        } catch (XMLConvException ge) {
            LOGGER.error("Unable to convert JSON to XML. ", ge);
            errors.add("Unable to convert JSON to XML.");
        }
        redirectAttributes.addFlashAttribute("xml", xml);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "redirect:/converter/json2xml";
    }
}
