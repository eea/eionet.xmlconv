package eionet.gdem.web.spring.schemas;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.schemas.SchemaElemHolder;
import eionet.gdem.web.spring.schemas.UplSchemaHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
            e.printStackTrace();
            LOGGER.error("Upload schema form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/list";
    }

    @GetMapping("/{schemaId}")
    public String show(@PathVariable String schemaId, Model model, HttpServletRequest request, HttpSession session) {
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
            model.addAttribute("schema.rootElements", seHolder);
            model.addAttribute("stylesheet.outputtype", seHolder);
            model.addAttribute("schemaForm", form);
        } catch (DCMException e) {
            LOGGER.error("Schema element form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "/schemas/list";
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/view";
    }

    @GetMapping("/add")
    public String add(Model model) {
        UploadSchemaForm form = new UploadSchemaForm();
        model.addAttribute("schemaForm", form);
        return "/schemas/add";
    }


}