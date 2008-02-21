/*
 * Created on 07.02.2008
 */
package eionet.gdem.web.struts.remoteapi;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.results.HttpMethodResponseWrapper;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.api.BaseMethodAction;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ListConversionAction
 */

public class ConvertAction  extends BaseMethodAction {

	private static final String CONVERT_ID_PARAM_NAME = "convert_id";
	private static final String URL_PARAM_NAME = "url";

	private static LoggerIF _logger = GDEMServices.getLogger();

	/**
	 * Purpose of this action is to execute <code>ConversionService</code> convert method.
	 * The method expects 2 request parameters: convert_id and url;
	 */
	public ActionForward execute(ActionMapping map, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse) throws ServletException{
		

		String convert_id = null;
		String url = null;
		
		//create custom HttpServletResponseWrapper		
		HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(httpServletResponse);
		//get request parameters
		Map params = request.getParameterMap();
		try{
			//parse request parameters
			if(params.containsKey(CONVERT_ID_PARAM_NAME))
				convert_id = (String)((Object[]) params.get(CONVERT_ID_PARAM_NAME))[0];
			if(Utils.isNullStr(convert_id))
				throw new GDEMException(CONVERT_ID_PARAM_NAME + " parameter is missing from request.");
			if(params.containsKey(URL_PARAM_NAME))
				url = (String)((Object[]) params.get(URL_PARAM_NAME))[0];
			if(Utils.isNullStr(url))
				throw new GDEMException(URL_PARAM_NAME + " parameter is missing from request.");
			
			// call ConversionService
			ConversionServiceIF cs = new ConversionService();
			//set up the servlet outputstream form converter
			cs.setHttpResponse(methodResponse);
			// execute conversion
			cs.convert(url, convert_id);
			//flush the content
			methodResponse.flush();
		}
		catch(Exception e){
			_logger.error(e.toString());
			try{
				//error happened
				methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage(),map.getPath(),params);
			}
			catch(Exception ge){
				_logger.error("Unable to flush XML error: " + ge.toString());
				throw new ServletException (ge);
			}
		}
		//Do nothing, then response is already sent.		
		return map.findForward(null);
	}
}
