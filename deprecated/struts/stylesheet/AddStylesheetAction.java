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

import com.mysql.jdbc.StringUtils;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;


import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Add stylesheet action.
 * @author Unknown
 * @author George Sofianos
 */
public class AddStylesheetAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddStylesheetAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        StylesheetForm form = (StylesheetForm) actionForm;
        Stylesheet stylesheet = AddEditStylehseetUtils.convertFormToStylesheetDto(form, httpServletRequest);

        FormFile xslFile = form.getXslfile();
        String user = (String) httpServletRequest.getSession().getAttribute("user");
        String schema = (form.getNewSchemas() == null || form.getNewSchemas().size() == 0) ? null : form.getNewSchemas().get(0);
        httpServletRequest.setAttribute("schema", schema);

        if (isCancelled(httpServletRequest)) {
            if (schema != null) {
                return new ActionForward("/do/schemaStylesheets?schema=" + schema, true);
            } else {
                return actionMapping.findForward("list");
            }
        }

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        if (xslFile == null || xslFile.getFileSize() == 0) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.validation"));
            saveErrors(httpServletRequest, errors);
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("fail");
        }
        String description = form.getDescription();
        if (description == null || description.isEmpty()) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.error.descriptionMissing"));
            saveErrors(httpServletRequest, errors);
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("fail");
        }
        stylesheet.setXslFileName(xslFile.getFileName());
        try {
            stylesheet.setXslContent(new String(xslFile.getFileData(), "UTF-8"));
        } catch (Exception e) {
            LOGGER.error("Error in edit stylesheet action when trying to load XSL file content from FormFile object", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(BusinessConstants.EXCEPTION_GENERAL));
        } finally {
            xslFile.destroy();
        }
        AddEditStylehseetUtils.validateXslFile(stylesheet, errors);

        if (errors.isEmpty()) {
            try {
                StylesheetManager stylesheetManager = new StylesheetManager();
                // stylesheetManager.add(user, schema, xslFile, type, desc, dependsOn);
                stylesheetManager.add(stylesheet, user);
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.inserted"));
                StylesheetListLoader.reloadStylesheetList(httpServletRequest);
                StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
            } catch (DCMException e) {
                e.printStackTrace();
                LOGGER.error("Add stylesheet error", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            }
        }
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);
        if (!StringUtils.isNullOrEmpty(schema)) {
            return new ActionForward("/do/schemaStylesheets?schema=" + schema, true);
        } else {
            return actionMapping.findForward("fail");
        }
    }
}
