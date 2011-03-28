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

package eionet.gdem.web.struts.stylesheet;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

public class ConvTypeAction extends Action {

    private static LoggerIF _logger = GDEMServices.getLogger();


    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        ConvTypeHolder ctHolder = new ConvTypeHolder();
        ActionMessages errors = new ActionMessages();

        String schema = (String) httpServletRequest.getParameter("schema");
        httpServletRequest.setAttribute("schema", schema);

        try {
            StylesheetManager sm = new StylesheetManager();
            ctHolder = sm.getConvTypes();
            SchemaManager schemaMan = new SchemaManager();
            ArrayList schemas = schemaMan.getDDSchemas();

            httpServletRequest.getSession().setAttribute("stylesheet.DDSchemas", schemas);

            if (!Utils.isNullStr(schema)) {
                String schemaId = schemaMan.getSchemaId(schema);
                if (schemaId != null) {
                    httpServletRequest.setAttribute("schemaInfo", schemaMan.getSchema(schemaId));
                    httpServletRequest.setAttribute("existingStylesheets", sm.getSchemaStylesheets(schemaId, null));
                }
            }

        } catch (DCMException e) {
            e.printStackTrace();
            _logger.error("Error getting conv types",e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveErrors(httpServletRequest, errors);
        }
        httpServletRequest.getSession().setAttribute("stylesheet.outputtype", ctHolder);
        return actionMapping.findForward("success");
    }

}
