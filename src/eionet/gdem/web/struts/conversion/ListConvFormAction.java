/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Istvan Alfeldi (ED)
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

public class ListConvFormAction extends Action {

    private static LoggerIF _logger = GDEMServices.getLogger();


    /*
     * (non-Javadoc)
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ActionErrors errors = new ActionErrors();
        ArrayList schemas = null;

        //load the list of schemas from the session
        Object schemasInSession = httpServletRequest.getSession().getAttribute(
        "conversion.schemas");

        //reset the form in the session
        ConversionForm cForm = (ConversionForm) actionForm;
        cForm.resetAll(actionMapping, httpServletRequest);


        try {
            //if schemas list is not stored in the session, then load it from the database
            if (schemasInSession == null
                    || ((ArrayList) schemasInSession).size() == 0) {
                schemasInSession = loadSchemas();
                httpServletRequest.getSession().setAttribute(
                        "conversion.schemas", schemasInSession);
            }
        } catch (DCMException e) {
            e.printStackTrace();
            _logger.error("Serach CR Conversions error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e
                    .getErrorCode()));
            saveMessages(httpServletRequest, errors);
        }
        return actionMapping.findForward("success");
    }
    /**
     * Load the list of schemas from the databases
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
