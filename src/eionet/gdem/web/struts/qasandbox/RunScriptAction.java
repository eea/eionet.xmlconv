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

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.Constants;
import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.business.ConvTypeManager;
import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.dto.ConvType;
import eionet.gdem.dto.QAScript;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.ValidationService;

/**
 * EditQAScriptInSandboxAction
 * Execute the QA script and display the results.
 * If the result of QA script is not html, then wire the result directly into Servlet OutputStream.
 * 
 * @author Enriko Käsper, Tieto Estonia
 */

public class RunScriptAction extends Action {

	private static final String HTML_CONTENT_TYPE = "text/html";
	private static final String HTML_CHARACTER_ENCODING = "utf-8";
	private static LoggerIF _logger = GDEMServices.getLogger();

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ActionErrors errors = new ActionErrors();

		QASandboxForm cForm = (QASandboxForm) actionForm;
		cForm.setResult(null);
		String scriptId = cForm.getScriptId();
		String scriptContent = cForm.getScriptContent();
		String scriptType = cForm.getScriptType();
		String sourceUrl = cForm.getSourceUrl();
		boolean showScripts = cForm.isShowScripts();

		if (showScripts && Utils.isNullStr(scriptId)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingId"));
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}
		if (!showScripts && Utils.isNullStr(scriptContent)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingContent"));
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}
		if (Utils.isNullStr(sourceUrl)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingUrl"));
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}
		if (!Utils.isURL(sourceUrl)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.notUrl"));
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}

		try {
			String result = null;

			// VALIDATION! if it is a validation job, then do the action and get
			// out of here
			if (scriptId.equals(String.valueOf(Constants.JOB_VALIDATION))) {
				try {
					ValidationService vs = new ValidationService();
					vs.setTrustedMode(false);

					// result = vs.validateSchema(dataURL, xml_schema);
					result = vs.validate(sourceUrl);
				} catch (DCMException de) {
					result = de.getMessage();
				}
				cForm.setResult(result);
				return actionMapping.findForward("success");
			}

			QAScript qascript = null;
			String outputContentType = HTML_CONTENT_TYPE;
			String xqResultType = null;
			QAScriptManager qm = new QAScriptManager();
			ConvTypeManager ctm = new ConvTypeManager();

			// get QA script
			if (!Utils.isNullStr(scriptId) && !"0".equals(scriptId)) {
				qascript = qm.getQAScript(scriptId);
				String resultType = qascript.getResultType();
				// get correct putput type by convTypeId
				ConvType cType = ctm.getConvType(resultType);
				if (cType != null && !Utils.isNullStr(cType.getContType())) {
					outputContentType = cType.getContType();
					xqResultType = cType.getConvType();
				}

			}
			XQScript xq = null;
			if (showScripts) {
				// run script by ID
				// read scriptContent from file
				try {
					scriptContent = Utils.readStrFromFile(Properties.queriesFolder + qascript.getFileName());
				} catch (Exception e) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.fileNotFound"));
					saveErrors(httpServletRequest, errors);
					return actionMapping.findForward("error");
				}
			}
			String[] pars = new String[1];
			pars[0] = Constants.XQ_SOURCE_PARAM_NAME + "=" + sourceUrl;
			xq = new XQScript(scriptContent, pars, xqResultType);
			xq.setScriptType(scriptType);
			xq.setSrcFileUrl(sourceUrl);

			OutputStream output = null;
			try {
				// write the result directly to servlet boutputstream
				if (!outputContentType.startsWith(HTML_CONTENT_TYPE)) {
					httpServletResponse.setContentType(outputContentType);
					httpServletResponse.setCharacterEncoding(HTML_CHARACTER_ENCODING);
					output = httpServletResponse.getOutputStream();
					xq.getResult(output);
					output.flush();
					output.close();
					return null;
				} else {
					result = xq.getResult();
					cForm.setResult(result);

				}
			} catch (GDEMException ge) {
				result = ge.getMessage();
				if (output == null) {
					cForm.setResult(result);
					return actionMapping.findForward("success");
				} else {
					output.write(result.getBytes());
					output.flush();
					output.close();
					return null;
				}
			}
		} catch (Exception e) {
			_logger.error("Error executing QA script", e);
			saveErrors(httpServletRequest, errors);
			return actionMapping.findForward("error");
		}

		return actionMapping.findForward("success");
	}
}
