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

package eionet.gdem.web.struts.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.DynaValidatorForm;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        DynaValidatorForm form = (DynaValidatorForm) actionForm;

        String cmdXGawk = (String) form.get("cmdXGawk");
        Long qaTimeout = (Long) form.get("qaTimeout");
        String user = (String) httpServletRequest.getSession().getAttribute("user");

        try {

            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_CONFIG_PATH, "u")) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.autorization.config.update"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward("success");
            }
            if (qaTimeout == null || qaTimeout.equals("") || qaTimeout <= 0) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.config.system.qatimeout.validation"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward("success");
            }

            DcmProperties dcmProp = new DcmProperties();

            dcmProp.setSystemParams(qaTimeout, cmdXGawk);

        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("SystemAction error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveErrors(httpServletRequest, errors);
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("success");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("SystemAction error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
            saveErrors(httpServletRequest, errors);
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("success");
        }
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.editParam.system.saved"));

        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);
        return actionMapping.findForward("success");
    }

}
