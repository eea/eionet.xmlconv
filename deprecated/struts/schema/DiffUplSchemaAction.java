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
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.web.struts.schema;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.DocumentAnalyser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, Tieto Estonia DiffUplSchemaAction
 */

public class DiffUplSchemaAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiffUplSchemaAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        SchemaElemForm form = (SchemaElemForm) actionForm;

        String schemaId = form.getSchemaId();
        String uplSchemaId = form.getUplSchemaId();
        String schemaFile = form.getUplSchemaFileName();
        String schemaUrl = form.getSchema();
        String forward = "success";

        SyncUplSchemaForm syncForm = new SyncUplSchemaForm();

        String user_name = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            byte[] remoteSchema = sm.downloadRemoteSchema(schemaUrl);

            // check validity - it is really schema
            boolean isSchemaOrDTD = DocumentAnalyser.sourceIsXMLSchema(remoteSchema) || DocumentAnalyser.sourceIsDTD(remoteSchema);
            if (!isSchemaOrDTD) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(BusinessConstants.WARNING_SCHEMA_NOTVALID));
            }

            String result = sm.diffRemoteSchema(remoteSchema, schemaFile);

            if (!Utils.isNullStr(result)) {
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(result));
            }

            if (!BusinessConstants.WARNING_FILES_IDENTICAL.equals(result) || result.equals("")) {
                forward = "warning";
                syncForm.setSchemaId(schemaId);
                syncForm.setSchemaUrl(schemaUrl);
                syncForm.setUplSchemaFileName(schemaFile);
                syncForm.setUplSchemaId(uplSchemaId);
                try {
                    syncForm.setSchemaFile(new String(remoteSchema, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    syncForm.setSchemaFile(new String(remoteSchema));
                    e.printStackTrace();
                }
                httpServletRequest.setAttribute("SyncUplSchemaForm", syncForm);
            }

        } catch (DCMException e) {
            // e.printStackTrace();
            LOGGER.error("Unable to diff schemas", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            forward = "fail";
        }
        httpServletRequest.setAttribute("schemaId", schemaId);

        saveMessages(httpServletRequest.getSession(), messages);
        saveErrors(httpServletRequest.getSession(), errors);

        // saveMessages(httpServletRequest,messages);
        // saveErrors(httpServletRequest,errors);

        return actionMapping.findForward(forward);
    }
}
