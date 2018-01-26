package eionet.gdem.web.spring.schemas;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.FileUploadWrapper;
import eionet.gdem.web.spring.SpringMessage;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.schemas.SchemaElemHolder;
import eionet.gdem.web.spring.schemas.UplSchemaHolder;
import eionet.gdem.web.spring.scripts.QAScriptForm;
import eionet.gdem.web.spring.scripts.QAScriptListHolder;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import eionet.gdem.web.spring.stylesheet.StylesheetListHolder;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
public class SchemasController {

    private MessageService messageService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemasController.class);

    @Autowired
    public SchemasController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        UplSchemaHolder holder = null;
        SpringMessages errors = new SpringMessages();

        String user = (String) session.getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            holder = sm.getAllSchemas(user);
            SingleForm cForm = new SingleForm();
            model.addAttribute("form", cForm);
            model.addAttribute("schemas", holder);
        } catch (DCMException e) {
            LOGGER.error("Upload schema form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/list";
    }

    @GetMapping("/{schemaId}")
    public String view(@PathVariable String schemaId, Model model, HttpServletRequest request, HttpSession session) {
        SpringMessages errors = new SpringMessages();

        SchemaForm form = new SchemaForm();
        /*String schemaId = httpServletRequest.getParameter("schemaId");*/
        String user = (String) session.getAttribute("user");

        /*if (schemaId == null || schemaId.trim().isEmpty()) {
            schemaId = httpServletRequest.getParameter("schema");
        }*/

        try {
            SchemaManager sm = new SchemaManager();
            SchemaElemHolder seHolder = sm.getSchemaElems(user, schemaId);
            if (seHolder == null || seHolder.getSchema() == null) {
                throw new DCMException(BusinessConstants.EXCEPTION_SCHEMA_NOT_EXIST);
            }
            schemaId = seHolder.getSchema().getId();
            form.setSchema(seHolder.getSchema().getSchema());
            form.setDescription(seHolder.getSchema().getDescription());
            form.setSchemaId(schemaId);
            form.setDtdId(seHolder.getSchema().getDtdPublicId());
            form.setElemName("");
            form.setNamespace("");
            form.setDoValidation(seHolder.getSchema().isDoValidation());
            form.setBlocker(seHolder.getSchema().isBlocker());
            form.setSchemaLang(seHolder.getSchema().getSchemaLang());
            form.setDtd(seHolder.getSchema().getIsDTD());
            String fileName = seHolder.getSchema().getUplSchemaFileName();
            form.setExpireDateObj(seHolder.getSchema().getExpireDate());
            if (seHolder.getSchema().getUplSchema() != null && !Utils.isNullStr(fileName)) {
                form.setUplSchemaId(seHolder.getSchema().getUplSchema().getUplSchemaId());
                form.setUplSchemaFileUrl(seHolder.getSchema().getUplSchema().getUplSchemaFileUrl());
                form.setLastModified(seHolder.getSchema().getUplSchema().getLastModified());
                form.setUplSchemaFileName(fileName);
                form.setUplSchemaFileUrl(Properties.gdemURL + "/schema/" + fileName);
            }
            seHolder.setSchemaIdRemoteUrl(Utils.isURL(seHolder.getSchema().getSchema())
                    && !seHolder.getSchema().getSchema().startsWith(SecurityUtil.getUrlWithContextPath(request)));
            model.addAttribute("rootElements", seHolder);
            model.addAttribute("schemaForm", form);
        } catch (DCMException e) {
            LOGGER.error("Schema element form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "/schemas/list";
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/view";
    }

    @GetMapping("/{schemaId}/edit")
    public String edit(@PathVariable String schemaId, Model model, HttpServletRequest request, HttpSession session) {
        SpringMessages errors = new SpringMessages();

        SchemaForm form = new SchemaForm();
        /*String schemaId = httpServletRequest.getParameter("schemaId");*/
        String user = (String) session.getAttribute("user");

        /*if (schemaId == null || schemaId.trim().isEmpty()) {
            schemaId = httpServletRequest.getParameter("schema");
        }*/

        try {
            SchemaManager sm = new SchemaManager();
            SchemaElemHolder seHolder = sm.getSchemaElems(user, schemaId);
            if (seHolder == null || seHolder.getSchema() == null) {
                throw new DCMException(BusinessConstants.EXCEPTION_SCHEMA_NOT_EXIST);
            }
            schemaId = seHolder.getSchema().getId();
            form.setSchema(seHolder.getSchema().getSchema());
            form.setDescription(seHolder.getSchema().getDescription());
            form.setSchemaId(schemaId);
            form.setDtdId(seHolder.getSchema().getDtdPublicId());
            form.setElemName("");
            form.setNamespace("");
            form.setDoValidation(seHolder.getSchema().isDoValidation());
            form.setBlocker(seHolder.getSchema().isBlocker());
            form.setSchemaLang(seHolder.getSchema().getSchemaLang());
            form.setDtd(seHolder.getSchema().getIsDTD());
            String fileName = seHolder.getSchema().getUplSchemaFileName();
            form.setExpireDateObj(seHolder.getSchema().getExpireDate());
            if (seHolder.getSchema().getUplSchema() != null && !Utils.isNullStr(fileName)) {
                form.setUplSchemaId(seHolder.getSchema().getUplSchema().getUplSchemaId());
                form.setUplSchemaFileUrl(seHolder.getSchema().getUplSchema().getUplSchemaFileUrl());
                form.setLastModified(seHolder.getSchema().getUplSchema().getLastModified());
                form.setUplSchemaFileName(fileName);
                form.setUplSchemaFileUrl(Properties.gdemURL + "/schema/" + fileName);
            }
            seHolder.setSchemaIdRemoteUrl(Utils.isURL(seHolder.getSchema().getSchema())
                    && !seHolder.getSchema().getSchema().startsWith(SecurityUtil.getUrlWithContextPath(request)));
            model.addAttribute("rootElements", seHolder);
            model.addAttribute("schemaForm", form);
        } catch (DCMException e) {
            LOGGER.error("Schema element form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "/schemas/list";
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/edit";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable String id, @ModelAttribute SchemaForm form, HttpServletRequest httpServletRequest, HttpSession session, RedirectAttributes redirectAttributes) {
        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String schemaId = form.getSchemaId();
        String schema = form.getSchema();
        String description = form.getDescription();
        String dtdId = form.getDtdId();
        String schemaLang = form.getSchemaLang();
        boolean doValidation = form.isDoValidation();
        Date expireDate = form.getExpireDateObj();
        boolean blocker = form.isBlocker();

/*        if (isCancelled(httpServletRequest)) {
            try {

                httpServletRequest.setAttribute("schema", sch.getSchema());
                return actionMapping.findForward("back");
            } catch (DCMException e) {
                e.printStackTrace();
                LOGGER.error("Error editing schema", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            }
        }*/
        /*errors = form.validate(actionMapping, httpServletRequest);*/
        if (errors.size() > 0) {
            redirectAttributes.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/schemas/list";
        }
        if (schema == null || schema.equals("")) {
            errors.add(messageService.getMessage("label.schema.validation"));
            redirectAttributes.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/schemas/list";
        }

        if (!(new SchemaUrlValidator().isValidUrlSet(schema))) {
            errors.add(messageService.getMessage("label.uplSchema.validation.urlFormat"));
            redirectAttributes.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/schemas/list";
        }

        String user = (String) session.getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            String schemaIdByUrl = sm.getSchemaId(schema);

            if (schemaIdByUrl != null && !schemaIdByUrl.equals(schemaId)) {
                String schemaTargetUrl = String.format("viewSchemaForm?schemaId=%s", schemaIdByUrl);
                errors.add(messageService.getMessage("label.schema.url.exists", schemaTargetUrl));
                redirectAttributes.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/schemas/list";
            }

            sm.update(user, schemaId, schema, description, schemaLang, doValidation, dtdId, expireDate, blocker);

            messages.add(messageService.getMessage("label.schema.updated"));
            redirectAttributes.addAttribute("schema", schema);
            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            LOGGER.error("Error editing schema", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);

        return "redirect:/schemas/edit";
    }

    @GetMapping("/add")
    public String add(Model model) {
        UploadSchemaForm form = new UploadSchemaForm();
        model.addAttribute("schemaForm", form);
        return "/schemas/add";
    }

    @PostMapping("/add")
    public String addSubmit(@ModelAttribute UploadSchemaForm form, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        MultipartFile schemaFile = form.getSchemaFile();
        String desc = form.getDescription();
        String schemaUrl = form.getSchemaUrl();
        boolean doValidation = form.isDoValidation();
        String schemaLang = form.getSchemaLang();
        boolean blocker = form.isBlockerValidation();

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        if ((schemaFile == null || schemaFile.getSize() == 0) && Utils.isNullStr(schemaUrl)) {
            errors.add(messageService.getMessage("label.uplSchema.validation"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/schemas/add";
        }

        if (!(new SchemaUrlValidator().isValidUrlSet(schemaUrl))) {
            errors.add(messageService.getMessage("label.uplSchema.validation.urlFormat"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/schemas/add";
        }

        try {
            SchemaManager sm = new SchemaManager();
            String fileName = "";
            String tmpSchemaUrl = "";
            // generate unique file name
            if (schemaFile != null) {
                fileName = sm.generateUniqueSchemaFilename(user, Utils.extractExtension(schemaFile.getOriginalFilename(), "xsd"));
                if (Utils.isNullStr(schemaUrl)) {
                    tmpSchemaUrl = Properties.gdemURL + "/schema/" + fileName;
                    schemaUrl = tmpSchemaUrl;
                }
            }
            // Add row to T_SCHEMA table
            String schemaID = sm.addSchema(user, schemaUrl, desc, schemaLang, doValidation, blocker);
            if (schemaFile != null && schemaFile.getSize() > 0) {
                // Change the filename to schema-UniqueIDxsd
                fileName =
                        sm.generateSchemaFilenameByID(Properties.schemaFolder, schemaID, Utils.extractExtension(schemaFile.getOriginalFilename()));
                // Add row to T_UPL_SCHEMA table
                sm.addUplSchema(user, schemaFile, fileName, schemaID);
                // Update T_SCHEMA table set
                if (!Utils.isNullStr(tmpSchemaUrl)) {
                    schemaUrl = Properties.gdemURL + "/schema/" + fileName;
                }
                sm.update(user, schemaID, schemaUrl, desc, schemaLang, doValidation, null, null, blocker);
            }
            messages.add(messageService.getMessage("label.uplSchema.inserted"));
            QAScriptListLoader.reloadList(httpServletRequest);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            LOGGER.error("Error adding upload schema", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas";
    }

    @PostMapping("/actions")
    public String delete(@ModelAttribute SingleForm form, @RequestParam String action, RedirectAttributes redirectAttributes, HttpSession session) {
        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String schemaId = Integer.toString(form.getId());

        String user_name = (String) session.getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            if ("delete".equals(action)) {
                int schemaDeleted = sm.deleteUplSchema(user_name, schemaId, true);
                if (schemaDeleted == 2) {
                    messages.add(messageService.getMessage("label.uplSchema.deleted"));
                }

                if (schemaDeleted == 1 || schemaDeleted == 3) {
                    messages.add(messageService.getMessage("label.schema.deleted"));
                }

                if (schemaDeleted == 0 || schemaDeleted == 2) {
                    errors.add(messageService.getMessage("label.uplSchema.notdeleted"));
                }
            }
            /*if (!deleteSchema) {
                httpServletRequest.setAttribute("schemaId", schemaId);
                forward = "success_deletefile";
                // clear qascript list in cache
                QAScriptListLoader.reloadList(httpServletRequest);
                StylesheetListLoader.reloadStylesheetList(httpServletRequest);
                StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
            }*/
        } catch (DCMException e) {
            LOGGER.error("Error deleting root schema", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "redirect:/schemas";
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas";
    }


    @PostMapping("/unknown/delete")
    public String deleteunknown(@ModelAttribute SingleForm form, RedirectAttributes redirectAttributes, HttpSession session) {
        SpringMessages errors = new SpringMessages();

        SchemaManager sm = new SchemaManager();
        String user = (String) session.getAttribute("user");
        String id = Integer.toString(form.getId());
        try {
            sm.deleteUplSchema(user, id, true);
        } catch (DCMException e) {
            LOGGER.error("Could not remove selected schemas", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "redirect:/schemas";
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "redirect:/schemas";
    }

    @GetMapping("/{schemaId}/conversions")
    public String conversions(@PathVariable String schemaId, Model model) {
        StylesheetListHolder st = null;
        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        try {
            SchemaManager sm = new SchemaManager();
            st = sm.getSchemaStylesheetsList(schemaId);
            model.addAttribute("conversions", st);

        } catch (DCMException e) {
            LOGGER.error("Error getting stylesheet", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute("schemaId", schemaId);
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        model.addAttribute(SpringMessages.SUCCESS_MESSAGES, success);
        return "/schemas/conversions";
    }

    @GetMapping("/{schemaId}/conversions/add")
    public String conversionsAdd(@PathVariable String schemaId, Model model) {
        // TODO: complete this
        return "/conversions/add";
    }

    @GetMapping("/{schemaId}/scripts")
    public String scripts(@PathVariable String schemaId, Model model, HttpServletRequest httpServletRequest) {
        QAScriptListHolder st = null;

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        try {
            SchemaManager sm = new SchemaManager();
            st = sm.getSchemasWithQAScripts(schemaId);
            /*model.addAttribute("scripts", QAScriptListLoader.getList(httpServletRequest));
            httpServletRequest.setAttribute("schema.qascripts", st);*/
            model.addAttribute("scripts", st);
            model.addAttribute("form", new SchemaForm());
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
        form.setSchema(schemaUrl);

        // TODO fix this
        model.addAttribute("resulttypes", SpringEventListeners.loadConvTypes(XQScript.SCRIPT_RESULTTYPES));
        model.addAttribute("scriptlangs", SpringEventListeners.loadConvTypes(XQScript.SCRIPT_LANGS));
        model.addAttribute("form", form);*/
        return "/scripts/add";
    }

}