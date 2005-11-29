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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.DynaValidatorForm;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.BaseAction;

public class SaveHostAction extends BaseAction {
	private static LoggerIF _logger = GDEMServices.getLogger();
	
	public ActionForward execute(ActionMapping map, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse) {
		ActionMessages messages = new ActionMessages();
		ActionMessages errors = new ActionMessages();
		DynaValidatorForm hostForm = (DynaValidatorForm) actionForm;
		String hostId = processFormStr((String) hostForm.get("id"));
		String host= (String) hostForm.get("host");
		String username= (String) hostForm.get("username");
		String password= (String) hostForm.get("password");
		
		try {
			if(hostId==null) { //Add new host
				_logger.debug("ADDING NEW HOST !!!");
				if(	checkPermission(request, Names.ACL_HOST_PATH, "i")) {
					DbModuleIF dbM= GDEMServices.getDbModule();
					dbM.addHost(host, username, password);
					hostForm.getMap().clear();
					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.hosts.inserted"));
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.inoperm", translate(map, request, "label.hosts")));
				}
			} else { //Update host
				_logger.debug("UPDATE HOST !!!");
				if(	checkPermission(request, Names.ACL_HOST_PATH, "u")) {
					DbModuleIF dbM= GDEMServices.getDbModule();
					dbM.updateHost(hostId, host, username, password);
					hostForm.getMap().clear();
					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.hosts.updated"));
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.unoperm", translate(map, request, "label.hosts")));
				}
			}
		} catch (Exception e) {
			_logger.error("", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
		}
		
		if(errors.size()>0)	{
			request.getSession().setAttribute("dcm.errors", errors);
			return map.getInputForward();
		}
		if(messages.size()>0) request.getSession().setAttribute("dcm.messages", messages);
		
		return map.findForward("success");
		
	}
	
	private boolean checkConnection(String url, String username, String password) {
		boolean result=false;
		try {
			InputFile src = new InputFile(url);
			src.setAuthentication(Utils.getEncodedAuthentication(username, password));
			src.setTrustedMode(false);
			src.getSrcInputStream();
			result=true;
		} catch(Exception e) {
			_logger.error("", e);
		}
		return result;
	}

}
