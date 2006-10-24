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

package eionet.gdem.dcm.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.struts.upload.FormFile;


import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ConvType;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.stylesheet.ConvTypeHolder;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.services.db.dao.IStyleSheetDao;


public class StylesheetManager {
	private static LoggerIF _logger = GDEMServices.getLogger();
	  private  IStyleSheetDao styleSheetDao = GDEMServices.getDaoService().getStyleSheetDao();;
	  private  ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
	  private  IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();
	  


	public void delete(String user, String stylesheetId) throws DCMException {

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "d")) {
				_logger.debug("You don't have permissions to delete stylesheet!");
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_DELETE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error deleting stylesheet", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		try {
			HashMap hash = styleSheetDao.getStylesheetInfo(stylesheetId);
			String fileName = (String) hash.get("xsl");
			String xslFolder = Properties.xslFolder;
			if (!xslFolder.endsWith(File.separator)) xslFolder = xslFolder + File.separator;
			Utils.deleteFile(xslFolder + fileName);
			styleSheetDao.removeStylesheet(stylesheetId);
			

			/*
			 * //removing schema if it doesnt have stylesheets String schema =
			 * (String)hash.get("xml_schema"); String schemaId =
			 * (String)hash.get("schema_id");
			 * 
			 * Vector vDb = dbM.listConversions(schema);
			 * 
			 * if(vDb.size()==0) { dbM.removeSchema( schemaId, true, true, true); }
			 */
		} catch (Exception e) {
			_logger.error("Error deleting stylesheet", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}


	public ConvTypeHolder getConvTypes() throws DCMException {
		ConvTypeHolder ctHolder = new ConvTypeHolder();
		ArrayList convs;
		try {
			convs = new ArrayList();

			Vector convTypes = convTypeDao.getConvTypes();


			for (int i = 0; i < convTypes.size(); i++) {
				Hashtable hash = (Hashtable) convTypes.get(i);
				String conv_type = (String) hash.get("conv_type");

				ConvType conv = new ConvType();
				conv.setConvType(conv_type);
				convs.add(conv);
			}
			ctHolder.setConvTypes(convs);
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error getting conv types", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return ctHolder;

	}


	public void add(String user, String schema, FormFile file, String type, String descr) throws DCMException {

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "i")) {
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_INSERT);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error adding stylesheet", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		try {
			String fileName = file.getFileName();


			if (styleSheetDao.checkStylesheetFile(fileName)) {
				throw new DCMException(BusinessConstants.EXCEPTION_STYLEHEET_FILE_EXISTS);
			}

			InputStream in = file.getInputStream();
			String filepath = new String(Properties.xslFolder + "/" + file.getFileName());
			OutputStream w = new FileOutputStream(filepath);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
				w.write(buffer, 0, bytesRead);
			}
			w.close();
			in.close();
			file.destroy();

			String schemaID = schemaDao.getSchemaID(schema);
			if (schemaID == null) schemaID = schemaDao.addSchema(schema, null);
			
			styleSheetDao.addStylesheet(schemaID, type, fileName, descr);
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error adding stylesheet", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}


	public Stylesheet getStylesheet(String stylesheetId) throws DCMException {
		Stylesheet st = new Stylesheet();

		try {
			if (!stylesheetId.equals("")) {
				HashMap xsl = styleSheetDao.getStylesheetInfo(stylesheetId);

				if (xsl == null) xsl = new HashMap();

				st.setSchema((String) xsl.get("xml_schema"));
				st.setXsl_descr((String) xsl.get("description"));
				st.setType((String) xsl.get("content_type_out"));
				st.setXsl(Names.XSL_FOLDER + xsl.get("xsl"));
				st.setConvId(stylesheetId);
			}

		} catch (Exception e) {
			_logger.error("Error getting stylesheet", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return st;

	}


	public void update(String user, String xsl_id, String schema, FormFile file, String type, String descr) throws DCMException {
		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "u")) {
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_UPDATE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error updating stylesheet", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		try {
			String fileName = file.getFileName().trim();


			if (fileName != null && !fileName.equals("")) {
				if(!styleSheetDao.checkStylesheetFile(xsl_id, fileName)) {
					if (styleSheetDao.checkStylesheetFile(fileName)) {						
						throw new DCMException(BusinessConstants.EXCEPTION_STYLEHEET_FILE_EXISTS);
					}
				}

				InputStream in = file.getInputStream();
				String filepath = new String(Properties.xslFolder + "/" + fileName);
				OutputStream w = new FileOutputStream(filepath);
				int bytesRead = 0;
				byte[] buffer = new byte[8192];
				while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
					w.write(buffer, 0, bytesRead);
				}
				w.close();
				in.close();
				file.destroy();

				// delete Old xsl
				HashMap hash = styleSheetDao.getStylesheetInfo(xsl_id);


				String fileNameOld = (String) hash.get("xsl");
				String xslFolder = Properties.xslFolder;
				// if (!xslFolder.endsWith(File.separator))
				// xslFolder = xslFolder + File.separator;

				Utils.deleteFile(xslFolder + fileNameOld);

			}
			String schemaID = schemaDao.getSchemaID(schema);
			
			if (schemaID == null) schemaID = schemaDao.addSchema(schema, null);
			styleSheetDao.updateStylesheet(xsl_id, schemaID, descr, fileName, type);
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error updating stylesheet", e);			
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}

}
