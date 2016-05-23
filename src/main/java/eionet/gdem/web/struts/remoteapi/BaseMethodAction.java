/*
 * Created on 19.02.2008
 */
package eionet.gdem.web.struts.remoteapi;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.remote.XMLErrorResult;
import eionet.gdem.web.struts.BaseAction;

/**
 * Abstract action for remote service HTTP methods.
 * @author Enriko Käsper, TietoEnator Estonia AS BaseMethodAction
 * @author George Sofianos
 */

public class BaseMethodAction extends BaseAction {

    /**
     * Store the error result in Session.
     *
     * @param request Request
     * @param errorMsg Error
     * @param methodName Method name
     * @param params Parameters
     * @param outputOpened Output
     */
    protected void
            setServiceError(HttpServletRequest request, String errorMsg, String methodName, Map params, boolean outputOpened) {
        XMLErrorResult errorResult = new XMLErrorResult();
        errorResult.setError(errorMsg);
        errorResult.setMethod(methodName);
        request.getSession().setAttribute("api.errors", errorResult);
    }

    /**
     * Get error result from session attribute.
     *
     * @param request Request
     * @return XML Error result
     */
    protected XMLErrorResult getServiceError(HttpServletRequest request) {
        XMLErrorResult errorResult = (XMLErrorResult) request.getSession().getAttribute("api.errors");
        request.getSession().setAttribute("api.errors", null);
        return errorResult;

    }

    /**
     * Returns ticket
     * @param req Request
     * @return Ticket
     */
    protected String getTicket(HttpServletRequest req) {
        String ticket = null;
        HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            ticket = (String) httpSession.getAttribute(Names.TICKET_ATT);
        }

        return ticket;
    }
}
