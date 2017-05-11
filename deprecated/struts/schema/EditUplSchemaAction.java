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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Unknown
 * @author George Sofianos
 */
public class EditUplSchemaAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditUplSchemaAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        SchemaElemForm form = (SchemaElemForm) actionForm;
        String uplSchemaId = form.getUplSchemaId();
        String schemaId = form.getSchemaId();
        FormFile file = form.getSchemaFile();
        String fileName = form.getUplSchemaFileName();

        if (isCancelled(httpServletRequest)) {
            return actionMapping.findForward("success");
        }

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            // sm.uplUpdate( user, schemaId, description);
            if (file != null && file.getFileSize() > 0) {
                if (Utils.isNullStr(fileName)) {
                    // Change the filename to schema-UniqueIDxsd
                    fileName =
                        sm.generateSchemaFilenameByID(Properties.schemaFolder, schemaId,
                                Utils.extractExtension(file.getFileName(), "xsd"));
                    sm.addUplSchema(user, file, fileName, schemaId);
                } else if (uplSchemaId != null) {
                    sm.updateUplSchema(user, uplSchemaId, schemaId, fileName, file);
                }
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplSchema.updated"));
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplSchema.upload.validation"));
            }

        } catch (DCMException e) {
            // e.printStackTrace();
            LOGGER.error("Error editing uploaded schema", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }
        httpServletRequest.setAttribute("schemaId", schemaId);

        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);

        saveErrors(httpServletRequest, errors);
        saveMessages(httpServletRequest, messages);

        return actionMapping.findForward("success");
    }
}
