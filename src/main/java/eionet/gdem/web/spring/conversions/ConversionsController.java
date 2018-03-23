package eionet.gdem.web.spring.conversions;

import eionet.gdem.Constants;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.PathNotFoundException;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlException;
import eionet.gdem.utils.xml.sax.SaxContext;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.schemas.IRootElemDao;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.schemas.SchemaForm;
import eionet.gdem.web.spring.stylesheet.ConvTypeHolder;
import eionet.gdem.web.spring.stylesheet.ConversionsUtils;
import eionet.gdem.web.spring.stylesheet.StylesheetForm;
import eionet.gdem.web.spring.stylesheet.StylesheetListHolder;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
@Controller("webConversions")
@RequestMapping("/conversions")
public class ConversionsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsController.class);
    private MessageService messageService;
    private IRootElemDao rootElemDao;

    @Autowired
    public ConversionsController(MessageService messageService, IRootElemDao rootElemDao) {
        this.messageService = messageService;
        this.rootElemDao = rootElemDao;
    }

    /**
     * Helper method to keep model attributes in every request
     * This also works on error pages.
     */
    @ModelAttribute
    public void init(Model model) {
        ConvTypeHolder ctHolder = null;
        StylesheetManager stylesheetManager = new StylesheetManager();
        try {
            ctHolder = stylesheetManager.getConvTypes();
        } catch (DCMException e) {
            throw new RuntimeException("Error: " + messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute("outputtypes", ctHolder);
    }

    @GetMapping
    public String list(Model model, HttpServletRequest httpServletRequest) {
        try {
            model.addAttribute("conversions", StylesheetListLoader.getStylesheetList(httpServletRequest));
        } catch (DCMException e) {
            throw new RuntimeException("Error getting stylesheet list: " + messageService.getMessage(e.getErrorCode()));
        }
        return "/conversions/list";
    }

    @GetMapping("/{conversionId}")
    public String view(@ModelAttribute("form") StylesheetForm form, @PathVariable String conversionId, Model model) {

        StylesheetManager stylesheetManager = new StylesheetManager();

        try {
            Stylesheet stylesheet = stylesheetManager.getStylesheet(conversionId);

            if (stylesheet == null) {
                throw new PathNotFoundException("Conversion does not exist");
            }

            form.setDescription(stylesheet.getDescription());
            form.setOutputtype(stylesheet.getType());
            form.setStylesheetId(stylesheet.getConvId());
            form.setXsl(stylesheet.getXsl());
            form.setXslContent(stylesheet.getXslContent());
            form.setXslFileName(stylesheet.getXslFileName());
            form.setModified(stylesheet.getModified());
            form.setChecksum(stylesheet.getChecksum());
            form.setSchemas(stylesheet.getSchemas());
            // set empty string if dependsOn is null to avoid struts error in define tag:
            // Define tag cannot set a null value
            form.setDependsOn(stylesheet.getDependsOn() == null ? "" : stylesheet.getDependsOn());

            if (stylesheet.getSchemas().size() > 0) {
                //set first schema for Run Conversion link
                form.setSchema(stylesheet.getSchemas().get(0).getSchema());
                // check if any related schema has type=EXCEL, if yes, then depends on info should be visible
                List<Schema> relatedSchemas = new ArrayList<Schema>(stylesheet.getSchemas());
                CollectionUtils.filter(relatedSchemas, new BeanPredicate("schemaLang", new EqualPredicate("EXCEL")));
                if (relatedSchemas.size() > 0) {
                    form.setShowDependsOnInfo(true);
                    List<Stylesheet> existingStylesheets = new ArrayList<Stylesheet>();
                    for (Schema relatedSchema : relatedSchemas) {
                        CollectionUtils.addAll(existingStylesheets, stylesheetManager.getSchemaStylesheets(relatedSchema.getId(),
                                conversionId).toArray());
                    }
                    form.setExistingStylesheets(existingStylesheets);
                }
            }

            /** FIXME - do we need the list of DD XML Schemas on the page
             StylesheetListHolder stylesheetList = StylesheetListLoader.getGeneratedList(httpServletRequest);
             List<Schema> schemas = stylesheetList.getDdStylesheets();
             httpServletRequest.setAttribute("stylesheet.DDSchemas", schemas);
             */


            /*
            String schemaId = schema.getSchemaId(stylesheet.getSchema());
            if (!Utils.isNullStr(schemaId)) {
                httpServletRequest.setAttribute("schemaInfo", schema.getSchema(schemaId));
                httpServletRequest.setAttribute("existingStylesheets", stylesheetManager.getSchemaStylesheets(schemaId, stylesheetId));
            }
            */
            //httpServletRequest.setAttribute(StylesheetListLoader.STYLESHEET_LIST_ATTR, StylesheetListLoader.getStylesheetList(httpServletRequest));

        } catch (DCMException e) {
            throw new RuntimeException("Edit stylesheet error: " + messageService.getMessage(e.getErrorCode()));
        }

        return "/conversions/view";
    }

    @GetMapping("/{conversionId}/delete")
    public String delete(@PathVariable String conversionId, Model model,
                         HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages success = new SpringMessages();

        String userName = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            StylesheetManager sm = new StylesheetManager();
            sm.delete(userName, conversionId);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
            success.add(messageService.getMessage("label.stylesheet.deleted"));
        } catch (DCMException e) {
            throw new RuntimeException("Error deleting stylesheet: " + messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, success);
        return "redirect:/conversions";
    }

    @PostMapping(params = "delete")
    public String deleteSubmit(@ModelAttribute("form") ConversionForm cForm, Model model,
                         HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages success = new SpringMessages();

        String userName = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            StylesheetManager sm = new StylesheetManager();
            String stylesheetId = cForm.getConversionId();
            redirectAttributes.addFlashAttribute("schema", cForm.getSchema());
            sm.delete(userName, stylesheetId);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
            success.add(messageService.getMessage("label.stylesheet.deleted"));
        } catch (DCMException e) {
            throw new RuntimeException("Error deleting stylesheet: " + messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, success);
        return "redirect:/conversions";
    }

    @GetMapping("/{id}/edit")
    public String edit(@ModelAttribute("form") StylesheetForm form, @PathVariable String id) {

        StylesheetManager stylesheetManager = new StylesheetManager();

        try {
            Stylesheet stylesheet = stylesheetManager.getStylesheet(id);

            if (stylesheet == null) {
                throw new PathNotFoundException("Conversion does not exist");
            }

            form.setDescription(stylesheet.getDescription());
            form.setOutputtype(stylesheet.getType());
            form.setStylesheetId(stylesheet.getConvId());
            form.setXsl(stylesheet.getXsl());
            form.setXslContent(stylesheet.getXslContent());
            form.setXslFileName(stylesheet.getXslFileName());
            form.setModified(stylesheet.getModified());
            form.setChecksum(stylesheet.getChecksum());
            form.setSchemas(stylesheet.getSchemas());
            // set empty string if dependsOn is null to avoid struts error in define tag:
            // Define tag cannot set a null value
            form.setDependsOn(stylesheet.getDependsOn() == null ? "" : stylesheet.getDependsOn());

            if (stylesheet.getSchemas().size() > 0) {
                //set first schema for Run Conversion link
                form.setSchema(stylesheet.getSchemas().get(0).getSchema());
                // check if any related schema has type=EXCEL, if yes, then depends on info should be visible
                List<Schema> relatedSchemas = new ArrayList<Schema>(stylesheet.getSchemas());
                CollectionUtils.filter(relatedSchemas, new BeanPredicate("schemaLang", new EqualPredicate("EXCEL")));
                if (relatedSchemas.size() > 0) {
                    form.setShowDependsOnInfo(true);
                    List<Stylesheet> existingStylesheets = new ArrayList<Stylesheet>();
                    for (Schema relatedSchema : relatedSchemas) {
                        CollectionUtils.addAll(existingStylesheets, stylesheetManager.getSchemaStylesheets(relatedSchema.getId(), id).toArray());
                    }
                    form.setExistingStylesheets(existingStylesheets);
                }
            }

            /** FIXME - do we need the list of DD XML Schemas on the page
             StylesheetListHolder stylesheetList = StylesheetListLoader.getGeneratedList(httpServletRequest);
             List<Schema> schemas = stylesheetList.getDdStylesheets();
             httpServletRequest.setAttribute("stylesheet.DDSchemas", schemas);
             */


            /*
            String schemaId = schema.getSchemaId(stylesheet.getSchema());
            if (!Utils.isNullStr(schemaId)) {
                httpServletRequest.setAttribute("schemaInfo", schema.getSchema(schemaId));
                httpServletRequest.setAttribute("existingStylesheets", stylesheetManager.getSchemaStylesheets(schemaId, stylesheetId));
            }
            */
            //httpServletRequest.setAttribute(StylesheetListLoader.STYLESHEET_LIST_ATTR, StylesheetListLoader.getStylesheetList(httpServletRequest));

        } catch (DCMException e) {
            throw new RuntimeException("Edit stylesheet error: " + messageService.getMessage(e.getErrorCode()));
        }
        return "/conversions/edit";
    }

    @PostMapping(params = {"upload"})
    public String upload(@ModelAttribute("form") StylesheetForm form, BindingResult formResult,
                         Model model, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {
        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        Stylesheet stylesheet = ConversionsUtils.convertFormToStylesheetDto(form, httpServletRequest);

        MultipartFile xslFile = form.getXslfile();
        String user = (String) httpServletRequest.getSession().getAttribute("user");
        boolean updateContent = false;

        httpServletRequest.setAttribute("stylesheetId", stylesheet.getConvId());

/*        if (isCancelled(httpServletRequest)) {
            return findForward(actionMapping, "success", stylesheet.getConvId());
        }*/
        String description = form.getDescription();
        if (description == null || description.isEmpty()) {
            formResult.reject("label.stylesheet.error.descriptionMissing");
        }
        if (xslFile != null && xslFile.getSize() != 0) {
            if (StringUtils.isEmpty(stylesheet.getXslFileName())) {
                stylesheet.setXslFileName(xslFile.getOriginalFilename());
            }
            try {
                stylesheet.setXslContent(new String(xslFile.getBytes(), StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException("Error in edit stylesheet action when trying to load XSL file content from FormFile object", e);
            }

            ConversionsUtils.validateXslFile(stylesheet, errors);
            updateContent = true;
        }

        if (errors.isEmpty()) {
            try {
                StylesheetManager stylesheetManager = new StylesheetManager();
                // stylesheetManager.update(user, stylesheetId, schema, xslFile, curFileName, type, desc, dependsOn);
                stylesheetManager.update(stylesheet, user, updateContent);
                if (updateContent) {
                    messages.add(messageService.getMessage("label.stylesheet.updated"));
                } else {
                    messages.add(messageService.getMessage("label.stylesheet.updated.notuploaded"));
                }
                StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            } catch (DCMException e) {
                throw new RuntimeException("Edit stylesheet error", e);
            }
        }

        /*if (!errors.isEmpty()) {
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("fail");
        }*/
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/conversions/" + stylesheet.getConvId();
    }

    @PostMapping(params = {"save"})
    public String save(@ModelAttribute("form") StylesheetForm form, BindingResult formResult,
                       Model model, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        Stylesheet stylesheet = ConversionsUtils.convertFormToStylesheetDto(form, httpServletRequest);

        String user = (String) httpServletRequest.getSession().getAttribute("user");
        String oldFileChecksum = form.getChecksum();
        boolean updateContent = false;
        String newChecksum = null;

        httpServletRequest.setAttribute("stylesheetId", stylesheet.getConvId());

/*        if (isCancelled(httpServletRequest)) {
            return findForward(actionMapping, "success", stylesheet.getConvId());
        }*/
        String description = form.getDescription();
        if (description == null || description.isEmpty()) {
            formResult.reject("label.stylesheet.error.descriptionMissing");
        }
        if (!Utils.isNullStr(stylesheet.getXslFileName()) && !Utils.isNullStr(stylesheet.getXslContent())
                && stylesheet.getXslContent().indexOf(Constants.FILEREAD_EXCEPTION) == -1) {

            // compare checksums
            try {
                newChecksum = Utils.getChecksumFromString(stylesheet.getXslContent());
            } catch (Exception e) {
                LOGGER.error("unable to create checksum");
            }

            updateContent = StringUtils.isEmpty(oldFileChecksum) || !oldFileChecksum.equals(newChecksum);

            if (updateContent) {
                ConversionsUtils.validateXslFile(stylesheet, errors);
            }
        }

        if (errors.isEmpty()) {
            try {
                StylesheetManager stylesheetManager = new StylesheetManager();
                // st.updateContent(user, stylesheetId, schema, xslFileName, type, desc, xslContent, updateContent, dependsOn);
                stylesheetManager.update(stylesheet, user, updateContent);
                messages.add(messageService.getMessage("label.stylesheet.updated"));
                StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            } catch (DCMException e) {
                throw new RuntimeException("Edit stylesheet error", e);
            }
        }

        /*if (!errors.isEmpty()) {
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("fail");
        }*/
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/conversions/" + stylesheet.getConvId();

    }

    @GetMapping("/type")
    public String type(Model model, HttpServletRequest httpServletRequest) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        String schema = httpServletRequest.getParameter("schema");
        httpServletRequest.setAttribute("schema", schema);

        try {
            StylesheetManager sm = new StylesheetManager();
            SchemaManager schemaMan = new SchemaManager();

            StylesheetListHolder stylesheetList = StylesheetListLoader.getGeneratedList(httpServletRequest);
            List<Schema> schemas = stylesheetList.getDdStylesheets();
            httpServletRequest.setAttribute("stylesheet.DDSchemas", schemas);

            if (!Utils.isNullStr(schema)) {
                String schemaId = schemaMan.getSchemaId(schema);
                if (schemaId != null) {
                    httpServletRequest.setAttribute("schemaInfo", schemaMan.getSchema(schemaId));
                    httpServletRequest.setAttribute("existingStylesheets", sm.getSchemaStylesheets(schemaId, null));
                }
            }

        } catch (DCMException e) {
            LOGGER.error("Error getting conv types", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            model.addAttribute("errors", errors);
        }
        model.addAttribute("success", success);
        // todo fix url
        return "/conversions/type";
    }


    @GetMapping("/add")
    public String add(@ModelAttribute("form") StylesheetForm form) {
        return "/conversions/add";
    }

    @PostMapping(params = "add")
    public String addSubmit(@ModelAttribute("form") StylesheetForm form, BindingResult formResult,
                            Model model, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {
        SpringMessages success = new SpringMessages();

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        new StylesheetValidator().validate(form, formResult);
        if (formResult.hasErrors()) {
            return "/conversions/add";
        }

        Stylesheet stylesheet = new Stylesheet();
        stylesheet.setConvId(form.getStylesheetId());
        stylesheet.setDescription(form.getDescription());
        stylesheet.setType(form.getOutputtype());
        stylesheet.setDependsOn(form.getDependsOn());
        stylesheet.setXslFileName(form.getXslFileName());
        stylesheet.setXslContent(form.getXslContent());
        stylesheet.setSchemaUrls(form.getNewSchemas());

        MultipartFile xslFile = form.getXslfile();
        String schemaId = form.getSchemaId();
        String schema = (form.getNewSchemas() == null || form.getNewSchemas().size() == 0) ? null : form.getNewSchemas().get(0);
        stylesheet.setXslFileName(xslFile.getOriginalFilename());
        try {
            stylesheet.setXslContent(new String(xslFile.getBytes(), "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("File reading error: " + e.getMessage());
        } finally {
            /*xslFile.destroy();*/
        }

        try {
            IXmlCtx x = new SaxContext();
            x.setWellFormednessChecking();
            x.checkFromString(stylesheet.getXslContent());
        } catch (XmlException e) {
            LOGGER.error("Add stylesheet error", e);
            formResult.rejectValue("xslfile", "Invalid XSL file: " + e.getMessage());
        }

        if (formResult.hasErrors()) {
            return "/conversions/add";
        }

        try {
            StylesheetManager stylesheetManager = new StylesheetManager();
            // stylesheetManager.add(user, schema, xslFile, type, desc, dependsOn);
            stylesheetManager.add(stylesheet, user);
            success.add(messageService.getMessage("label.stylesheet.inserted"));
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            throw new RuntimeException("Add stylesheet error: " + messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, success);
        if (!StringUtils.isEmpty(schemaId)) {
            return "redirect:/schemas/" + schemaId + "/conversions";
        } else {
            return "redirect:/conversions/";
        }
    }

    /**
     * check if schema passed as request parameter exists in the list of schemas stored in the session. If there is no schema list
     * in the session, then create it
     *
     * @param httpServletRequest Request
     * @param schema Schema
     * @return True if schema exists
     * @throws DCMException If an error occurs.
     */
    private boolean schemaExists(HttpServletRequest httpServletRequest, String schema) throws DCMException {
        List<Schema> schemasInCache = StylesheetListLoader.getConversionSchemasList(httpServletRequest);

        Schema oSchema = new Schema();
        oSchema.setSchema(schema);
        return schemasInCache.contains(oSchema);
    }
}
