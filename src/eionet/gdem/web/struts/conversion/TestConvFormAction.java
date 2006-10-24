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
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Istvan Alfeldi (ED)
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
import org.apache.struts.validator.DynaValidatorForm;

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
import eionet.gdem.validation.ValidationService;

public class TestConvFormAction extends Action {

	private static LoggerIF _logger = GDEMServices.getLogger();

	  private IRootElemDao rootElemDao = GDEMServices.getDaoService().getRootElemDao();
	  

	

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		String ticket = (String) httpServletRequest.getSession().getAttribute(Names.TICKET_ATT);
		ActionErrors errors = new ActionErrors();
		ArrayList schemas = new ArrayList();

		DynaValidatorForm cForm = (DynaValidatorForm) actionForm;
		String schema = processFormStr((String) cForm.get("schemaUrl"));
		String xmlUrl = processFormStr((String) cForm.get("url"));
		String idConv = processFormStr((String) cForm.get("conversionId"));
		String validate = processFormStr((String) cForm.get("validate"));

		_logger.debug(schema); _logger.debug(xmlUrl); _logger.debug(idConv); _logger.debug(validate);

		if (xmlUrl==null && schema==null) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.validation"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("back");
		}


		try {
			SchemaManager sm = new SchemaManager();
			ConversionService cs = new ConversionService();
			if (!Utils.isNullStr(schema)) {
				if (!cs.existsXMLSchema(schema)) {
					throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
				}
				ArrayList stylesheets = null;
				ArrayList cdrfiles = null;
				stylesheets = sm.getSchemaStylesheets(schema);
				cdrfiles = sm.getCdrFiles(schema);
				Schema scObj = new Schema();
				scObj.setSchema(schema);
				scObj.setStylesheets(stylesheets);
				scObj.setCdrfiles(cdrfiles);
				schemas.add(scObj);
			} else {
				if (!Utils.isNullStr(xmlUrl)) {
					InputAnalyser analyser = new InputAnalyser();
					try {
						analyser.parseXML(xmlUrl);
					} catch (DCMException e) {
						throw e;
					} catch (Exception e) {
						throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
					}
					// schema or dtd found from header
					String schemaOrDTD = analyser.getSchemaOrDTD();

					if (schemaOrDTD != null) {
						ArrayList stylesheets = null;
						ArrayList cdrfiles = null;
						stylesheets = sm.getSchemaStylesheets(schemaOrDTD);
						cdrfiles = sm.getCdrFiles(schemaOrDTD);
						Schema scObj = new Schema();
						scObj.setSchema(schemaOrDTD);
						scObj.setStylesheets(stylesheets);
						scObj.setCdrfiles(cdrfiles);
						schemas.add(scObj);
					}
					// did not find schema or dtd from xml header
					else {
						String root_elem = analyser.getRootElement();

						String namespace = analyser.getNamespace();



						Vector matchedSchemas = rootElemDao.getRootElemMatching(root_elem, namespace);

						for (int k = 0; k < matchedSchemas.size(); k++) {
							HashMap schemaHash = (HashMap) matchedSchemas.get(k);
							String schema_name = (String) schemaHash.get("xml_schema");
							ArrayList stylesheets = null;
							ArrayList cdrfiles = null;
							stylesheets = sm.getSchemaStylesheets(schema_name);
							cdrfiles = sm.getCdrFiles(schema_name);
							Schema scObj = new Schema();
							scObj.setSchema(schema_name);
							scObj.setStylesheets(stylesheets);
							scObj.setCdrfiles(cdrfiles);
							schemas.add(scObj);
						}

					}
				}

			}

			if (idConv == null && schemas.size() > 0 && ((Schema) schemas.get(0)).getStylesheets().size() > 0) {
				idConv = ((Stylesheet) (((Schema) schemas.get(0)).getStylesheets().get(0))).getConvId();
			}
			if (idConv == null) {
				idConv = "-1";
			}

			cForm.set("conversionId", idConv);

			if (validate != null) {
				ArrayList valid;
				if (schema == null) { // schema defined in header
					valid = validate(xmlUrl, ticket);
				} else {
					valid = validateSchema(xmlUrl, schema, ticket);
				}
				httpServletRequest.setAttribute("conversion.valid", valid);
			}

		} catch (DCMException e) {
			e.printStackTrace();
			_logger.error("Error testing conversion",e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			//saveMessages(httpServletRequest, errors);
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("error");
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error testing conversion",e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(BusinessConstants.EXCEPTION_GENERAL));
			//saveMessages(httpServletRequest, errors);
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("error");
		}


		if(schemas.size()>0){
			httpServletRequest.getSession().setAttribute("conversion.schemas", schemas);
			return actionMapping.findForward("success");
		}else{
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.noconversion"));
			httpServletRequest.getSession().setAttribute("dcm.errors", errors);
			return actionMapping.findForward("back");
		}
	}


	private ArrayList validate(String url, String ticket) throws DCMException {
		try {
			ValidationService v = new ValidationService(true);
			v.setTrustedMode(false);
	    	v.setTicket(ticket);
			v.validate(url);
			return v.getErrorList();
		} catch (Exception e) {
			throw new DCMException(BusinessConstants.EXCEPTION_VALIDATION_ERROR);
		}
	}


	private ArrayList validateSchema(String url, String schema, String ticket) throws DCMException {
		try {
			ValidationService v = new ValidationService(true);
			v.setTrustedMode(false);
	    	v.setTicket(ticket);
			v.validateSchema(url, schema);
			v.printList();
			return v.getErrorList();

		} catch (Exception e) {
			throw new DCMException(BusinessConstants.EXCEPTION_VALIDATION_ERROR);
		}
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

}
