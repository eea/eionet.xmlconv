package eionet.gdem.web.spring.converter;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.dto.ConvertedFileDto;
import eionet.gdem.dto.CrFileDto;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.functions.Json;
import eionet.gdem.services.MessageService;
import eionet.gdem.services.db.dao.IRootElemDao;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.cdr.UrlUtils;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.conversions.ConversionForm;
import eionet.gdem.web.spring.conversions.ConversionsController;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 *
 */
@Controller
@RequestMapping("/converter")
public class ConverterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsController.class);

    private MessageService messageService;

    private IRootElemDao rootElemDao;

    @Autowired
    public ConverterController(MessageService messageService, IRootElemDao rootElemDao) {
        this.messageService = messageService;
        this.rootElemDao = rootElemDao;
    }

    @GetMapping
    public String list(@ModelAttribute ConversionForm form, Model model) {
        /*ConversionForm form = new ConversionForm();*/
        model.addAttribute("conversionForm", form);
        return "/converter/list";
    }

    @PostMapping
    public String listSubmit(@ModelAttribute ConversionForm cForm, HttpServletRequest httpServletRequest, Model model, RedirectAttributes redirectAttributes) {
        //String ticket = (String) httpServletRequest.getSession().getAttribute(Names.TICKET_ATT);
        SpringMessages errors = new SpringMessages();

        ArrayList<Schema> schemas = new ArrayList<Schema>();
        ArrayList stylesheets = null;

        /*// default action forward */
        String actionForward = "success";
        String idConv = null;
        String schema = cForm.getSchemaUrl();
        String url = cForm.getUrl();

        if ("convertAction".equals(cForm.getAction()) && !cForm.isConverted()) {
            cForm.setConvertAction(null);
            cForm.setConverted(true);
            cForm.setAction("convert");
            actionForward = "convert";
        }
        // search conversions and display the selection on the form
        else if ("searchAction".equals(cForm.getAction())) {
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
                            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                            // httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                        } catch (Exception e) {
                            errors.add(messageService.getMessage(e.getMessage()));
                            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                            // httpServletRequest.getSession().setAttribute("dcm.errors", errors);
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
                    redirectAttributes.addFlashAttribute("converted.url", "");
                    redirectAttributes.addFlashAttribute("converted.conversionId", "");
                }
                cForm.setConversionId(idConv);
                cForm.setSearchAction(null);
                cForm.setAction("search");
            } catch (DCMException e) {
                LOGGER.error("Error listing conversions", e);
                errors.add(messageService.getMessage(e.getErrorCode()));
                // saveMessages(httpServletRequest, errors);
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/converter";
            } catch (Exception e) {
                LOGGER.error("Error listing conversions", e);
                errors.add(messageService.getMessage(BusinessConstants.EXCEPTION_GENERAL));
                // saveMessages(httpServletRequest, errors);
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/converter";
            }
        } else {
            // comping back from convert page
            cForm.setConverted(false);
        }
        redirectAttributes.addFlashAttribute("conversionForm", cForm);
        return "redirect:/converter";
    }

    @GetMapping("/search")
    public String searchXML(@ModelAttribute ConversionForm cForm, Model model, HttpServletRequest httpServletRequest) {
        SpringMessages errors = new SpringMessages();

        try {
            model.addAttribute("schemas", StylesheetListLoader.getConversionSchemasList(httpServletRequest));
        } catch (DCMException e) {
            LOGGER.error("Serach CR Conversions error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }
        model.addAttribute("conversionForm", cForm);
        return "/converter/search";
    }

    @PostMapping("/search")
    public String searchXMLSubmit(@ModelAttribute ConversionForm cForm, HttpServletRequest httpServletRequest, Model model, RedirectAttributes redirectAttributes) {

        /*String ticket = (String) httpServletRequest.getSession().getAttribute(Constants.TICKET_ATT);*/
        SpringMessages errors = new SpringMessages();
        String idConv = null;
        Schema oSchema = null;

        // request comes from SchemaStyleheets pagew
        /*if (httpServletRequest.getParameter("conversionId") != null) {
            idConv = httpServletRequest.getParameter("conversionId");
            httpServletRequest.getSession().setAttribute("converted.conversionId", idConv);
        }*/

        String schema = cForm.getSchemaUrl();
        oSchema = cForm.getSchema();

        try {
            SchemaManager sm = new SchemaManager();
            ConversionService cs = new ConversionService();
            // use the Schema data from the session, if schema is the same
            // otherwise load the data from database and search CR
            if (!Utils.isNullStr(schema) && (oSchema == null || !oSchema.getSchema().equals(schema))) {
                if (!schemaExists(httpServletRequest, schema)) {
                    throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
                }
                ArrayList stylesheets = null;
                List<CrFileDto> crfiles = null;
                stylesheets = sm.getSchemaStylesheets(schema);
                crfiles = sm.getCRFiles(schema);
                oSchema = new Schema();
                oSchema.setSchema(schema);
                oSchema.setStylesheets(stylesheets);
                oSchema.setCrfiles(crfiles);

                if (idConv == null && oSchema.getStylesheets().size() > 0) {
                    idConv = ((Stylesheet) (oSchema.getStylesheets().get(0))).getConvId();
                }
                if (idConv == null) {
                    idConv = "-1";
                }
                cForm.setSchema(oSchema);
                cForm.setConversionId(idConv);

                httpServletRequest.getSession().setAttribute("converted.url", "");
                httpServletRequest.getSession().setAttribute("converted.conversionId", "");
            }
        } catch (DCMException e) {
            LOGGER.error("Error searching XML files", e);
            errors.add(messageService.getMessage((e.getErrorCode())));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/converter";
        } catch (Exception e) {
            LOGGER.error("Error searching XML files", e);
            errors.add(messageService.getMessage(BusinessConstants.EXCEPTION_GENERAL));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/converter";
        }
        redirectAttributes.addFlashAttribute("conversionForm", cForm);
        return "redirect:/converter/search";
    }

    @GetMapping("/excel2xml")
    public String excel2xml(@ModelAttribute("form") Excel2xmlForm form, @ModelAttribute("conversionLog") String conversionLog, Model model) {
        if (form != null) {
            form.setSplit("all");
        }
        model.addAttribute("form", form);
        model.addAttribute("conversionLog", conversionLog);
        model.addAttribute("conversionLinks", model.asMap().get("conversionLinks"));
        return "/converter/excel2xml";
    }

    @PostMapping("/excel2xml")
    public String excel2xmlSubmit(@ModelAttribute Excel2xmlForm form, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();

        String ticket = (String) session.getAttribute(Constants.TICKET_ATT);

        String url = processFormStr(form.getUrl());
        String split = processFormStr(form.getSplit());
        String sheet = processFormStr(form.getSheet());
        Boolean showConversionLog = processFormBoolean(form.isConversionLog());

        // get request parameters
        try {
            // parse request parameters
            if (Utils.isNullStr(url)) {
                errors.add(messageService.getMessage("label.conversion.insertExcelUrl"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/converter/excel2xml";
            }
            if (Utils.isNullStr(split)) {
                errors.add(messageService.getMessage("label.conversion.insertSplit"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/converter/excel2xml";
            }
            if (split.equals("split") && Utils.isNullStr(sheet) && !showConversionLog) {
                errors.add(messageService.getMessage("label.conversion.insertSheet"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/converter/excel2xml";
            }
            ConversionServiceIF cs = new ConversionService();
            cs.setTicket(ticket);
            cs.setTrustedMode(true);
            ConversionResultDto conversionResult = null;
            // execute conversion
            if (split.equals("split")) {
                conversionResult = cs.convertDD_XML(url, true, sheet);
            } else {
                conversionResult = cs.convertDD_XML(url, true, null);
            }
            List<String> conversionLinks = new ArrayList<>();
            for (ConvertedFileDto dto : conversionResult.getConvertedFiles()) {
                conversionLinks.add("//" + Properties.appHost + "/" + Properties.contextPath + "/tmp/" + UrlUtils.getFileName(dto.getFilePath()));
            }
            redirectAttributes.addFlashAttribute("conversionLinks", conversionLinks);
            String conversionLog  = conversionResult.getConversionLogAsHtml();
            if (!Utils.isNullStr(conversionLog)){
                form.setConversionLog(true);
                redirectAttributes.addFlashAttribute("conversionLog", conversionLog);
            } else {
                form.setConversionLog(false);
            }


            redirectAttributes.addFlashAttribute("form", form);
        } catch (XMLConvException e) {
            LOGGER.error("Error testing conversion", e);
            errors.add(messageService.getMessage(e.getMessage()));
            return "redirect:/converter/excel2xml";
        }
        return "redirect:/converter/excel2xml";
    }

    @GetMapping("/json2xml")
    public String json2xml(Model model) {
        Json2xmlForm form = new Json2xmlForm();
        model.addAttribute("form", form);
        return "/converter/json2xml";
    }

    @PostMapping("json2xml")
    public String json2xmlSubmit(@ModelAttribute Json2xmlForm form, RedirectAttributes redirectAttributes) {

        String content = form.getContent();
        String xml = null;
        try {
            if (content == null) {
                throw new XMLConvException("Missing request parameter: ");
            }
        //TODO update JSON library.
        xml = Json.jsonString2xml(content);

        } catch (XMLConvException ge) {
            LOGGER.error("Unable to convert JSON to XML. " + ge.toString());
        } catch (Exception e) {
            LOGGER.error("Unable to convert JSON to XML. ");
        }
        redirectAttributes.addFlashAttribute("xml", xml);
        return "redirect:/converter/json2xml";
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


    /**
     * Process String
     * @param arg Argument
     * @return Result
     */
    private String processFormStr(String arg) {
        String result = null;
        if (arg != null) {
            if (!arg.trim().equalsIgnoreCase("")) {
                result = arg.trim();
            }
        }
        return result;
    }

    /**
     * Process Boolean
     * @param arg Argument
     * @return Result
     */
    private Boolean processFormBoolean(Boolean arg) {
        if (arg == null) {
            arg = false;
        }
        return arg;
    }
}
