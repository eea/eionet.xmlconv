package eionet.gdem.web.struts.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.dcm.conf.DbTest;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.dcm.conf.LdapTest;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.struts.stylesheet.StylesheetForm;

public class DbAction extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		
		ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();		
		
				
		DbForm form=(DbForm)actionForm;
		String dbUrl= form.getDbUrl();
		String dbUser=form.getUser();
		String dbPwd = form.getPassword();
		String user = (String)httpServletRequest.getSession().getAttribute("user");
		
		
				
		try{
			
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_CONFIG_PATH, "u")){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.autorization.config.update"));
				httpServletRequest.getSession().setAttribute("dcm.errors", errors);						
				return actionMapping.findForward("success");				
			}
			
			if(dbUrl == null || dbUrl.equals("")){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.config.ldap.url.validation"));
				httpServletRequest.getSession().setAttribute("dcm.errors", errors);						
				return actionMapping.findForward("success");
			}
			
			
			DbTest dbTest = new DbTest();
			dbTest.tstDbParams(dbUrl, dbUser, dbPwd);
			
			DcmProperties dcmProp = new DcmProperties();
			
			dcmProp.setDbParams(dbUrl, dbUser, dbPwd); 
			
		}catch(DCMException e){			
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			saveErrors(httpServletRequest,errors);
	        httpServletRequest.getSession().setAttribute("dcm.errors", errors);			
			return actionMapping.findForward("success");
		}catch(Exception e){
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
			saveErrors(httpServletRequest,errors);
	        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("success");
		}
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.editParam.db.saved"));
		
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);						
        return actionMapping.findForward("success");
	}		
	
	

}
