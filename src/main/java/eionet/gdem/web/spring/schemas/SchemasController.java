package eionet.gdem.web.spring.schemas;

import eionet.acl.SignOnException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private SchemasService schemasService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemasController.class);

    class schemaViewHelper{
        Model model;
        SpringMessages errors;
        SchemaForm form;

    }
    @Autowired
    public SchemasController(MessageService messageService, SchemasService schemasService) {
        this.messageService = messageService;
        this.schemasService = schemasService;
    }

    @ModelAttribute
    public void init(Model model, HttpSession session) {

        String user = (String) session.getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            UplSchemaHolder holder = sm.getAllSchemas(user);
            if (holder.getSchemas()!=null) {
                for (Schema schema : holder.getSchemas()) {
                    schema.setMaxExecutionTimeUI(Utils.createFormatForMs(schema.getMaxExecutionTime()));
                }
            }
            model.addAttribute("schemas", holder);
        } catch (DCMException e) {
            throw new RuntimeException("Could not retrieve schema list: " + messageService.getMessage(e.getErrorCode()));
        }
    }

    @GetMapping
    public String list(@ModelAttribute("form") SchemaForm form) {
        return "/schemas/list";
    }


    @GetMapping("/add")
    public String add(@ModelAttribute("form") UploadSchemaForm form) {
        form.setMaxExecutionTime(Properties.maxSchemaExecutionTime);
        return "/schemas/add";
    }

    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("form") UploadSchemaForm form, HttpServletRequest httpServletRequest,
                            BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            if (!SecurityUtil.hasPerm(user, "/schema", "i")) {
                throw new AccessDeniedException(messageService.getMessage("error.inoperm", "label.schemas.title"));
            }
        } catch (SignOnException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }

        MultipartFile schemaFile = form.getSchemaFile();
        String desc = form.getDescription();
        String schemaUrl = form.getSchemaUrl();
        boolean doValidation = form.isDoValidation();
        String schemaLang = form.getSchemaLang();
        boolean blocker = form.isBlockerValidation();
        Long maxExecutionTime = form.getMaxExecutionTime();

        new UploadSchemaFormValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/schemas/add";
        }

        try {
            SchemaManager sm = new SchemaManager();
            String fileName = "";
            String tmpSchemaUrl = "";
            // generate unique file name
            if (schemaFile != null && schemaFile.getSize() > 0) {
                fileName = sm.generateUniqueSchemaFilename(user, Utils.extractExtension(schemaFile.getOriginalFilename(), "xsd"));
                if (Utils.isNullStr(schemaUrl)) {
                    tmpSchemaUrl = Properties.gdemURL + "/schema/" + fileName;
                    schemaUrl = tmpSchemaUrl;
                }
            }
            // Add row to T_SCHEMA table
            String schemaID = sm.addSchema(user, schemaUrl, desc, schemaLang, doValidation, blocker, maxExecutionTime);
            if (schemaFile != null && schemaFile.getSize() > 0) {
                // Change the filename to schema-UniqueIDxsd
                fileName = sm.generateSchemaFilenameByID(Properties.schemaFolder, schemaID, Utils.extractExtension(schemaFile.getOriginalFilename()));
                // Add row to T_UPL_SCHEMA table
                sm.addUplSchema(user, schemaFile, fileName, schemaID);
                // Update T_SCHEMA table set
                if (!Utils.isNullStr(tmpSchemaUrl)) {
                    schemaUrl = Properties.gdemURL + "/schema/" + fileName;
                }
                sm.update(user, schemaID, schemaUrl, desc, schemaLang, doValidation, null, null, blocker, maxExecutionTime);
            }
            messages.add(messageService.getMessage("label.uplSchema.inserted"));
            QAScriptListLoader.reloadList(httpServletRequest);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            throw new RuntimeException("Error adding upload schema: " + messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas";
    }

    @GetMapping("/{schemaId}")
    public String displayBySchemaId(@PathVariable String schemaId, @ModelAttribute("form") SchemaForm form,
                                    Model model, HttpServletRequest request, HttpSession session) {
        SpringMessages errors = new SpringMessages();

        String user = (String) session.getAttribute("user");

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
            form.setExpireDate(seHolder.getSchema().getExpireDate());
            form.setMaxExecutionTime(seHolder.getSchema().getMaxExecutionTime());
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
            model.addAttribute("schemaId", schemaId);
        } catch (DCMException e) {
            throw new RuntimeException("Schema element form error: " + e.getErrorCode());
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/view";
    }


    @GetMapping("/one")
    public String displayBySchemaUrl(@RequestParam(value="schemaUrl") String schemaUrl, @ModelAttribute("form") SchemaForm form,
                                     Model model, HttpServletRequest request, HttpSession session) {

        return this.displayBySchemaId(schemaUrl,form,model,request,session);
    }


    @GetMapping("/{schemaId}/edit")
    public String edit(@PathVariable String schemaId, @ModelAttribute("form") SchemaForm form,
                       Model model, HttpServletRequest request) {
        SpringMessages errors = new SpringMessages();
        String user = (String) request.getSession().getAttribute("user");

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
            form.setExpireDate(seHolder.getSchema().getExpireDate());
            form.setMaxExecutionTime(seHolder.getSchema().getMaxExecutionTime());
            if (seHolder.getSchema().getUplSchema() != null && !Utils.isNullStr(fileName)) {
                form.setUplSchemaId(seHolder.getSchema().getUplSchema().getUplSchemaId());
                form.setUplSchemaFileUrl(seHolder.getSchema().getUplSchema().getUplSchemaFileUrl());
                form.setLastModified(seHolder.getSchema().getUplSchema().getLastModified());
                form.setUplSchemaFileName(fileName);
                form.setUplSchemaFileUrl(Properties.gdemURL + "/schema/" + fileName);
            }
            seHolder.setSchemaIdRemoteUrl(Utils.isURL(seHolder.getSchema().getSchema())
                    && !seHolder.getSchema().getSchema().startsWith(SecurityUtil.getUrlWithContextPath(request)));
            model.addAttribute("schemaId", schemaId);
            model.addAttribute("rootElements", seHolder);
            model.addAttribute("form", form);
        } catch (DCMException e) {
            throw new RuntimeException("Schema element form error: " + e.getErrorCode());
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/edit";
    }

    @PostMapping(params = {"update"})
    public String editSubmit(@ModelAttribute("form") SchemaForm form, HttpServletRequest httpServletRequest,Model model,
                             BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {
        SpringMessages messages = new SpringMessages();

        String schemaId = form.getSchemaId();
        String schema = form.getSchema();
        String description = form.getDescription();
        String dtdId = form.getDtdId();
        String schemaLang = form.getSchemaLang();
        boolean doValidation = form.isDoValidation();
        Date expireDate = form.getExpireDate();
        boolean blocker = form.isBlocker();
        Long maxExecutionTime = form.getMaxExecutionTime();

        new SchemaFormValidator().validate(form, bindingResult);
        model.addAttribute("schemaId", schemaId);
        if (bindingResult.hasErrors()) {
            return "/schemas/edit";
        }

        String user = (String) session.getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            String schemaIdByUrl = sm.getSchemaId(schema);
            if (schemaIdByUrl != null && !schemaIdByUrl.equals(schemaId)) {
                String schemaTargetUrl = String.format("viewSchemaForm?schemaId=%s", schemaIdByUrl);
                bindingResult.reject(messageService.getMessage("label.schema.url.exists", schemaTargetUrl));
                return "/schemas/edit";
            }

            sm.update(user, schemaId, schema, description, schemaLang, doValidation, dtdId, expireDate, blocker, maxExecutionTime);
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

    @PostMapping(params = {"delete"})
    public String delete(@ModelAttribute("form") SchemaForm form,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        SpringMessages messages = new SpringMessages();

        String schemaId = form.getSchemaId();
        String user_name = (String) httpServletRequest.getSession().getAttribute("user");

        new SchemaFormValidator().validateDelete(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/schemas/list";
        }

        try {
            SchemaManager sm = new SchemaManager();
            int schemaDeleted = sm.deleteUplSchema(user_name, schemaId, true);
            if (schemaDeleted == 2) {
                messages.add(messageService.getMessage("label.uplSchema.deleted"));
            }

            if (schemaDeleted == 1 || schemaDeleted == 3) {
                messages.add(messageService.getMessage("label.schema.deleted"));
            }

            if (schemaDeleted == 0 || schemaDeleted == 2) {
                messages.add(messageService.getMessage("label.uplSchema.notdeleted"));
            }

            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            throw new RuntimeException("Error deleting root schema: " + e.getErrorCode());
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas";
    }


}