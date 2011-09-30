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
 *    Original code: Nedeljko Pavlovic (ED)
 */

package eionet.gdem.web.struts.stylesheet;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.DynaValidatorForm;

import eionet.gdem.Properties;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dcm.XslGenerator;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.utils.xml.XSLTransformer;

public class GetStylesheetAction extends Action {

    public static XSLTransformer transform = new XSLTransformer();

    /** */
    private static final Log LOGGER = LogFactory.getLog(GetStylesheetAction.class);


    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {

        ActionMessages errors = new ActionMessages();
        String metaXSLFolder = Properties.metaXSLFolder;
        String tableDefURL = Properties.ddURL;
        DynaValidatorForm loginForm = (DynaValidatorForm) actionForm;
        String id = (String) loginForm.get("id");
        String convId = (String) loginForm.get("conv");

        try {
            ConversionDto conv = Conversion.getConversionById(convId);
            String format = metaXSLFolder + File.separatorChar + conv.getStylesheet();
            String url = tableDefURL + "/GetTableDef?id=" + id;
            ByteArrayInputStream byteIn = XslGenerator.convertXML(url, format);
            int bufLen = 0;
            byte[] buf = new byte[1024];

            // byteIn.re

            response.setContentType("text/xml");
            while ((bufLen = byteIn.read(buf)) != -1) {
                response.getOutputStream().write(buf, 0, bufLen);
            }

            byteIn.close();
            return null;

        } catch (Exception ge) {
            LOGGER.error("Error getting stylesheet", ge);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.error.generation"));
            // request.getSession().setAttribute("dcm.errors", errors);
            request.setAttribute("dcm.errors", errors);
            return actionMapping.findForward("fail");
        }

        // return null;

    }

}
