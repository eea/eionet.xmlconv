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
 *    Original code: Nenad Popovic (ED)
 */

package eionet.gdem.web.struts.uimanage;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.eurodyn.web.util.uimanage.FSUtil;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.utils.SecurityUtil;
/**
* <p>Implementation of Struts <strong>Action</strong> </p>
* 
* <p>Uploads or deletes image file</p>
*/
public class ImageManagerAction extends Action {

	//private static final WDSLogger logger = WDSLogger.getLogger(ImageManagerAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ServletContext context = servlet.getServletContext();
		ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();		

		String user = (String)request.getSession().getAttribute("user");		
		
		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_CONFIG_PATH, "u")){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.autorization.config.update"));
				request.getSession().setAttribute("dcm.errors", errors);						
				return mapping.findForward("home");				
			}
		} catch (Exception e) {			
			e.printStackTrace();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
			request.getSession().setAttribute("dcm.errors", errors);						
			return mapping.findForward("home");							
		}
		
		FSUtil fileOp = new FSUtil();
		String path = context.getRealPath("/images/gallery/");
		ImageManagerForm myForm = (ImageManagerForm) form;
		String deletePic=myForm.getDeletePic();
		if((myForm.getPicFile()!=null)&&(!myForm.getPicFile().getFileName().equalsIgnoreCase(""))){			
			FormFile upload = myForm.getPicFile();
			fileOp.uploadFile(path, upload.getFileName(), upload.getInputStream());
			//logger.debug("Uploaded image:"+upload.getFileName());
			upload.destroy();
		}else if(!deletePic.equals("")){
			fileOp.deleteFile(path,deletePic);
			//logger.debug("Deleted image:"+deletePic);
		}
		request.setAttribute("fileList", fileOp.listFiles(context.getRealPath("/images/gallery/")));

		return mapping.findForward("uploaded");
	}
}