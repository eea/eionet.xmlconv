package eionet.gdem.web.spring.conversion;

import com.mysql.jdbc.StringUtils;
import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dcm.XslGenerator;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.MessageService;
import eionet.gdem.services.db.dao.IRootElemDao;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.web.spring.FileUploadWrapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


/**
 *
 *
 */
@Controller
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

    @GetMapping
    public String list(Model model, HttpServletRequest httpServletRequest) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        try {
            httpServletRequest.setAttribute(StylesheetListLoader.STYLESHEET_LIST_ATTR, StylesheetListLoader.getStylesheetList(httpServletRequest));
        } catch (DCMException e) {
            LOGGER.error("Error getting stylesheet list", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
            model.addAttribute("errors", errors);
        }
        return "/stylesheetList.jsp";
    }

    @GetMapping("/{id}")
    public String show(Model model, HttpServletRequest httpServletRequest) {
        StylesheetListHolder st = new StylesheetListHolder();
        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        // TODO FIX THIS
        String schema = null;
        /*String schema = (String) df.get("schema");*/

        if (schema == null || schema.equals("")) {
            schema = (String) httpServletRequest.getAttribute("schema");
        }

        if (schema == null || schema.equals("")) {
            return "/";
        }
        httpServletRequest.setAttribute("schema", schema);

        try {
            SchemaManager sm = new SchemaManager();
            st = sm.getSchemaStylesheetsList(schema);
            httpServletRequest.setAttribute("schema.stylesheets", st);

        } catch (DCMException e) {
            LOGGER.error("Error getting stylesheet", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute("errors", errors);
        model.addAttribute("success", success);
        // TODO FIX URL
        return "";
    }


    @GetMapping(value = "/stylsheet/{id}", produces = "text/xml")
    public String getConversion(Model model, HttpServletResponse httpServletResponse) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        String metaXSLFolder = Properties.metaXSLFolder;
        String tableDefURL = Properties.ddURL;
        /*DynaValidatorForm loginForm = (DynaValidatorForm) actionForm;
        String id = (String) loginForm.get("id");
        String convId = (String) loginForm.get("conv");*/

        String id = "";
        String convId = "";

        try {
            ConversionDto conv = Conversion.getConversionById(convId);
            String format = metaXSLFolder + File.separatorChar + conv.getStylesheet();
            String url = tableDefURL + "/GetTableDef?id=" + id;
            ByteArrayInputStream byteIn = XslGenerator.convertXML(url, format);
            int bufLen = 0;
            byte[] buf = new byte[1024];

            /*response.setContentType("text/xml");*/
            while ((bufLen = byteIn.read(buf)) != -1) {
                httpServletResponse.getOutputStream().write(buf, 0, bufLen);
            }
            byteIn.close();
            return null;

        } catch (Exception ge) {
            LOGGER.error("Error getting stylesheet", ge);
            errors.add(messageService.getMessage("label.stylesheet.error.generation"));
            model.addAttribute("dcm.errors", errors);
            return "redirect:/web/conversion/{id}";
        }
    }


    @PostMapping
    public String listSubmit(@ModelAttribute ConversionForm cForm, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        ArrayList<Schema> schemas = new ArrayList<Schema>();
        ArrayList stylesheets = null;

        // default action forward
        String actionForward = "success";
        String idConv = null;

        String schema = cForm.getSchemaUrl();
        String url = cForm.getUrl();

        // get request parameters

        // forward to convert action
        if (httpServletRequest.getParameter("convertAction") != null && !cForm.isConverted()) {
            cForm.setConvertAction(null);
            cForm.setConverted(true);
            cForm.setAction("convert");
            actionForward = "convert";
        }
        // search conversions and display the selection on the form
        else if (httpServletRequest.getParameter("searchAction") != null) {
            // search available conversions
            try {
                SchemaManager sm = new SchemaManager();
                // ConversionService cs = new ConversionService();
                // list conversions by selected schema
                if (!Utils.isNullStr(schema)) {
                    if (!schemaExists(httpServletRequest, schema)) {
                        throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
                    }
                    stylesheets = sm.getSchemaStylesheets(schema);
                    Schema oSchema = new Schema();
                    oSchema.setSchema(schema);
                    oSchema.setStylesheets(stylesheets);
                    schemas.add(oSchema);
                    // store schema info in the form bean
                    cForm.setSchemas(schemas);
                    cForm.setInsertedUrl(null);
                }
                // sniff schema declaration from the header of XML file
                // if the xml url is stored in the session already, then use XML Schema information from the session
                else {
                    if (!Utils.isNullStr(url) && !url.equals(cForm.getInsertedUrl())) {
                        cForm.setInsertedUrl(url);
                        InputAnalyser analyser = new InputAnalyser();
                        try {
                            analyser.parseXML(url);
                        } catch (DCMException e) {
                            errors.add(messageService.getMessage(e.getErrorCode()));
                        } catch (Exception e) {
                            errors.add(messageService.getMessage(e.getMessage()));
                        }
                        // schema or dtd found from header
                        String schemaOrDTD = analyser.getSchemaOrDTD();
                        if (schemaOrDTD != null) {
                            stylesheets = sm.getSchemaStylesheets(schemaOrDTD);
                            Schema oSchema = new Schema();
                            oSchema.setSchema(schemaOrDTD);
                            oSchema.setStylesheets(stylesheets);
                            schemas.add(oSchema);
                            cForm.setSchemas(schemas);
                        }
                        // did not find schema or dtd from xml header
                        // compare root elements
                        else {
                            String root_elem = analyser.getRootElement();
                            String namespace = analyser.getNamespace();
                            Vector matchedSchemas = rootElemDao.getRootElemMatching(root_elem, namespace);
                            for (int k = 0; k < matchedSchemas.size(); k++) {
                                HashMap schemaHash = (HashMap) matchedSchemas.get(k);
                                String schema_name = (String) schemaHash.get("xml_schema");
                                stylesheets = sm.getSchemaStylesheets(schema_name);
                                Schema oSchema = new Schema();
                                oSchema.setSchema(schema_name);
                                oSchema.setStylesheets(stylesheets);
                                schemas.add(oSchema);
                            }
                            cForm.setSchemas(schemas);
                        }
                        // no schemas found from the header, show schema selection on the form
                        if (cForm.getSchemas() == null || cForm.getSchemas().size() == 0) {
                            cForm.setShowSchemaSelection(true);
                        } else {
                            cForm.setShowSchemaSelection(false);
                        }

                    }
                }
                if (cForm.getSchemas() == null || cForm.getSchemas().size() == 0) {
                    cForm.setShowSchemaSelection(true);
                } else {
                    // set default conversion ID
                    if (idConv == null && cForm.getSchemas().get(0).getStylesheets().size() > 0) {
                        idConv = ((Stylesheet) (cForm.getSchemas().get(0).getStylesheets().get(0))).getConvId();
                    }
                }
                if (idConv == null) {
                    idConv = "-1";
                }
                if (!cForm.isConverted()) {
                    httpServletRequest.getSession().setAttribute("converted.url", "");
                    httpServletRequest.getSession().setAttribute("converted.conversionId", "");
                }
                cForm.setConversionId(idConv);
                cForm.setSearchAction(null);
                cForm.setAction("search");
            } catch (DCMException e) {
                e.printStackTrace();
                LOGGER.error("Error listing conversions", e);
                errors.add(messageService.getMessage(e.getErrorCode()));
                // saveMessages(httpServletRequest, errors);
                redirectAttributes.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
                /*httpServletRequest.getSession().setAttribute("dcm.errors", errors);*/
                return "redirect/web/conversion/list";
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Error listing conversions", e);
                errors.add(messageService.getMessage(BusinessConstants.EXCEPTION_GENERAL));
                // saveMessages(httpServletRequest, errors);
                redirectAttributes.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
                /*httpServletRequest.getSession().setAttribute("dcm.errors", errors);*/
                return "redirect:/web/conversion/list";
            }
        } else {
            // comping back from convert page
            cForm.setConverted(false);
        }
        return "redirect:/old/conversions";
    }

    @PostMapping("/delete/{id}")
    public String delete(@ModelAttribute ConversionForm cForm, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        try {
            String stylesheetId = httpServletRequest.getParameter("conversionId");
            String userName = (String) httpServletRequest.getSession().getAttribute("user");

            httpServletRequest.setAttribute("schema", httpServletRequest.getParameter("schema"));
            StylesheetManager sm = new StylesheetManager();
            sm.delete(userName, stylesheetId);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            success.add(messageService.getMessage("label.stylesheet.deleted"));
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            LOGGER.error("Error deleting stylesheet", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "redirect:/web/conversion/list";
        }
        return "redirect:/web/conversion/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        StylesheetForm form = new StylesheetForm();
        String stylesheetId = httpServletRequest.getParameter("stylesheetId");

        if (stylesheetId == null || stylesheetId.equals("")) {
            stylesheetId = (String) httpServletRequest.getAttribute("stylesheetId");
        }

        ConvTypeHolder ctHolder = new ConvTypeHolder();
        StylesheetManager stylesheetManager = new StylesheetManager();

        try {
            Stylesheet stylesheet = stylesheetManager.getStylesheet(stylesheetId);

            if (stylesheet == null) {
                try {
                    httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
                catch (IOException ex) {
                    LOGGER.error("Failed to set 404 response status", ex);
                }
                /*return actionMapping.findForward(null);*/
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
                                stylesheetId).toArray());
                    }
                    form.setExistingStylesheets(existingStylesheets);
                }
            }
            ctHolder = stylesheetManager.getConvTypes();

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
            LOGGER.error("Edit stylesheet error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "redirect:/web/conversions/{id}";
        }
        //TODO why is it needed to update session attribute in each request
        httpServletRequest.getSession().setAttribute("stylesheet.outputtype", ctHolder);
        return "redirect:/web/conversions/{id}";
    }

    @GetMapping("/schemaDelete")
    public String schemaDelete(Model model, HttpServletRequest httpServletRequest) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        SchemaForm form = new SchemaForm();
        String schemaId = form.getSchemaId();

        String user_name = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            sm.deleteSchemaStylesheets(user_name, schemaId);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);


            success.add(messageService.getMessage("label.stylesheets.deleted"));
        } catch (DCMException e) {
            LOGGER.error("Error deleting schema", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        // saveErrors(httpServletRequest, errors);

        model.addAttribute("errors", errors);
        model.addAttribute("messages", success);
        return "";
    }

    @GetMapping("/type")
    public String type(Model model, HttpServletRequest httpServletRequest) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        ConvTypeHolder ctHolder = new ConvTypeHolder();
        String schema = httpServletRequest.getParameter("schema");
        httpServletRequest.setAttribute("schema", schema);

        try {
            StylesheetManager sm = new StylesheetManager();
            ctHolder = sm.getConvTypes();
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
        httpServletRequest.getSession().setAttribute("stylesheet.outputtype", ctHolder);
        model.addAttribute("success", success);
        // todo fix url
        return "";
    }


    @GetMapping("/add")
    public String add(Model model, HttpServletRequest httpServletRequest) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        StylesheetForm form = new StylesheetForm();
        Stylesheet stylesheet = ConversionsUtils.convertFormToStylesheetDto(form, httpServletRequest);

        FileUploadWrapper xslFile = form.getXslfile();
        String user = (String) httpServletRequest.getSession().getAttribute("user");
        String schema = (form.getNewSchemas() == null || form.getNewSchemas().size() == 0) ? null : form.getNewSchemas().get(0);
        httpServletRequest.setAttribute("schema", schema);
/*
        if (isCancelled(httpServletRequest)) {
            if (schema != null) {
                return new ActionForward("/do/schemaStylesheets?schema=" + schema, true);
            } else {
                return actionMapping.findForward("list");
            }
        }*/

        // TODO FIX THIS:
        // || xslFile.getFileSize() == 0) {
        if (xslFile == null) {
            errors.add(messageService.getMessage("label.stylesheet.validation"));
            model.addAttribute("errors", errors);
            return "/conversions";
        }
        String description = form.getDescription();
        if (description == null || description.isEmpty()) {
            errors.add(messageService.getMessage("label.stylesheet.error.descriptionMissing"));
            model.addAttribute("errors", errors);
            return "/conversions";
        }
        stylesheet.setXslFileName(xslFile.getFile().getName());
        try {
            // TODO FIX THIS: xslFile.getFileData()
            stylesheet.setXslContent(new String(xslFile.getFile().getBytes(), "UTF-8"));
        } catch (Exception e) {
            LOGGER.error("Error in edit stylesheet action when trying to load XSL file content from FormFile object", e);
            errors.add(messageService.getMessage(BusinessConstants.EXCEPTION_GENERAL));
        } finally {
            /*xslFile.destroy();*/
        }
        ConversionsUtils.validateXslFile(stylesheet, errors);

        if (errors.isEmpty()) {
            try {
                StylesheetManager stylesheetManager = new StylesheetManager();
                // stylesheetManager.add(user, schema, xslFile, type, desc, dependsOn);
                stylesheetManager.add(stylesheet, user);
                success.add(messageService.getMessage("label.stylesheet.inserted"));
                StylesheetListLoader.reloadStylesheetList(httpServletRequest);
                StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
            } catch (DCMException e) {
                LOGGER.error("Add stylesheet error", e);
                errors.add(messageService.getMessage(e.getErrorCode()));
            }
        }
        model.addAttribute("errors", errors);
        model.addAttribute("success", success);
        if (!StringUtils.isNullOrEmpty(schema)) {
            return "/do/schemaStylesheets?schema=" + schema;
        } else {
            return "/conversions";
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
