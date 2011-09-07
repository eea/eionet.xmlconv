/*
 * Created on 16.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ValidateXMLFormAction
 */

public class ValidateXMLFormAction extends Action {

    private static LoggerIF _logger = GDEMServices.getLogger();

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionErrors errors = new ActionErrors();
        ArrayList schemas = null;

        // load the list of schemas from the session
        Object schemasInSession = httpServletRequest.getSession().getAttribute("conversion.schemas");

        // reset the form in the session
        ConversionForm cForm = (ConversionForm) actionForm;

        try {
            // if schemas list is not stored in the session, then load it from the database
            if (schemasInSession == null || ((ArrayList) schemasInSession).size() == 0) {
                schemasInSession = loadSchemas();
                httpServletRequest.getSession().setAttribute("conversion.schemas", schemasInSession);
            }
        } catch (DCMException e) {
            e.printStackTrace();
            _logger.error("Serach CR Conversions error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveMessages(httpServletRequest, errors);
        }
        return actionMapping.findForward("success");
    }

    /**
     * Load the list of schemas from the databases
     * 
     * @return
     * @throws DCMException
     */
    private ArrayList loadSchemas() throws DCMException {

        ArrayList schemas = null;
        SchemaManager sm = new SchemaManager();
        schemas = sm.getSchemas();
        return schemas;
    }
}
