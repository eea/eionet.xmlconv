package eionet.gdem.web.struts.remoteapi;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.GDEMException;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.qa.XQueryService;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, TripleDev
 */

public class RunQAScriptAction extends BaseMethodAction {

    /** */
    private static final Log LOGGER = LogFactory.getLog(RunQAScriptAction.class);

    /** Script ID parameter name */
    protected static final String SCRIPT_ID_PARAM_NAME = "script_id";
    /** URL parameter name */
    protected static final String URL_PARAM_NAME = "url";

    /**
     * Purpose of this action is to execute <code>XQueryService</code> runQAScript method. The method expects 2 request parameters:
     * convert_id and url;
     */
    @Override
    public ActionForward execute(ActionMapping map, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse httpServletResponse) throws ServletException {

        String scriptId = null;
        String url = null;

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(httpServletResponse);
        // get request parameters
        Map params = request.getParameterMap();
        try {
            // parse request parameters
            if (params.containsKey(SCRIPT_ID_PARAM_NAME)) {
                scriptId = (String) ((Object[]) params.get(SCRIPT_ID_PARAM_NAME))[0];
            }
            if (Utils.isNullStr(scriptId)) {
                throw new GDEMException(SCRIPT_ID_PARAM_NAME + " parameter is missing from request.");
            }
            if (params.containsKey(URL_PARAM_NAME)) {
                url = (String) ((Object[]) params.get(URL_PARAM_NAME))[0];
            }
            if (Utils.isNullStr(url)) {
                throw new GDEMException(URL_PARAM_NAME + " parameter is missing from request.");
            }

            // call XQueryService
            XQueryService xqs = new XQueryService();
            // set up the servlet outputstream form converter
            xqs.setHttpResponse(methodResponse);
            xqs.setTicket(getTicket(request));
            // execute conversion
            xqs.runQAScript(url, scriptId);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            try {
                // error happened
                methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), map.getPath(), params);
            } catch (Exception ge) {
                LOGGER.error("Unable to flush XML error: " + ge.toString());
                throw new ServletException(ge);
            }
        } finally {
            if (methodResponse != null) {
                try {
                    // flush the content
                    methodResponse.flush();
                } catch (Exception e) {
                    LOGGER.error("Unable to close Servlet Output Stream.", e);
                    e.printStackTrace();
                }
            }
        }
        // Do nothing, then response is already sent.
        return null;
    }
}
