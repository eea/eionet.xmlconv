package eionet.gdem.web.spring.schemas;

import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.listeners.AppServletContextListener;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.scripts.QAScriptForm;
import eionet.gdem.web.spring.scripts.QAScriptListHolder;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

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
    public String scripts(@PathVariable String schemaId, Model model, HttpServletRequest httpServletRequest,@ModelAttribute("schemaForm") SchemaForm schemaForm) {

        try {
            SchemaManager sm = new SchemaManager();
        Schema schema =    sm.getSchema(schemaId);
            QAScriptListHolder st = sm.getSchemasWithQAScripts(schemaId);
            /*model.addAttribute("scripts", QAScriptListLoader.getList(httpServletRequest));
            httpServletRequest.setAttribute("schema.qascripts", st);*/
            model.addAttribute("schemaId", schemaId);
            model.addAttribute("scripts", st);
            model.addAttribute("schemaForm", new SchemaForm());
            model.addAttribute("scriptForm", new QAScriptForm());
            model.addAttribute("doValidation",schema.isDoValidation());
            schemaForm.setDoValidation(schema.isDoValidation());
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


    @PostMapping(value="/scriptsUpdate",params = {"update"})
    public String editSubmit(@ModelAttribute("form") SchemaForm form, HttpServletRequest httpServletRequest, Model model,
                             BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {
        SpringMessages messages = new SpringMessages();

        String schemaId = form.getSchemaId();
        String schema = form.getSchema();
        boolean doValidation = form.isDoValidation();



        try {
            SchemaManager sm = new SchemaManager();
            String schemaIdByUrl = sm.getSchemaId(schema);
            if (schemaIdByUrl != null && !schemaIdByUrl.equals(schemaId)) {
                String schemaTargetUrl = String.format("viewSchemaForm?schemaId=%s", schemaIdByUrl);
                bindingResult.reject(messageService.getMessage("label.schema.url.exists", schemaTargetUrl));
                return "/schemas/edit";
            }

            this.schemasService.updateSchemaValidation(schemaId,doValidation);

            messages.add(messageService.getMessage("label.schema.updated"));

            QAScriptListLoader.reloadList(httpServletRequest);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            throw new RuntimeException("Error editing schema" + e.getErrorCode());
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas/" + schemaId + "/edit";
    }
}
