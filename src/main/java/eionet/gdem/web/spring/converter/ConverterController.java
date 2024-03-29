package eionet.gdem.web.spring.converter;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.schemas.IRootElemDao;
import eionet.gdem.utils.Utils;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
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
    public String list(@ModelAttribute("form") ConversionForm form) {
        return "/converter/convert";
    }

    @PostMapping(params = "convert")
    public String convert(@ModelAttribute("form") ConversionForm cForm, HttpSession httpSession, HttpServletResponse httpServletResponse,
                          BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        String ticket = (String) httpSession.getAttribute(Constants.TICKET_ATT);
        String url = cForm.getUrl();
        String convert_id = cForm.getConversionId();

        new ConverterValidator().validate(cForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/converter/convert";
        }

        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(httpServletResponse);
        // get request parameters
        try {
            ConversionServiceIF cs = new ConversionService();
            // set up the servlet outputstream form converter
            cs.setHttpResponse(methodResponse);
            cs.setTicket(ticket);
            cs.convert(url, convert_id);
        } catch (XMLConvException e) {
            throw new RuntimeException("Error testing conversion: " + e.getMessage());
        }
        return null;
    }

    @PostMapping(params = "find")
    public String find(@ModelAttribute("form") ConversionForm cForm, HttpServletRequest httpServletRequest, Model model,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        //String ticket = (String) httpServletRequest.getSession().getAttribute(Names.TICKET_ATT);
        SpringMessages errors = new SpringMessages();

        ArrayList<Schema> schemas = new ArrayList<Schema>();
        ArrayList stylesheets = null;

        String idConv = null;
        String schema = cForm.getSchemaUrl();
        String url = cForm.getUrl();

        new ConverterValidator().validateFind(cForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/converter/convert";
        }
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
                    InputAnalyser analyser = null;
                    try {
                        analyser = new InputAnalyser();
                        analyser.parseXML(url);
                    } catch (DCMException e) {
                        throw new RuntimeException(messageService.getMessage(e.getErrorCode()));
                    }
                    // schema or dtd found from header
                    if (analyser.getSchemas().isEmpty()) {
                        for (String schemaOrDTD : analyser.getSchemas()) {
                            stylesheets = sm.getSchemaStylesheets(schemaOrDTD);
                            Schema oSchema = new Schema();
                            oSchema.setSchema(schemaOrDTD);
                            oSchema.setStylesheets(stylesheets);
                            schemas.add(oSchema);
                            cForm.setSchemas(schemas);
                        }
                    } else {
                        // did not find schema or dtd from xml header
                        // compare root elements
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
            cForm.setAction("find");

            redirectAttributes.addFlashAttribute("schemasList", StylesheetListLoader.getConversionSchemasList(httpServletRequest));
        } catch (DCMException e) {
            throw new RuntimeException("Error listing conversions: " + messageService.getMessage(e.getErrorCode()));
        } catch (SQLException e) {
            throw new RuntimeException("Error listing conversions: " + e.getMessage());
        }
        model.addAttribute("form", cForm);
        return "/converter/convert";
    }

    /**
     * check if schema passed as request parameter exists in the list of schemas stored in the session. If there is no schema list
     * in the session, then create it
     *
     * @param httpServletRequest Request
     * @param schema             Schema
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
