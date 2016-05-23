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

package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import eionet.gdem.Properties;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;
import eionet.gdem.web.struts.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add upload schema action class.
 * @author Unknown
 * @author George Sofianos
 */
public class AddUplSchemaAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddUplSchemaAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        UplSchemaForm form = (UplSchemaForm) actionForm;

        FormFile schemaFile = form.getSchemaFile();
        String desc = form.getDescription();
        String schemaUrl = form.getSchemaUrl();
        boolean doValidation = form.isDoValidation();
        String schemaLang = form.getSchemaLang();
        boolean blocker = form.isBlockerValidation();

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        if (isCancelled(httpServletRequest)) {

            return actionMapping.findForward("success");
        }

        if ((schemaFile == null || schemaFile.getFileSize() == 0) && Utils.isNullStr(schemaUrl)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplSchema.validation"));
            saveErrors(httpServletRequest, errors);
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("fail");
        }
        
        if (!(new SchemaUrlValidator().isValidUrlSet(schemaUrl))) {
            return this.onValidationFailure(actionMapping, httpServletRequest, errors, "label.uplSchema.validation.urlFormat");
        }
        
        try {
            SchemaManager sm = new SchemaManager();
            String fileName = "";
            String tmpSchemaUrl = "";
            // generate unique file name
            if (schemaFile != null) {
                fileName = sm.generateUniqueSchemaFilename(user, Utils.extractExtension(schemaFile.getFileName(), "xsd"));
                if (Utils.isNullStr(schemaUrl)) {
                    tmpSchemaUrl = Properties.gdemURL + "/schema/" + fileName;
                    schemaUrl = tmpSchemaUrl;
                }
            }
            // Add row to T_SCHEMA table
            String schemaID = sm.addSchema(user, schemaUrl, desc, schemaLang, doValidation, blocker);
            if (schemaFile != null && schemaFile.getFileSize() > 0) {
                // Change the filename to schema-UniqueIDxsd
                fileName =
                    sm.generateSchemaFilenameByID(Properties.schemaFolder, schemaID, Utils.extractExtension(schemaFile.getFileName()));
                // Add row to T_UPL_SCHEMA table
                sm.addUplSchema(user, schemaFile, fileName, schemaID);
                // Update T_SCHEMA table set
                if (!Utils.isNullStr(tmpSchemaUrl)) {
                    schemaUrl = Properties.gdemURL + "/schema/" + fileName;
                }
                sm.update(user, schemaID, schemaUrl, desc, schemaLang, doValidation, null, null, blocker);
            }
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplSchema.inserted"));
            QAScriptListLoader.reloadList(httpServletRequest);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            LOGGER.error("Error adding upload schema", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);
        saveErrors(httpServletRequest, errors);
        saveMessages(httpServletRequest, messages);

        return actionMapping.findForward("success");
    }

    /**
     * Validation failure
     * @param actionMapping Action mapping
     * @param httpServletRequest Request
     * @param errors Errors
     * @param messageKey Message key
     * @return Action forward
     */
    private ActionForward onValidationFailure(ActionMapping actionMapping, HttpServletRequest httpServletRequest, ActionMessages errors, String messageKey) {
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(messageKey));
        saveErrors(httpServletRequest, errors);
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        
        return actionMapping.findForward("fail");
    }
}
