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
 * The Original Code is XMLCONV.
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 * 
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.web.struts.qasandbox;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.CrFileDto;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.qascript.QAScriptListHolder;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;

/**
 * SearchCRSandboxAction
 * Search XML files from Content Registry
 * 
 * @author Enriko Käsper, Tieto Estonia
 *  
 */

public class SearchCRSandboxAction extends Action {
	private static LoggerIF _logger = GDEMServices.getLogger();

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		ActionErrors errors = new ActionErrors();

		String schemaUrl = null;

		QASandboxForm cForm = (QASandboxForm) actionForm;

		schemaUrl = cForm.getSchemaUrl();
		Schema oSchema = cForm.getSchema();

		try {
			SchemaManager sm = new SchemaManager();
			// use the Schema data from the session, if schema is the same
			// otherwise load the data from database and search CR
			if (!Utils.isNullStr(schemaUrl)
					&& (oSchema == null || oSchema.getSchema() == null || !oSchema.getSchema().equals(schemaUrl) || oSchema
							.getCrfiles() == null)) {
				if (!schemaExists(httpServletRequest, schemaUrl)) {
					throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
				}
				List<CrFileDto> crfiles = null;
				crfiles = sm.getCRFiles(schemaUrl);
				if (oSchema == null)
					oSchema = new Schema();
				oSchema.setSchema(schemaUrl);
				oSchema.setCrfiles(crfiles);

				cForm.setSchema(oSchema);

				if (cForm.isShowScripts()) {
					return actionMapping.findForward("find");
				}
			}
		} catch (DCMException e) {
			// e.printStackTrace();
			_logger.error("Error searching XML files", e);
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		} catch (Exception e) {
			// e.printStackTrace();
			_logger.error("Error searching XML files", e);
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}

		return actionMapping.findForward("success");
	}

	/**
	 * check if schema passed as request parameter exists in the list of schemas
	 * stored in the session. If there is no schema list in the session, then
	 * create it
	 * 
	 * @param httpServletRequest
	 * @param schema
	 * @return
	 * @throws DCMException
	 */
	private boolean schemaExists(HttpServletRequest httpServletRequest, String schema) throws DCMException {
		Object schemasInSession = httpServletRequest.getSession().getAttribute("qascript.qascriptList");
		if (schemasInSession == null || ((QAScriptListHolder) schemasInSession).getQascripts().size() == 0) {
			schemasInSession = QAScriptListLoader.loadQAScriptList(httpServletRequest, true);
		}
		Schema oSchema = new Schema();
		oSchema.setSchema(schema);
		return ((QAScriptListHolder) schemasInSession).getQascripts().contains(oSchema);
	}

}
