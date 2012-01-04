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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.LookupDispatchAction;

import eionet.gdem.dto.Schema;

/**
 * SearchCRSandboxAction. The axction dispatches the original sandbox request to the correct action.
 *
 * @author Enriko Käsper, Tieto Estonia
 */

public class SandboxDispatchAction extends LookupDispatchAction {

    public ActionForward searchXml(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        return actionMapping.findForward("search");
    }

    public ActionForward extractSchema(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        return actionMapping.findForward("extract");
    }

    public ActionForward findScripts(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        return actionMapping.findForward("find");
    }

    public ActionForward manualUrl(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        QASandboxForm cForm = (QASandboxForm) actionForm;
        Schema schema = cForm.getSchema();
        if (schema != null) {
            schema.setCrfiles(null);
        }

        return actionMapping.findForward("success");
    }

    public ActionForward addToWorkqueue(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        return actionMapping.findForward("workqueue");

    }

    public ActionForward saveFile(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        return actionMapping.findForward("save");
    }

    public ActionForward runScript(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        return actionMapping.findForward("run");
    }

    protected Map<String, String> getKeyMethodMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("label.qasandbox.searchXML", "searchXml");
        map.put("label.qasandbox.findScripts", "findScripts");
        map.put("label.qasandbox.extractSchema", "extractSchema");
        map.put("label.qasandbox.manualUrl", "manualUrl");
        map.put("label.qasandbox.saveFile", "saveFile");
        map.put("label.qasandbox.addToWorkqueue", "addToWorkqueue");
        map.put("label.qasandbox.runNow", "runScript");
        return map;
    }
}
