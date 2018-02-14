package eionet.gdem.web.spring.schemas;

import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.conversions.StylesheetManager;
import eionet.gdem.web.spring.stylesheet.StylesheetForm;
import eionet.gdem.web.spring.stylesheet.StylesheetListHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 *
 *
 */
@Controller
@RequestMapping("/schemas")
public class SchemasConversionsController {

    private MessageService messageService;
    private SchemasService schemasService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemasConversionsController.class);

    @Autowired
    public SchemasConversionsController(MessageService messageService, SchemasService schemasService) {
        this.messageService = messageService;
        this.schemasService = schemasService;
    }

    @GetMapping("/{schemaId}/conversions")
    public String conversions(@PathVariable String schemaId, Model model, HttpSession session) {
        StylesheetListHolder st = null;
        String schemaUrl = null;
        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        try {
            schemaUrl = schemasService.getSchemaUrl(schemaId);
            SchemaManager sm = new SchemaManager();
            st = sm.getSchemaStylesheetsList(schemaId);
            model.addAttribute("conversions", st);
        } catch (DCMException e) {
            LOGGER.error("Error getting stylesheet", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute("schemaId", schemaId);
        model.addAttribute("schemaUrl", schemaUrl);
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        model.addAttribute(SpringMessages.SUCCESS_MESSAGES, success);
        return "/schemas/conversions";
    }

    @GetMapping("/{schemaId}/conversions/add")
    public String conversionsAdd(@ModelAttribute("form") StylesheetForm stylesheetForm, @PathVariable String schemaId, Model model) throws DCMException {
        SchemaManager sm = new SchemaManager();
        String[] schemas = {sm.getSchema(schemaId).getSchema()};
        stylesheetForm.setNewSchemas(schemas);
        StylesheetManager stylesheetManager = new StylesheetManager();
        model.addAttribute("outputtypes", stylesheetManager.getConvTypes());
        model.addAttribute("form", stylesheetForm);
        model.addAttribute("schemaId", schemaId);
        return "/conversions/add";
    }
}
