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

package eionet.gdem.web.struts.qascript;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Enriko Käsper, Tieto Estonia
 * AddQAScriptFormAction
 */

public class AddQAScriptFormAction  extends Action {

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {


        QAScriptForm form = (QAScriptForm) actionForm;
        String schemaId = (String) httpServletRequest.getParameter("schemaId");
        if (schemaId == null || schemaId.equals("")) {
            schemaId = (String) httpServletRequest.getAttribute("schemaId");
        }
        String schema = (String) httpServletRequest.getParameter("schema");
        if (schema == null || schema.equals("")) {
            schema = (String) httpServletRequest.getAttribute("schema");
        }
        form.setSchema(schema);
        form.setSchemaId(schemaId);

        return actionMapping.findForward("success");
    }
}
