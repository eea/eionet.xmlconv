/*
 * Created on 09.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import eionet.gdem.Constants;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.conversion.ConversionService;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.CrFileDto;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS SearchCRConversionAction
 */

public class SearchCRConversionAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchCRConversionAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        String ticket = (String) httpServletRequest.getSession().getAttribute(Constants.TICKET_ATT);

        ActionErrors errors = new ActionErrors();
        String idConv = null;
        Schema oSchema = null;

        // request comes from SchemaStyleheets pagew
        if (httpServletRequest.getParameter("conversionId") != null) {
            idConv = httpServletRequest.getParameter("conversionId");
            httpServletRequest.getSession().setAttribute("converted.conversionId", idConv);
        }

        ConversionForm cForm = (ConversionForm) actionForm;
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
            // e.printStackTrace();
            LOGGER.error("Error searching XML files", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveErrors(httpServletRequest, errors);
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("error");
        } catch (Exception e) {
            // e.printStackTrace();
            LOGGER.error("Error searching XML files", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(BusinessConstants.EXCEPTION_GENERAL));
            // saveMessages(httpServletRequest, errors);
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("error");
        }

        return actionMapping.findForward("success");
    }

    /**
     * check if schema passed as request parameter exists in the list of schemas stored in the session. If there is no schema list
     * in the session, then create it
     *
     * @param httpServletRequest
     * @param schema
     * @return
     * @throws DCMException
     */
    private boolean schemaExists(HttpServletRequest httpServletRequest, String schema) throws DCMException {
        List<Schema> schemasInCache = StylesheetListLoader.getConversionSchemasList(httpServletRequest);

        Schema oSchema = new Schema();
        oSchema.setSchema(schema);
        return schemasInCache.contains(oSchema);
    }
}
