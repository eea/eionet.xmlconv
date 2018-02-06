package eionet.gdem.web.spring.schemas;

import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.scripts.QAScriptForm;
import eionet.gdem.web.spring.scripts.QAScriptListHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 */
@Controller
@RequestMapping("/schemas")
public class SchemasScriptsController {

    private MessageService messageService;
    private SchemasService schemasService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemasScriptsController.class);

    @Autowired
    public SchemasScriptsController(MessageService messageService, SchemasService schemasService) {
        this.messageService = messageService;
        this.schemasService = schemasService;
    }

    @GetMapping("/{schemaId}/scripts")
    public String scripts(@PathVariable String schemaId, Model model, HttpServletRequest httpServletRequest) {
        QAScriptListHolder st = null;

        SpringMessages errors = new SpringMessages();

        try {
            SchemaManager sm = new SchemaManager();
            st = sm.getSchemasWithQAScripts(schemaId);
            /*model.addAttribute("scripts", QAScriptListLoader.getList(httpServletRequest));
            httpServletRequest.setAttribute("schema.qascripts", st);*/
            model.addAttribute("schemaId", schemaId);
            model.addAttribute("scripts", st);
            model.addAttribute("schemaForm", new SchemaForm());
            model.addAttribute("scriptForm", new QAScriptForm());
        } catch (DCMException e) {
            LOGGER.error("Error getting schema QA scripts", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/scripts";
    }

    @GetMapping("/{schemaId}/scripts/add")
    public String scriptsAdd(@PathVariable String schemaId, Model model) {
        /*SchemaManager sm = new SchemaManager();
        schemasService.schemaUrl(schemaId);
        QAScriptForm form = new QAScriptForm();
        String schemaUrl = "";
        /*try {
            schemaUrl = sm.getSchemaUrl(schemaId);

        } catch (DCMException e) {
            LOGGER.error("Error while finding schema", e);
        }

        schemaUrl = schemasService.schemaUrl(schemaId);

        form.setSchemaId(schemaId);

        // TODO fix this
        model.addAttribute("resulttypes", SpringEventListeners.loadConvTypes(XQScript.SCRIPT_RESULTTYPES));
        model.addAttribute("scriptlangs", SpringEventListeners.loadConvTypes(XQScript.SCRIPT_LANGS));
        model.addAttribute("form", form);*/
        return "/scripts/add";
    }
}
