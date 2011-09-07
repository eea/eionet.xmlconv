/*
 * Created on 07.02.2008
 */
package eionet.gdem.web.struts.remoteapi;

import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dcm.remote.ListConversionsResult;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.BaseAction;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ListConversionAction
 */

public class ListConversionsAction extends BaseAction {
    private static LoggerIF _logger = GDEMServices.getLogger();

    public static final String SCHEMA_PARAM_NAME = "schema";

    /**
     * Purpose of this action is to execute ConversionService method listConversions. The request could have schema parameter
     */
    public ActionForward execute(ActionMapping map, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse httpServletResponse) throws ServletException {

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(httpServletResponse);
        // get request parameters
        Map params = request.getParameterMap();

        try {
            String schema = null;
            if (params.containsKey(SCHEMA_PARAM_NAME))
                schema = (String) ((Object[]) params.get(SCHEMA_PARAM_NAME))[0];
            if (Utils.isNullStr(schema))
                schema = null;

            // Call ConversionService
            ConversionServiceIF cs = new ConversionService();
            Vector v = cs.listConversions(schema);

            // parse the result of Conversion Service method and format it as XML
            ListConversionsResult xmlResult = new ListConversionsResult();
            xmlResult.setResult(v);
            xmlResult.writeXML();
            // flush the result into servlet outputstream
            methodResponse.flushXML(xmlResult);
        } catch (Exception e) {
            _logger.error(e.toString());
            try {
                // if error happened, then flush the error in XML format into servlet outputstream
                methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), map.getPath(), params);
            } catch (Exception ge) {
                _logger.error("Unable to flush XML error: " + ge.toString());
                throw new ServletException(ge);
            }
        }
        // Do nothing, then response is already sent.
        return map.findForward(null);
    }
}
