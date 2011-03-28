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
 * @author Enriko Käsper, TietoEnator Estonia AS
 * BaseMethodAction
 *
 * Abstract action for remote service HTTP methods
 */

public class BaseMethodAction extends BaseAction {

    /**
     * Store the error result in Session
     * @param request
     * @param errorMsg
     * @param methodName
     * @param params
     * @param outputOpened
     */
    protected void setServiceError(HttpServletRequest request, String errorMsg, String methodName, Map params, boolean outputOpened){
        XMLErrorResult errorResult = new XMLErrorResult();
        errorResult.setError(errorMsg);
        errorResult.setMethod(methodName);
        request.getSession().setAttribute("api.errors", errorResult);
    }
    /**
     * Get error result from session attribute
     * @param request
     * @return
     */
    protected XMLErrorResult getServiceError(HttpServletRequest request){
        XMLErrorResult errorResult = (XMLErrorResult)request.getSession().getAttribute("api.errors");
        request.getSession().setAttribute("api.errors", null);
        return errorResult;

    }
    protected String getTicket(HttpServletRequest req){
          String ticket=null;
        HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            ticket = (String)httpSession.getAttribute(Names.TICKET_ATT);
        }

        return ticket;
    }
}
