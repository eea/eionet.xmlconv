/*
 * Created on 10.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

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
import eionet.gdem.conversion.ssr.InputAnalyser;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IRootElemDao;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ListConversionsAction
 */

public class ListConversionsAction  extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();

	private IRootElemDao rootElemDao = GDEMServices.getDaoService().getRootElemDao();




	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		String ticket = (String) httpServletRequest.getSession().getAttribute(Names.TICKET_ATT);
		ActionErrors errors = new ActionErrors();
		ArrayList<Schema> schemas = new ArrayList<Schema>();
		ArrayList stylesheets = null;
		String actionForward="success";
		String idConv=null;

		ConversionForm cForm = (ConversionForm) actionForm;
		String schema = cForm.getSchemaUrl();
		String url = cForm.getUrl();

		//get request parameters
			
		/*
		if (url==null && schema==null) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.validation"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("error");
		}
		 */
		//forward to do converion
		if(httpServletRequest.getParameter("convertAction")!=null && !cForm.isConverted()){
			cForm.setConvertAction(null);
			cForm.setConverted(true);
			actionForward="convert";
		}
		else if(httpServletRequest.getParameter("searchAction")!=null){
			//search available conversions
			try {
				SchemaManager sm = new SchemaManager();
				ConversionService cs = new ConversionService();
				//list conversions by selected schema
				if (!Utils.isNullStr(schema)) {
					if (!schemaExists(httpServletRequest,schema)) {
						throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
					}
					stylesheets = sm.getSchemaStylesheets(schema);
					Schema oSchema = new Schema();
					oSchema.setSchema(schema);
					oSchema.setStylesheets(stylesheets);
					schemas.add(oSchema);
					cForm.setSchemas(schemas);
				}
				//sniff schema declaration from the header
				else {
					if (!Utils.isNullStr(url)) {
						InputAnalyser analyser = new InputAnalyser();
						try {
							analyser.parseXML(url);
						} catch (DCMException e) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage()));
							//saveMessages(httpServletRequest, errors);
							httpServletRequest.getSession().setAttribute("dcm.errors", errors);
						} catch (Exception e) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage()));
							//saveMessages(httpServletRequest, errors);
							httpServletRequest.getSession().setAttribute("dcm.errors", errors);
						}
						// schema or dtd found from header
						String schemaOrDTD = analyser.getSchemaOrDTD();
						if (schemaOrDTD != null) {
							stylesheets = sm.getSchemaStylesheets(schemaOrDTD);
							Schema oSchema = new Schema();
							oSchema.setSchema(schemaOrDTD);
							oSchema.setStylesheets(stylesheets);
							schemas.add(oSchema);
							cForm.setSchemas(schemas);
						}
						// did not find schema or dtd from xml header
						else {
							String root_elem = analyser.getRootElement();
							String namespace = analyser.getNamespace();
							Vector matchedSchemas = rootElemDao.getRootElemMatching(root_elem, namespace);
							for (int k = 0; k < matchedSchemas.size(); k++) {
								HashMap schemaHash = (HashMap) matchedSchemas.get(k);
								String schema_name = (String) schemaHash.get("xml_schema");
								stylesheets = sm.getSchemaStylesheets(schema_name);
								Schema oSchema = new Schema();
								oSchema.setSchema(schema_name);
								oSchema.setStylesheets(stylesheets);
								schemas.add(oSchema);
							}
							cForm.setSchemas(schemas);
						}
					}
				}
				if(cForm.getSchemas()==null || cForm.getSchemas().size()==0)
					cForm.setShowSchemaSelection(true);
				else{
					if (idConv == null && cForm.getSchemas().get(0).getStylesheets().size() > 0) {
						idConv = ((Stylesheet) (cForm.getSchemas().get(0).getStylesheets().get(0)))
							.getConvId();
					}
				}
				if (idConv == null) {
					idConv = "-1";
				}
				cForm.setConversionId(idConv);
				cForm.setConverted(false);
				cForm.setSearchAction(null);

				httpServletRequest.getSession().setAttribute("converted.url","");
				httpServletRequest.getSession().setAttribute("converted.conversionId","");

				if(cForm.getSchemas()==null || cForm.getSchemas().size()==0)
					cForm.setShowSchemaSelection(true);


			} catch (DCMException e) {
				e.printStackTrace();
				_logger.error("Error listing conversions",e);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
				//saveMessages(httpServletRequest, errors);
				httpServletRequest.getSession().setAttribute("dcm.errors", errors);
				return actionMapping.findForward("error");
			} catch (Exception e) {
				e.printStackTrace();
				_logger.error("Error listing conversions",e);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(BusinessConstants.EXCEPTION_GENERAL));
				//saveMessages(httpServletRequest, errors);
				httpServletRequest.getSession().setAttribute("dcm.errors", errors);
				return actionMapping.findForward("error");
			}
		}


		return actionMapping.findForward(actionForward);
	}


	private String processFormStr(String arg) {
		String result=null;
		if(arg!=null) {
			if(!arg.trim().equalsIgnoreCase("")) {
				result=arg.trim();
			}
		}
		return result;
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
