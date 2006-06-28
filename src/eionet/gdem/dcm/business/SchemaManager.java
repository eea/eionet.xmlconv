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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.struts.upload.FormFile;

import eionet.gdem.Properties;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dto.CdrFileDto;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.dto.RootElem;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.dto.UplSchema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.schema.SchemaElemHolder;
import eionet.gdem.web.struts.schema.UplSchemaHolder;
import eionet.gdem.web.struts.stylesheet.StylesheetListHolder;

public class SchemaManager {

	private static LoggerIF _logger = GDEMServices.getLogger();


	public void delete(String user, String schemaId) throws DCMException {

		boolean hasOtherStuff = false;

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "d")) {
				_logger.debug("You don't have permissions to delete schemas!");
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_DELETE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.debug(e.toString(), e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		try {
			DbModuleIF dbM = GDEMServices.getDbModule();

			Vector stylesheets = dbM.getSchemaStylesheets(schemaId);
			if (stylesheets != null) {
				for (int i = 0; i < stylesheets.size(); i++) {
					HashMap hash = (HashMap) stylesheets.get(i);
					String xslFile = (String) hash.get("xsl");

					String xslFolder = Properties.xslFolder;
					if (!xslFolder.endsWith(File.separator)) xslFolder = xslFolder + File.separator;

					try {
						Utils.deleteFile(xslFolder + xslFile);
					} catch (Exception e) {
						_logger.debug("Error deleting file", e);
						throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
					}
				}
			}
			if (dbM.getSchemaQueries(schemaId) != null) hasOtherStuff = true;

			// dbM.removeSchema( schemaId, true, false, !hasOtherStuff);
			dbM.removeSchema(schemaId, true, true, true);
		} catch (Exception e) {
			_logger.debug("Error removing schema", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}


	public StylesheetListHolder getSchemas(String user_name) throws DCMException {

		StylesheetListHolder st = new StylesheetListHolder();

		boolean ssiPrm = false;
		boolean ssdPrm = false;
		Vector hcSchemas;
		ArrayList schemas;

		try {
			schemas = new ArrayList();
			ssiPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "i");
			ssdPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "d");

			st.setSsdPrm(ssdPrm);
			st.setSsiPrm(ssiPrm);

			DbModuleIF dbM = GDEMServices.getDbModule();
			hcSchemas = dbM.getSchemas(null);
			if (hcSchemas == null) hcSchemas = new Vector();

			for (int i = 0; i < hcSchemas.size(); i++) {
				HashMap schema = (HashMap) hcSchemas.get(i);
				Schema sc = new Schema();
				sc.setId((String) schema.get("schema_id"));
				sc.setSchema((String) schema.get("xml_schema"));
				sc.setDescription((String) schema.get("description"));

				Vector stylesheets = new Vector();
				if (schema.containsKey("stylesheets")) {
					stylesheets = (Vector) schema.get("stylesheets");
				}

				ArrayList stls = new ArrayList();
				for (int j = 0; j < stylesheets.size(); j++) {
					HashMap stylesheet = (HashMap) stylesheets.get(j);
					Stylesheet stl = new Stylesheet();
					// st.setConvId(1);
					stl.setType((String) stylesheet.get("content_type_out"));
					stl.setXsl(Names.XSL_FOLDER + (String) stylesheet.get("xsl"));
					stl.setXsl_descr((String) stylesheet.get("description"));
					stl.setDdConv(false);
					stls.add(stl);
				}

				// if (stls.size() > 0) {
				sc.setStylesheets(stls);
				schemas.add(sc);
				// }
			}
			if(schemas.size()>0){
				st.setHandCodedStylesheets(schemas);
			}

			// retrive conversions for DD tables
			List ddTables = DDServiceClient.getDDTables();
			schemas = new ArrayList();

			for (int i = 0; i < ddTables.size(); i++) {
				Hashtable schema = (Hashtable) ddTables.get(i);
				String tblId = (String) schema.get("tblId");
				String schemaUrl = Properties.ddURL + "/GetSchema?id=TBL" + tblId;

				Schema sc = new Schema();
				sc.setId("TBL" + tblId);
				sc.setSchema(schemaUrl);
				sc.setTable((String) schema.get("shortName"));
				sc.setDataset((String) schema.get("dataSet"));

				List ddStylesheets = Conversion.getConversions();
				ArrayList stls = new ArrayList();

				for (int j = 0; j < ddStylesheets.size(); j++) {
					ConversionDto ddConv = ((ConversionDto) ddStylesheets.get(j));

					String convId = ddConv.getConvId();
					String xsl_url = Properties.gdemURL + "/do/getStylesheet?id=" + tblId + "&conv=" + convId;

					Stylesheet stl = new Stylesheet();
					stl.setType(ddConv.getResultType());
					stl.setXsl(xsl_url);
					stl.setXsl_descr(ddConv.getDescription());
					stl.setDdConv(true);
					stls.add(stl);
				}
				sc.setStylesheets(stls);
				schemas.add(sc);
			}

			BeanComparator comp = new BeanComparator("table");
			Collections.sort(schemas, comp);

			st.setDdStylesheets(schemas);

		} catch (Exception e) {
			_logger.debug("Error getting schema",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return st;

	}


	public StylesheetListHolder getSchemaStylesheets(String schema, String user_name) throws DCMException {
		StylesheetListHolder st = new StylesheetListHolder();

		Vector hcSchemas;
		ArrayList schemas;

		try {

			schemas = new ArrayList();
			boolean ssiPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "i");
			boolean ssdPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "d");
			boolean convPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_TESTCONVERSION_PATH, "x");

			st.setSsdPrm(ssdPrm);
			st.setSsiPrm(ssiPrm);
			st.setConvPrm(convPrm);

			DbModuleIF dbM = GDEMServices.getDbModule();

			String schemaId = dbM.getSchemaID(schema);

			if (schemaId == null) {
				st.setHandcoded(false);
			} else {
				st.setHandcoded(true);
			}

			ConversionService cs = new ConversionService();
			Vector stylesheets = cs.listConversions(schema);
			ArrayList stls = new ArrayList();
			Schema sc = new Schema();
			sc.setId(schemaId);
			sc.setSchema(schema);

			for (int i = 0; i < stylesheets.size(); i++) {
				Hashtable hash = (Hashtable) stylesheets.get(i);
				String convert_id = (String) hash.get("convert_id");
				String xsl = (String) hash.get("xsl");
				String type;
				String description = (String) hash.get("description");
				String last_modified = "";
				boolean ddConv = false;
				String xslUrl;

				if (!xsl.startsWith(Properties.gdemURL + "/do/getStylesheet?id=")) {

					File f = new File(Properties.xslFolder + File.separatorChar +  xsl);
					if (f != null) last_modified = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(f.lastModified()));
					xslUrl = Names.XSL_FOLDER + (String) hash.get("xsl");
					type = (String) hash.get("result_type");
				} else {
					xslUrl = (String) hash.get("xsl");
					ddConv = true;
					type = (String) hash.get("result_type");
				}

				Stylesheet stl = new Stylesheet();
				// st.setConvId(1);
				stl.setType(type);
				stl.setXsl(xslUrl);
				stl.setXsl_descr((String) hash.get("description"));
				stl.setModified(last_modified);
				stl.setConvId((String) hash.get("convert_id"));
				stl.setDdConv(ddConv);
				stls.add(stl);

			}
			if (stls.size() > 0) {
				sc.setStylesheets(stls);
			}
			schemas.add(sc);
			st.setHandCodedStylesheets(schemas);
		} catch (Exception e) {
			_logger.debug("Errror getting schema stylesheets",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return st;
	}


	public void update(String user, String schemaId, String schema, String description, String dtdPublicId) throws DCMException {

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "u")) {
				_logger.debug("You don't have permissions to delete schemas!");
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_UPDATE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.debug("Error updating schema", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		// StringBuffer err_buf = new StringBuffer();
		// String del_id= (String)req.getParameter(Names.XSD_DEL_ID);

		try {
			DbModuleIF dbM = GDEMServices.getDbModule();
			dbM.updateSchema(schemaId, schema, description, dtdPublicId);
		} catch (Exception e) {
			_logger.debug("Error updating schema", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}


	public SchemaElemHolder getSchemaElems(String user_name, String schemaId) throws DCMException {

		SchemaElemHolder se = new SchemaElemHolder();

		boolean xsduPrm = false;
		Schema schema;
		ArrayList elems;

		try {
			elems = new ArrayList();
			xsduPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "u");

			se.setXsduPrm(xsduPrm);

			DbModuleIF dbM = GDEMServices.getDbModule();

			Vector list = dbM.getSchemas(schemaId, false);
			if (list == null) list = new Vector();

			String name = "";
			String schema_desc = null;
			String dtd_public_id = null;
			boolean isDTD = false;

			if (list.size() > 0) {

				schema = new Schema();

				HashMap schemaHash = (HashMap) list.get(0);
				schema.setSchema((String) schemaHash.get("xml_schema"));
				schema.setDescription((String) schemaHash.get("description"));
				schema.setDtdPublicId((String) schemaHash.get("dtd_public_id"));
				name = (String) schemaHash.get("xml_schema");
				int name_len = name.length();
				if (name_len > 3) {
					String schema_end = name.substring((name_len - 3), (name_len)).toLowerCase();
					if (schema_end.equals("dtd")) isDTD = true;
				}
				schema.setIsDTD(isDTD);
				se.setSchema(schema);
			}

			Vector root_elems = (Vector) dbM.getSchemaRootElems(schemaId);
			if (root_elems == null) root_elems = new Vector();

			for (int i = 0; i < root_elems.size(); i++) {
				HashMap hash = (HashMap) root_elems.get(i);

				RootElem rElem = new RootElem();
				rElem.setElemId((String) hash.get("rootelem_id"));
				rElem.setName((String) hash.get("elem_name"));
				rElem.setNamespace((String) hash.get("namespace"));
				elems.add(rElem);
			}
			if (elems.size() > 0) {
				se.setRootElem(elems);
			}

		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error getting root elements", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return se;

	}


	public ArrayList getSchemas() throws DCMException {

		ArrayList schemas = new ArrayList();
		ArrayList schemasChk = new ArrayList();
		Vector hcSchemas;

		try {

			// retrive conversions for DD tables
			List ddTables = DDServiceClient.getDDTables();

			for (int i = 0; i < ddTables.size(); i++) {
				Hashtable schema = (Hashtable) ddTables.get(i);
				String tblId = (String) schema.get("tblId");
				String schemaUrl = Properties.ddURL + "/GetSchema?id=TBL" + tblId;

				Schema sc = new Schema();
				sc.setId("TBL" + tblId);
				sc.setSchema(schemaUrl);
				sc.setTable((String) schema.get("shortName"));
				sc.setDataset((String) schema.get("dataSet"));
				schemas.add(sc);
				schemasChk.add(schema.get("xml_schema"));
			}

			BeanComparator comp = new BeanComparator("table");
			Collections.sort(schemas, comp);

			// append handcoded conversions
			DbModuleIF dbM = GDEMServices.getDbModule();
			hcSchemas = dbM.getSchemasWithStl();

			if (hcSchemas == null) hcSchemas = new Vector();

			for (int i = 0; i < hcSchemas.size(); i++) {
				HashMap schema = (HashMap) hcSchemas.get(i);
				if (!schemasChk.contains(schema.get("xml_schema"))) {
					Schema sc = new Schema();
					sc.setId((String) schema.get("schema_id"));
					sc.setSchema((String) schema.get("xml_schema"));
					sc.setDescription((String) schema.get("description"));
					schemas.add(sc);
					schemasChk.add(schema.get("xml_schema"));
				}
			}

		} catch (Exception e) {
			_logger.debug("Error getting schema",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		return schemas;

	}


	public ArrayList getSchemaStylesheets(String schema) throws DCMException {

		Vector hcSchemas;
		ArrayList stls = new ArrayList();

		try {
			DbModuleIF dbM = GDEMServices.getDbModule();
			ConversionService cs = new ConversionService();
			Vector stylesheets = cs.listConversions(schema);

			for (int i = 0; i < stylesheets.size(); i++) {
				Hashtable hash = (Hashtable) stylesheets.get(i);
				String convert_id = (String) hash.get("convert_id");
				String xsl = (String) hash.get("xsl");
				String type;
				String description = (String) hash.get("description");
				String last_modified = "";
				boolean ddConv = false;
				String xslUrl;

				if (!xsl.startsWith(Properties.gdemURL + "/do/getStylesheet?id=")) {
					xslUrl = Properties.gdemURL + "/" + Names.XSL_FOLDER + (String) hash.get("xsl");

					type = (String) hash.get("result_type");
				} else {
					xslUrl = (String) hash.get("xsl");
					ddConv = true;
					type = (String) hash.get("result_type");
				}

				Stylesheet stl = new Stylesheet();
				// st.setConvId(1);
				stl.setType(type);
				stl.setXsl(xslUrl);
				stl.setXsl_descr((String) hash.get("description"));
				stl.setConvId((String) hash.get("convert_id"));
				stl.setDdConv(ddConv);
				stls.add(stl);
			}

		} catch (Exception e) {
			_logger.error("Error getting schema stylesheets",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return stls;
	}


	public UplSchemaHolder getUplSchemas(String user_name) throws DCMException {

		UplSchemaHolder sc = new UplSchemaHolder();
		ArrayList schemas;

		boolean ssiPrm = false;
		boolean ssdPrm = false;
		boolean ssuPrm = false;

		try {

			ssiPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "i");
			ssdPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "d");
			ssuPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "u");

			sc.setSsdPrm(ssdPrm);
			sc.setSsiPrm(ssiPrm);
			sc.setSsuPrm(ssuPrm);

			schemas = new ArrayList();

			DbModuleIF dbM = GDEMServices.getDbModule();
			Vector schemaVec = dbM.getUplSchema();

			for (int i = 0; i < schemaVec.size(); i++) {
				Hashtable hash = (Hashtable) schemaVec.get(i);
				String id = (String) hash.get("id");
				String schema = Properties.gdemURL + "/schema/" + (String) hash.get("schema");
				String desc = (String) hash.get("description");

				UplSchema uplSchema = new UplSchema();
				uplSchema.setId(id);
				uplSchema.setSchema(schema);
				uplSchema.setDescription(desc);
				schemas.add(uplSchema);
			}
			if (schemas.size() > 0) {
				sc.setSchemas(schemas);
			}

		} catch (Exception e) {
			_logger.error("Error getting uploaded schema",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return sc;

	}


	public void addUplSchema(String user, FormFile file, String desc) throws DCMException {

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "i")) {
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_INSERT);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error adding upoaded schema",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		try {
			String fileName = file.getFileName();
			DbModuleIF dbM = GDEMServices.getDbModule();

			if (dbM.checkUplSchemaFile(fileName)) {
				throw new DCMException(BusinessConstants.EXCEPTION_UPLSCHEMA_FILE_EXISTS);
			}

			InputStream in = file.getInputStream();
			String filepath = new String(Properties.schemaFolder + File.separatorChar + file.getFileName());
			OutputStream w = new FileOutputStream(filepath);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
				w.write(buffer, 0, bytesRead);
			}
			w.close();
			in.close();
			file.destroy();

			dbM.addUplSchema(fileName, desc);
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error adding upoaded schema",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}


	public void deleteUplSchema(String user, String uplSchemaId) throws DCMException {

		boolean hasOtherStuff = false;

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "d")) {
				_logger.debug("You don't have permissions to delete schemas!");
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_DELETE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error deleting upoaded schema", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		try {
			DbModuleIF dbM = GDEMServices.getDbModule();

			String schema = dbM.getUplSchema(uplSchemaId);

			if (schema != null) {

				String schemaId = dbM.getSchemaID(Properties.gdemURL + "/schema/" + schema);

				if (schemaId != null) {
					Vector stylesheets = dbM.getSchemaStylesheets(schemaId);
					if (stylesheets != null) {
						for (int i = 0; i < stylesheets.size(); i++) {
							HashMap hash = (HashMap) stylesheets.get(i);
							String xslFile = (String) hash.get("xsl");

							String xslFolder = Properties.xslFolder;
							if (!xslFolder.endsWith(File.separator)) xslFolder = xslFolder + File.separator;

							try {
								Utils.deleteFile(xslFolder + xslFile);
							} catch (Exception e) {
								_logger.error("Error deleting stylesheet files",e);
								throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
							}
						}
					}
					if (dbM.getSchemaQueries(schemaId) != null) hasOtherStuff = true;

					dbM.removeSchema(schemaId, true, false, !hasOtherStuff);
				}
			}
			try {
				Utils.deleteFile(Properties.schemaFolder + "/" + schema);
			} catch (Exception e) {
				_logger.error("Error deleting upoladed schema file",e);
				throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
			}
			dbM.removeUplSchema(uplSchemaId);
		} catch (Exception e) {
			_logger.error("Error deleting uploaded schema",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}


	public ArrayList getDDSchemas() throws DCMException {
		ArrayList schemas = new ArrayList();
		ArrayList schemasChk = new ArrayList();

		try {

			// retrive conversions for DD tables
			List ddTables = DDServiceClient.getDDTables();
			for (int i = 0; i < ddTables.size(); i++) {
				Hashtable schema = (Hashtable) ddTables.get(i);
				String tblId = (String) schema.get("tblId");
				String schemaUrl = Properties.ddURL + "/GetSchema?id=TBL" + tblId;
				Schema sc = new Schema();
				sc.setId("TBL" + tblId);
				sc.setSchema(schemaUrl);
				sc.setTable((String) schema.get("shortName"));
				sc.setDataset((String) schema.get("dataSet"));
				schemas.add(sc);
				schemasChk.add(schema.get("xml_schema"));
			}
			BeanComparator comp = new BeanComparator("table");
			Collections.sort(schemas, comp);
		} catch (Exception e) {
			_logger.error("Error getting DD schemas",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return schemas;
	}


	public Schema getSchema(String schemaId) throws DCMException {

		HashMap sch = null;
		Schema schema = null;

		try {
			DbModuleIF dbM = GDEMServices.getDbModule();
			sch = dbM.getSchema(schemaId);

			schema = new Schema();
			schema.setId(schemaId);
			schema.setSchema((String) sch.get("xml_schema"));
			schema.setDescription((String) sch.get("description"));
			schema.setDtdPublicId((String) sch.get("dtd_public_id"));

		} catch (Exception e) {
			_logger.error("Error getting schema",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return schema;
	}


	public void updateUplSchema(String user, String schemaId, String description) throws DCMException {

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "u")) {
				_logger.debug("You don't have permissions to update schemas!");
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_UPDATE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error updating uploaded schema", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		try {
			DbModuleIF dbM = GDEMServices.getDbModule();
			// dbM.updateSchema(schemaId, schema, description, dtdPublicId);
			dbM.updateUplSchema(schemaId, description);
		} catch (Exception e) {
			_logger.error("Error updating uploaded schema", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}


	public UplSchema getUplSchemasById(String schemaId) throws DCMException {

		UplSchema sc = new UplSchema();
		try {
			DbModuleIF dbM = GDEMServices.getDbModule();
			Hashtable ht = dbM.getUplSchemaById(schemaId);
			String schema = (String) ht.get("schema");
			String desc = (String) ht.get("description");

			sc.setDescription(desc);
			sc.setId(schemaId);
			sc.setSchema(schema);
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error getting uploaded schema", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return sc;

	}
	public ArrayList getCdrFiles(String schema) throws DCMException {

		ArrayList files = new ArrayList();
		try{
			// retrive conversions for DD tables
			List cdrFiles = CDRServiceClient.searchXMLFiles(schema);

			for (int i = 0; i < cdrFiles.size(); i++) {
				Hashtable xmlfile = (Hashtable) cdrFiles.get(i);
				String url = (String) xmlfile.get("url");

				CdrFileDto cf = new CdrFileDto();
				cf.setUrl(url);
				cf.setCountry((String) xmlfile.get("country"));
				cf.setIso((String) xmlfile.get("iso"));
				cf.setPartofyear((String) xmlfile.get("partofyear"));
				cf.setTitle((String) xmlfile.get("title"));
				//year and endyear are Integers, if not null
				Object endyear =xmlfile.get("endyear");
				if(endyear instanceof Integer)
					cf.setEndyear(((Integer)endyear).intValue());

				Object year =(Integer) xmlfile.get("year");
				if(year instanceof Integer)
					cf.setYear(((Integer)year).intValue());

				files.add(cf);
			}
		} catch (Exception e) {
			_logger.error("Error getting XML files from CDR for schema",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return files;
	}



	public static void main(String[] args) throws DCMException {
		String xmlSchema = "http://rubi:8080/xmlconv/schema/install_wizard.log";
		if (xmlSchema.startsWith(Properties.gdemURL + "schema/")) {
			int i = xmlSchema.indexOf(Properties.gdemURL + "schema/");
			String url = Properties.gdemURL + "schema/";
			String schema = xmlSchema.substring(url.length(), xmlSchema.length());
			System.out.println(schema);
		}
		// xmlSchema.substring(xmlSchema.indexOf(Properties.gdemURL))

		// SchemaManager s = new SchemaManager();
		// SchemaElemHolder d = s.getSchemaElems( "_admin","37");
	}

}
