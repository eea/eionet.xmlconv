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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dto.HostDto;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IHostDao;
import eionet.gdem.web.struts.BaseAction;

public class ListHostsAction extends BaseAction {

    private static LoggerIF _logger = GDEMServices.getLogger();
    protected final String GDEM_SSAclName = "/stylesheets";

    private IHostDao hostDao = GDEMServices.getDaoService().getHostDao();;

    public ActionForward execute(ActionMapping map, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse httpServletResponse) {
        ActionErrors errors = new ActionErrors();
        List result = new ArrayList();

        try {
            if (checkPermission(request, Names.ACL_HOST_PATH, "v")) {
                Vector list = hostDao.getHosts(null);
                for (int i = 0; i < list.size(); i++) {
                    Hashtable host = (Hashtable) list.get(i);
                    HostDto h = new HostDto();
                    h.setId((String) host.get("host_id"));
                    h.setHostname((String) host.get("host_name"));
                    h.setUsername((String) host.get("user_name"));
                    result.add(h);
                }
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.vnoperm",
                        translate(map, request, "label.hosts")));
            }
        } catch (Exception e) {
            _logger.error("", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
        }

        if (errors.size() > 0)
            request.getSession().setAttribute("dcm.errors", errors);
        request.setAttribute("hosts.list", result);

        return map.findForward("success");

    }

}
