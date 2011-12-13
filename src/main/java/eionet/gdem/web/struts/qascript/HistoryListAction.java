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
 * Contributor(s):* Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.web.struts.qascript;

import java.util.List;

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

import eionet.gdem.Constants;
import eionet.gdem.dcm.business.BackupManager;
import eionet.gdem.dto.BackupDto;
import eionet.gdem.exceptions.DCMException;

public class HistoryListAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(HistoryListAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();
        List<BackupDto> l = null;

        String scriptId = httpServletRequest.getParameter(Constants.XQ_SCRIPT_ID_PARAM);

        try {
            BackupManager bm = new BackupManager();
            l = bm.getBackups(scriptId);

        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error getting history for QA scripts list", e);
            errors.add("history", new ActionMessage("label.exception.unknown"));
            saveErrors(httpServletRequest, errors);
        }
        httpServletRequest.setAttribute("qascript.history", l);
        httpServletRequest.setAttribute("script_id", scriptId);
        return actionMapping.findForward("success");
    }
}
