package eionet.gdem.web.struts.source;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.Constants;
import eionet.gdem.dcm.business.SourceFileManager;
import eionet.gdem.utils.Utils;

public class GetSourceAction extends Action {

	
	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		String ticket = httpServletRequest.getParameter(Constants.TICKET_PARAM);
		//String auth = httpServletRequest.getParameter(Constants.AUTH_PARAM);
		String source_url = httpServletRequest.getParameter(Constants.SOURCE_URL_PARAM);
			//"http://cdr.eionet.europa.eu/be/eu/wfdart8/be_scheldt_escaut/RBD_BE_Escaut_RW_Monitoring.xml";
		try{
			SourceFileManager manager = new SourceFileManager();
			if(!Utils.isNullStr(ticket)){
				manager.getFileBasicAuthentication(httpServletResponse, ticket, source_url);
			}
			else{
				manager.getFileNoAuthentication(httpServletResponse,source_url);				
			}
		}
		catch (Exception e) {
		}
		return null;
	}
}
