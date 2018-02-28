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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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

    @ModelAttribute
    public void init(Model model, HttpServletRequest httpServletRequest) {
        try {
            model.addAttribute("schemas", StylesheetListLoader.getConversionSchemasList(httpServletRequest));
        } catch (DCMException e) {
            throw new RuntimeException(messageService.getMessage(e.getErrorCode()));
        }
    }

    @GetMapping("/search")
    public String searchXML(@ModelAttribute("form") SearchForm cForm) {
        return "/converter/search";
    }

    @PostMapping("/search")
    public String searchXMLSubmit(@ModelAttribute("form") @Valid SearchForm cForm, BindingResult bindingResult, HttpServletRequest httpServletRequest,
                                  Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "/converter/search";
        }
        /*String ticket = (String) httpServletRequest.getSession().getAttribute(Constants.TICKET_ATT);*/

        String idConv = null;
        Schema schema = new Schema();

        // request comes from SchemaStyleheets pagew
        /*if (httpServletRequest.getParameter("conversionId") != null) {
            idConv = httpServletRequest.getParameter("conversionId");
            httpServletRequest.getSession().setAttribute("converted.conversionId", idConv);
        }*/

        String schemaUrl = cForm.getSchemaUrl();

        try {
            SchemaManager sm = new SchemaManager();
            ConversionService cs = new ConversionService();
            // use the Schema data from the session, if schema is the same
            // otherwise load the data from database and search CR
            if (!Utils.isNullStr(schemaUrl) && (schema == null || !schema.getSchema().equals(schema))) {
                if (!schemaExists(httpServletRequest, schemaUrl)) {
                    throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
                }
                ArrayList stylesheets = null;
                List<CrFileDto> crfiles = null;
                stylesheets = sm.getSchemaStylesheets(schemaUrl);
                crfiles = sm.getCRFiles(schemaUrl);

                schema.setSchema(schemaUrl);
                schema.setStylesheets(stylesheets);
                schema.setCrfiles(crfiles);

                if (idConv == null && schema.getStylesheets().size() > 0) {
                    idConv = ((Stylesheet) (schema.getStylesheets().get(0))).getConvId();
                }
                if (idConv == null) {
                    idConv = "-1";
                }
/*                cForm.setSchema(oSchema);
                cForm.setConversionId(idConv);*/
            }
        } catch (DCMException e) {
            throw new RuntimeException("Error searching XML files");
        }
        return "/converter/search";
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
