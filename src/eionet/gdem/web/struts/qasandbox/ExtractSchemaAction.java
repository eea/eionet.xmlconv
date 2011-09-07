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

import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.web.struts.qascript.QAScriptListHolder;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;

/**
 * SearchCRSandboxAction Extract the XML schema from the inserted source URL of XML file and find available QA scripts.
 * 
 * @author Enriko Käsper, Tieto Estonia
 */

public class ExtractSchemaAction extends Action {
    private static LoggerIF _logger = GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionErrors errors = new ActionErrors();

        QASandboxForm cForm = (QASandboxForm) actionForm;
        Schema oSchema = cForm.getSchema();
        String sourceUrl = cForm.getSourceUrl();

        if (Utils.isNullStr(sourceUrl)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingUrl"));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }
        if (!Utils.isURL(sourceUrl)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.notUrl"));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }

        String schemaUrl = null;
        try {
            if (!Utils.isNullStr(sourceUrl)) {
                schemaUrl = findSchemaFromXml(sourceUrl);
                if (!Utils.isURL(schemaUrl)) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.schemaNotFound"));
                    saveErrors(httpServletRequest, errors);
                    return actionMapping.findForward("error");
                }
                if (schemaExists(httpServletRequest, schemaUrl)) {
                    cForm.setSchemaUrl(schemaUrl);
                } else if (!Utils.isNullStr(schemaUrl)) {
                    if (oSchema == null)
                        oSchema = new Schema();

                    oSchema.setSchema(null);
                    oSchema.setDoValidation(false);
                    oSchema.setQascripts(null);
                    cForm.setSchemaUrl(null);
                    cForm.setShowScripts(true);
                    cForm.setSchema(oSchema);

                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.noSchemaScripts", schemaUrl));
                    saveErrors(httpServletRequest, errors);
                    return actionMapping.findForward("error");
                }
            }
        } catch (DCMException e) {
            // e.printStackTrace();
            _logger.error("Error extracting schema from XML file", e);
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        } catch (Exception e) {
            // e.printStackTrace();
            _logger.error("Error extracting schema from XML file", e);
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }

        return actionMapping.findForward("find");
    }

    /**
     * check if schema passed as request parameter exists in the list of schemas stored in the session. If there is no schema list
     * in the session, then create it
     * 
     * @param httpServletRequest
     * @param schema
     * @return
     * @throws DCMException
     */
    private boolean schemaExists(HttpServletRequest httpServletRequest, String schema) throws DCMException {
        Object schemasInSession = httpServletRequest.getSession().getAttribute("qascript.qascriptList");
        if (schemasInSession == null || ((QAScriptListHolder) schemasInSession).getQascripts().size() == 0) {
            schemasInSession = QAScriptListLoader.loadQAScriptList(httpServletRequest, true);
        }
        Schema oSchema = new Schema();
        oSchema.setSchema(schema);
        return ((QAScriptListHolder) schemasInSession).getQascripts().contains(oSchema);
    }

    private String findSchemaFromXml(String xml) {
        InputAnalyser analyser = new InputAnalyser();
        try {
            analyser.parseXML(xml);
            String schemaOrDTD = analyser.getSchemaOrDTD();
            return schemaOrDTD;
        } catch (Exception e) {
            // do nothoing - did not find XML Schema
            // handleError(request, response, e);
        }
        return null;
    }

}
