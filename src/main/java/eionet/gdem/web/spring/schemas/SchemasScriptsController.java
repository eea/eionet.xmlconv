package eionet.gdem.web.spring.schemas;

import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.listeners.AppServletContextListener;
import eionet.gdem.web.spring.scripts.QAScriptForm;
import eionet.gdem.web.spring.scripts.QAScriptListHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

        try {
            SchemaManager sm = new SchemaManager();
            QAScriptListHolder st = sm.getSchemasWithQAScripts(schemaId);
            /*model.addAttribute("scripts", QAScriptListLoader.getList(httpServletRequest));
            httpServletRequest.setAttribute("schema.qascripts", st);*/
            model.addAttribute("schemaId", schemaId);
            model.addAttribute("scripts", st);
            model.addAttribute("schemaForm", new SchemaForm());
            model.addAttribute("scriptForm", new QAScriptForm());
        } catch (DCMException e) {
            throw new RuntimeException("Error getting schema QA scripts: " + messageService.getMessage(e.getErrorCode()));
        }
        return "/schemas/scripts";
    }

    @GetMapping("/{schemaId}/scripts/add")
    public String scriptsAdd(@PathVariable String schemaId, @ModelAttribute("form") QAScriptForm scriptForm, Model model) {
        SchemaManager sm = new SchemaManager();
        String schemaUrl = schemasService.getSchemaUrl(schemaId);
        scriptForm.setSchema(schemaUrl);
        scriptForm.setSchemaId(schemaId);
        model.addAttribute("resulttypes", AppServletContextListener.loadConvTypes(XQScript.SCRIPT_RESULTTYPES));
        model.addAttribute("scriptlangs", AppServletContextListener.loadConvTypes(XQScript.SCRIPT_LANGS));
        return "/scripts/add";
    }
}
