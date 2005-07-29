package eionet.gdem.web.struts.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ssr.InputAnalyser;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.schema.SchemaElemForm;

public class ListConvAction  extends Action{

	private static LoggerIF _logger=GDEMServices.getLogger();
	
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
