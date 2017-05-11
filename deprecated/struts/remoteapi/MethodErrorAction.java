/*
 * Created on 18.02.2008
 */
package eionet.gdem.web.struts.remoteapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dcm.remote.XMLErrorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS MethodErrorAction
 */

public class MethodErrorAction extends BaseMethodAction {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodErrorAction.class);

    /**
     * Purpose of this action is show error messages of given request. XMLErrorResult should be stored in session already.
     */
    @Override
    public ActionForward execute(ActionMapping map, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse httpServletResponse) {

        // Create ccustom HTTP response wrapper
        HttpMethodResponseWrapper httpResult = new HttpMethodResponseWrapper(httpServletResponse);
        // Create error XML formatter
        XMLErrorResult errorResult = getServiceError(request);
        if (errorResult == null) {
            // no errors int the session
            errorResult = new XMLErrorResult();
            errorResult.setError("Unknown error happened.");
            errorResult.setBadRequestStatus();
        }
        try {
            // flush the error into servlet outputstream
            httpResult.flushXML(errorResult);
        } catch (Exception e) {
            LOGGER.error("Unable to flush XML error: " + e.toString());
        }
        // Do nothing
        return null;
    }
}
