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

package eionet.gdem.web.struts.hosts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import eionet.gdem.Constants;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.DynaValidatorForm;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IHostDao;
import eionet.gdem.web.struts.BaseAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteHostAction extends BaseAction {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteHostAction.class);

    private static IHostDao hostDao = GDEMServices.getDaoService().getHostDao();

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse httpServletResponse) {
        ActionErrors errors = new ActionErrors();
        ActionMessages messages = new ActionMessages();
        DynaValidatorForm hostForm = (DynaValidatorForm) actionForm;
        String hostId = processFormStr((String) hostForm.get("id"));

        try {
            if (checkPermission(request, Constants.ACL_HOST_PATH, "d")) {
                hostDao.removeHost(hostId);
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.hosts.deleted"));
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE,
                        new ActionMessage("error.dnoperm", translate(actionMapping, request, "label.hosts")));
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
        }

        if (errors.size() > 0) {
            request.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.getInputForward();
        }
        if (messages.size() > 0) {
            request.getSession().setAttribute("dcm.messages", messages);
        }

        return actionMapping.findForward("success");

    }

}
