/*
 * Created on 08.04.2008
 */
package eionet.gdem.web.struts.conversion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.exceptions.DCMException;
import eionet.gdem.web.struts.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS CRConversionFormAction
 */

public class CRConversionFormAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(CRConversionFormAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionErrors errors = new ActionErrors();

        // reset the form in the session
        ConversionForm cForm = (ConversionForm) actionForm;
        cForm.resetAll(actionMapping, httpServletRequest);

        try {
            httpServletRequest.setAttribute(StylesheetListLoader.CONVERSION_SCHEMAS_ATTR, StylesheetListLoader.getConversionSchemasList(httpServletRequest));
        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Serach CR Conversions error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveMessages(httpServletRequest, errors);
        }

        saveErrors(httpServletRequest, errors);
        return actionMapping.findForward("success");
    }
}
