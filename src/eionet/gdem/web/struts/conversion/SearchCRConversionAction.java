/*
 * Created on 09.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS SearchCRConversionAction
 */

public class SearchCRConversionAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();

	public ActionForward execute(ActionMapping actionMapping,
			ActionForm actionForm, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		String ticket = (String) httpServletRequest.getSession().getAttribute(
				Names.TICKET_ATT);

		ActionErrors errors = new ActionErrors();
		String idConv = null;
		Schema oSchema = null;
		
		//request comes from SchemaStyleheets pagew
		if(httpServletRequest.getParameter("conversionId")!=null){
			idConv=	(String)httpServletRequest.getParameter("conversionId");
			httpServletRequest.getSession().setAttribute("converted.conversionId", idConv);
		}


		ConversionForm cForm = (ConversionForm) actionForm;
		String schema = cForm.getSchemaUrl();
		oSchema = cForm.getSchema();

		try {
			SchemaManager sm = new SchemaManager();
			ConversionService cs = new ConversionService();
			// use the Schema data from the session, if schema is the same
			//otherwise load the data from database and search CR
			if (!Utils.isNullStr(schema)
					&& (oSchema == null || !oSchema.getSchema().equals(schema))) {
				if (!schemaExists(httpServletRequest,schema)) {
					throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
				}
				ArrayList stylesheets = null;
				ArrayList crfiles = null;
				stylesheets = sm.getSchemaStylesheets(schema);
				crfiles = sm.getCRFiles(schema);
				oSchema = new Schema();
				oSchema.setSchema(schema);
				oSchema.setStylesheets(stylesheets);
				oSchema.setCrfiles(crfiles);

				if (idConv == null && oSchema.getStylesheets().size() > 0) {
					idConv = ((Stylesheet) (oSchema.getStylesheets().get(0)))
							.getConvId();
				}
				if (idConv == null) {
					idConv = "-1";
				}
				cForm.setSchema(oSchema);
				cForm.setConversionId(idConv);

				httpServletRequest.getSession().setAttribute("converted.url","");
				httpServletRequest.getSession().setAttribute("converted.conversionId","");
			}
		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error("Error searching XML files", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e
					.getErrorCode()));
			// saveMessages(httpServletRequest, errors);
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("error");
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error searching XML files", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					BusinessConstants.EXCEPTION_GENERAL));
			// saveMessages(httpServletRequest, errors);
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("error");
		}

		return actionMapping.findForward("success");
	}
	/**
	 * check if schema passed as request parameter exists in the list of schemas stored in the session.
	 * If there is no schema list in the session, then create it
	 * @param httpServletRequest
	 * @param schema
	 * @return
	 * @throws DCMException
	 */
	private boolean schemaExists(HttpServletRequest httpServletRequest,
			String schema) throws DCMException {
		Object schemasInSession = httpServletRequest.getSession().getAttribute(
				"conversion.schemas");
		if (schemasInSession == null
				|| ((ArrayList) schemasInSession).size() == 0) {
			SchemaManager sm = new SchemaManager();
			schemasInSession = sm.getSchemas();
			httpServletRequest.getSession().setAttribute("conversion.schemas",
					schemasInSession);
		}
		Schema oSchema = new Schema();
		oSchema.setSchema(schema);
		return ((ArrayList) schemasInSession).contains(oSchema);
	}
}
