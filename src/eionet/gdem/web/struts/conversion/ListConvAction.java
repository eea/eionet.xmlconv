package eionet.gdem.web.struts.conversion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ListConvAction  extends Action{


	   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
			ListConvForm form=(ListConvForm)actionForm;
			String validate=form.getValidate();
			String schema=form.getXmlSchema(); 
			String xml = form.getXmlUrl();
						
			httpServletRequest.setAttribute("schema", schema);
			httpServletRequest.setAttribute("url", xml);
			if (validate != null) {
				httpServletRequest.setAttribute("validate", validate);
			}			
	        return actionMapping.findForward("success");
	    }


}
