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

package eionet.gdem.web.struts.qasandbox;

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
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.qascript.QAScriptListHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SearchCRSandboxAction Find all the scripts for the given XML schema and allow to execute them in sandox
 *
 * @author Enriko Käsper, Tieto Estonia
 */

public class FindQAScriptsAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(FindQAScriptsAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionErrors errors = new ActionErrors();

        QASandboxForm cForm = (QASandboxForm) actionForm;
        String schemaUrl = cForm.getSchemaUrl();
        Schema schema = cForm.getSchema();

        if (Utils.isNullStr(schemaUrl)) {

            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingSchemaUrl"));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }
        try {
            // cForm.setScriptId(null);

            SchemaManager sm = new SchemaManager();
            String schemaId = sm.getSchemaId(schemaUrl);
            QAScriptListHolder qaScripts = sm.getSchemasWithQAScripts(schemaId);

            if (qaScripts != null && !Utils.isNullList(qaScripts.getQascripts())) {
                Schema newSchema = qaScripts.getQascripts().get(0);
                if (schema == null || !schema.equals(newSchema)) {
                    cForm.setSchema(newSchema);
                } else {
                    schema.setDoValidation(newSchema.isDoValidation());
                    schema.setQascripts(newSchema.getQascripts());
                    cForm.setSchema(schema);
                }
            }
            cForm.setShowScripts(true);
            if (Utils.isNullStr(cForm.getScriptId())) {
                if (Utils.isNullList(cForm.getSchema().getQascripts()) && cForm.getSchema().isDoValidation()) {
                    cForm.setScriptId("-1");
                } else if (!Utils.isNullList(cForm.getSchema().getQascripts()) && cForm.getSchema().getQascripts().size() == 1
                        && !cForm.getSchema().isDoValidation()) {
                    cForm.setScriptId(cForm.getSchema().getQascripts().get(0).getScriptId());
                }
            }
        } catch (DCMException e) {
            // e.printStackTrace();
            LOGGER.error("Error searching XML files", e);
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        } catch (Exception e) {
            // e.printStackTrace();
            LOGGER.error("Error searching XML files", e);
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }

        return actionMapping.findForward("success");
    }

}
