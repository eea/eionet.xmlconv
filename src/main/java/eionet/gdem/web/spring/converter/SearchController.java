package eionet.gdem.web.spring.converter;

import eionet.gdem.conversion.ConversionService;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.CrFileDto;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.conversions.ConversionForm;
import eionet.gdem.web.spring.schemas.IRootElemDao;
import eionet.gdem.web.spring.schemas.SchemaManager;
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
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
@Controller
@RequestMapping("/converter")
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    private MessageService messageService;

    private IRootElemDao rootElemDao;

    @Autowired
    public SearchController(MessageService messageService, IRootElemDao rootElemDao) {
        this.messageService = messageService;
        this.rootElemDao = rootElemDao;
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
        model.addAttribute("form", cForm);
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

//                httpServletRequest.getSession().setAttribute("conversionUrl", "");
//                httpServletRequest.getSession().setAttribute("conversionId", "");
//                redirectAttributes.addFlashAttribute("converted.url", "");
//                httpServletRequest.getSession().setAttribute("converted.conversionId", "");
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
        redirectAttributes.addFlashAttribute("form", cForm);
        return "redirect:/converter/search";
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
