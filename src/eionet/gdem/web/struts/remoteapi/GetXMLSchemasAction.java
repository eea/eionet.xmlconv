/*
 * Created on 28.02.2008
 */
package eionet.gdem.web.struts.remoteapi;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.remote.GetXMLSchemasResult;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS GetSchemasAction
 */

public class GetXMLSchemasAction extends BaseMethodAction {

    private static LoggerIF _logger = GDEMServices.getLogger();

    /**
     * Purpose of this action is to execute ConversionService method listConversions. The request could have schema parameter
     */
    @Override
    public ActionForward execute(ActionMapping map, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse httpServletResponse) throws ServletException {

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(httpServletResponse);
        // get request parameters
        Map params = request.getParameterMap();

        try {

            // Call ConversionService
            ConversionServiceIF cs = new ConversionService();
            List schemas = cs.getXMLSchemas();

            // parse the result of Conversion Service method and format it as XML
            GetXMLSchemasResult xmlResult = new GetXMLSchemasResult();
            xmlResult.setResult(schemas);
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
        return null;
    }
}
