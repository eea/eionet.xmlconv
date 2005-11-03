package eionet.gdem.web.struts.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.DynaValidatorForm;

import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class LdapFormAction extends Action{

	private static LoggerIF _logger=GDEMServices.getLogger();
	
	   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
			ActionErrors errors = new ActionErrors();
			DynaValidatorForm form=(DynaValidatorForm) actionForm;
			try{				
				form.set("url", Properties.ldapUrl);
				form.set("context", Properties.ldapContext);
				form.set("userDir", Properties.ldapUserDir);
				form.set("attrUid", Properties.ldapAttrUid);
			}catch(Exception e){			
				e.printStackTrace();
				_logger.error(e.getMessage());
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
				saveMessages(httpServletRequest, errors);				
			}
			saveMessages(httpServletRequest, errors);
			
	        return actionMapping.findForward("success");
	    }


}
