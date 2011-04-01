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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.struts.upload.FormFile;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dto.CdrFileDto;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.dto.CrFileDto;
import eionet.gdem.dto.QAScript;
import eionet.gdem.dto.RootElem;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.dto.UplSchema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IRootElemDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.services.db.dao.IUPLSchemaDao;
import eionet.gdem.utils.HttpUtils;
import eionet.gdem.utils.MultipartFileUpload;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.qascript.QAScriptListHolder;
import eionet.gdem.web.struts.schema.SchemaElemHolder;
import eionet.gdem.web.struts.schema.UplSchemaHolder;
import eionet.gdem.web.struts.stylesheet.StylesheetListHolder;


public class SchemaManager {

    private static LoggerIF _logger = GDEMServices.getLogger();

    private  ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
    private  IRootElemDao rootElemDao = GDEMServices.getDaoService().getRootElemDao();
    private  IUPLSchemaDao uplSchemaDao = GDEMServices.getDaoService().getUPLSchemaDao();




    public void deleteSchemaStylesheets(String user, String schemaId) throws DCMException {

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


            Vector stylesheets = schemaDao.getSchemaStylesheets(schemaId);

            if (!Utils.isNullVector(schemaDao.getSchemaQueries(schemaId))||
                    uplSchemaDao.checkUplSchemaFK(schemaId))
                hasOtherStuff = true;


            // dbM.removeSchema( schemaId, true, false, !hasOtherStuff);
            schemaDao.removeSchema(schemaId, true, false, false, !hasOtherStuff);

            //delete stylesheet files only if db operation succeeded
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

        } catch (Exception e) {
            _logger.debug("Error removing schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Method creates StylesheetListHolder object, that stores the list of stylesheets both for handcoded and generated.
     * The object stores also user permissions info to manage stylesheets.
     *
     * @param user_name		user name stored in HTTP session.This for checking user permisssions
     * @param type			if type==null, then show only handcoded stylesheets. If type==generated, then
     * 						show only generated stylesheets. If type==both, then show both handcoded and generated stylesheets
     * @return
     * @throws DCMException
     */
    public StylesheetListHolder getSchemas(String user_name,String type) throws DCMException {

        StylesheetListHolder st = new StylesheetListHolder();
        if(Utils.isNullStr(type))type="handcoded";

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

            //By default show only handcoded stylesheets
            if(type.equals("handcoded")|| type.equals("all")){
                hcSchemas = schemaDao.getSchemas(null);
                if (hcSchemas == null) hcSchemas = new Vector();

                for (int i = 0; i < hcSchemas.size(); i++) {
                    HashMap schema = (HashMap) hcSchemas.get(i);
                    Schema sc = new Schema();
                    sc.setId((String) schema.get("schema_id"));
                    sc.setSchema((String) schema.get("xml_schema"));
                    sc.setDescription((String) schema.get("description"));
                    sc.setExpireDate(Utils.parseDate((String) schema.get("expire_date"),"yyyy-MM-dd HH:mm:ss"));

                    Vector stylesheets = new Vector();
                    if (schema.containsKey("stylesheets")) {
                        stylesheets = (Vector) schema.get("stylesheets");
                    }

                    ArrayList stls = new ArrayList();
                    for (int j = 0; j < stylesheets.size(); j++) {
                        HashMap stylesheet = (HashMap) stylesheets.get(j);
                        Stylesheet stl = new Stylesheet();
                        stl.setConvId((String) stylesheet.get("convert_id"));
                        stl.setType((String) stylesheet.get("content_type_out"));
                        stl.setXsl(Names.XSL_FOLDER + (String) stylesheet.get("xsl"));
                        stl.setXsl_descr((String) stylesheet.get("description"));
                        stl.setDdConv(false);
                        stls.add(stl);
                    }
                    if (stls.size() > 0) {
                        sc.setStylesheets(stls);
                        schemas.add(sc);
                    }
                }
                if(schemas.size()>0){
                    st.setHandCodedStylesheets(schemas);
                }
            }

            //if type==all, then show both handcoded and generated styleseheets
            //if type==generated, then show only generated from DD  sttyleseets
            if(type.equals("generated") || type.equals("all")){
            // retrive conversions for DD tables
                schemas = getDDSchemas(true);
                st.setDdStylesheets(schemas);
            }

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

            String schemaId = schemaDao.getSchemaID(schema);


            if (schemaId == null) {
                st.setHandcoded(false);
            } else {
                st.setHandcoded(true);
            }

            ConversionServiceIF cs = new ConversionService();
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
                    if (f != null && f.exists()) last_modified = Utils.getDateTime(new Date(f.lastModified()));
                    //DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(f.lastModified()));
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


    /**
     * Method creates QAScriptListHolder object, that stores the list of qa script
     * The object stores also user permissions info to manage stylesheets.
     *
     * @param user_name		user name stored in HTTP session.This for checking user permisssions
     * @return
     * @throws DCMException
     */
    public QAScriptListHolder getSchemasWithQAScripts(String user_name) throws DCMException {
        return getSchemasWithQAScripts(user_name, null);
    }
    public QAScriptListHolder getSchemasWithQAScripts(String user_name, String schemaId) throws DCMException {

        QAScriptListHolder st = new QAScriptListHolder();

        boolean ssiPrm = false;
        boolean ssdPrm = false;
        boolean wqiPrm = false;
        boolean wquPrm = false;
        boolean qsiPrm = false;
        boolean qsuPrm = false;
        Vector hcSchemas;
        List<Schema> schemas;

        try {
            schemas = new ArrayList<Schema>();
            ssiPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "i");
            ssdPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "d");
            wqiPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_WQ_PATH, "i");
            wquPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "u");
            qsiPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QASANDBOX_PATH, "i");
            qsuPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QASANDBOX_PATH, "u");

            st.setSsdPrm(ssdPrm);
            st.setSsiPrm(ssiPrm);
            st.setWqiPrm(wqiPrm);
            st.setWquPrm(wquPrm);
            st.setQsiPrm(qsiPrm);
            st.setQsuPrm(qsuPrm);

            hcSchemas = schemaDao.getSchemas(schemaId);
            if (hcSchemas == null) hcSchemas = new Vector();

            for (int i = 0; i < hcSchemas.size(); i++) {
                HashMap schema = (HashMap) hcSchemas.get(i);
                Schema sc = new Schema();
                sc.setId((String) schema.get("schema_id"));
                sc.setSchema((String) schema.get("xml_schema"));
                sc.setDescription((String) schema.get("description"));
                boolean validate = (!Utils.isNullStr((String) schema.get("validate")) && ((String) schema.get("validate")).equals("1"));
                sc.setDoValidation(validate);

                Vector qascripts = new Vector();
                if (schema.containsKey("queries")) {
                    qascripts = (Vector) schema.get("queries");
                    }

                    List<QAScript> qases = new ArrayList<QAScript>();
                    for (int j = 0; j < qascripts.size(); j++) {
                        HashMap qascript = (HashMap) qascripts.get(j);
                        QAScript qas = new QAScript();
                        qas.setScriptId((String) qascript.get("query_id"));
                        qas.setFileName((String) qascript.get("query"));
                        qas.setDescription((String) qascript.get("description"));
                        qas.setShortName((String) qascript.get("short_name"));
                        qas.setScriptType((String) qascript.get("script_type"));
                        qas.setResultType((String) qascript.get("result_type"));
                        qases.add(qas);

                        //get file last modified only if schemaId is known
                        if(schemaId!=null)
                            if (!Utils.isNullStr(qas.getFileName())) {
                                qas.setFilePath(Names.QUERY_FOLDER + qas.getFileName());
                                String queryFolder=Properties.queriesFolder;
                                if (!queryFolder.endsWith(File.separator))
                                    queryFolder = queryFolder + File.separator;
                                try {
                                    File f = new File(queryFolder + qas.getFileName());
                                    if (f != null && f.exists())
                                        qas.setModified(Utils.getDateTime(new Date(f.lastModified())));
                                } catch (Exception e) {
                            }

                        }

                    }
                    if (qases.size() > 0) {
                        sc.setQascripts(qases);
                    }
                    if (qases.size() > 0 || schemaId!=null || sc.isDoValidation()==true) {
                        schemas.add(sc);
                    }
                }
                if(schemas.size()>0){
                    st.setQascripts(schemas);
                }

        } catch (Exception e) {
            _logger.debug("Error getting schemas with QA scripts",e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return st;

    }


    public void update(String user, String schemaId, String schema, String description, String schemaLang, boolean doValidation, String dtdPublicId, Date expireDate) throws DCMException {

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
            schemaDao.updateSchema(schemaId, schema, description, schemaLang, doValidation, dtdPublicId, expireDate);

        } catch (Exception e) {
            _logger.debug("Error updating schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }


    public SchemaElemHolder getSchemaElems(String user_name, String schemaId) throws DCMException {

        SchemaElemHolder se = new SchemaElemHolder();

        boolean xsduPrm = false;
        boolean xsddPrm = false;
        Schema schema;
        ArrayList elems;

        try {
            elems = new ArrayList();
            xsduPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "u");
            xsddPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "d");

            se.setXsduPrm(xsduPrm);
            se.setXsddPrm(xsddPrm);

            Vector list = schemaDao.getSchemas(schemaId, false);


            if (list == null) list = new Vector();

            if (list.size() > 0) {

                schema = new Schema();

                HashMap schemaHash = (HashMap) list.get(0);
                schema.setSchema((String) schemaHash.get("xml_schema"));
                schema.setDescription((String) schemaHash.get("description"));
                schema.setSchemaLang((String) schemaHash.get("schema_lang"));
                boolean validate = (!Utils.isNullStr((String) schemaHash.get("validate")) && ((String) schemaHash.get("validate")).equals("1"));
                schema.setDoValidation(validate);
                schema.setDtdPublicId((String) schemaHash.get("dtd_public_id"));
                schema.setExpireDate(Utils.parseDate((String) schemaHash.get("expire_date"),"yyyy-MM-dd HH:mm:ss"));


                //get uploaded schema information
                HashMap uplSchemaMap = uplSchemaDao.getUplSchemaByFkSchemaId(schemaId);

                if(uplSchemaMap!=null && uplSchemaMap.get("upl_schema_file")!=null){
                    UplSchema uplSchema = new UplSchema();

                    String uplSchemaFile = (String) uplSchemaMap.get("upl_schema_file");
                    String uplSchemaId = (String) uplSchemaMap.get("upl_schema_id");
                    String uplSchemaFileUrl = Properties.gdemURL + "/schema/" +uplSchemaFile;

                    if(!Utils.isNullStr(uplSchemaFile)){
                        try{
                            File f=new File(Properties.schemaFolder,uplSchemaFile);
                            if (f!=null)
                                uplSchema.setLastModified(Utils.getDateTime(new Date(f.lastModified())));
                        }
                        catch(Exception e){
                        }
                    }
                    uplSchema.setUplSchemaId(uplSchemaId);
                    uplSchema.setUplSchemaFile(uplSchemaFile);
                    uplSchema.setUplSchemaFileUrl(uplSchemaFileUrl);
                    schema.setUplSchema(uplSchema);
                    schema.setUplSchemaFileName(uplSchemaFile);
                }

                se.setSchema(schema);
            }


            Vector root_elems = (Vector) rootElemDao.getSchemaRootElems(schemaId);

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

    /**
     *	Get DD Schemas and append schemas founf from database
     * 	@return
     * 	@throws DCMException
     */
    public ArrayList getSchemas() throws DCMException {

        ArrayList schemas = new ArrayList();
        ArrayList schemasChk = new ArrayList();
        Vector hcSchemas;

        try {

            // retrive conversions for DD tables
            List ddTables = getDDTables();

            for (int i = 0; i < ddTables.size(); i++) {
                Hashtable schema = (Hashtable) ddTables.get(i);
                String tblId = (String) schema.get("tblId");
                String schemaUrl = Properties.ddURL + "/GetSchema?id=TBL" + tblId;

                Schema sc = new Schema();
                sc.setId("TBL" + tblId);
                sc.setSchema(schemaUrl);
                sc.setTable((String) schema.get("shortName"));
                sc.setDataset((String) schema.get("dataSet"));
                Date d = null;
                try{
                    d = Utils.parseDate((String)schema.get("dateReleased"), "ddMMyy");
                }
                catch(Exception e){
                    _logger.error("Unable to parse DataDictionary dataset released date: " + (String)schema.get("dateReleased"),e);
                }
                sc.setDatasetReleased(d);
                schemas.add(sc);
                schemasChk.add(schemaUrl);
            }


            // append handcoded conversions
            hcSchemas = schemaDao.getSchemasWithStl();


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
            BeanComparator comparator = new BeanComparator( "schema" , new NullComparator());
            Collections.sort(schemas, comparator);

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

            ConversionServiceIF cs = new ConversionService();
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

            ArrayList schemaList = uplSchemaDao.getSchemas();


            for (int i = 0; i < schemaList.size(); i++) {
                HashMap hash = (HashMap) schemaList.get(i);
                String schemaId = (String) hash.get("schema_id");
                //String schema = Properties.gdemURL + "/schema/" + (String) hash.get("schema");
                String schema =(String) hash.get("xml_schema");
                String desc = (String) hash.get("description");
                String uplSchemaId = (String) hash.get("upl_schema_id");
                String uplSchemaFile = (String) hash.get("upl_schema_file");
                String uplSchemaFileUrl = Properties.gdemURL + "/schema/" +(String) hash.get("upl_schema_file");

                UplSchema uplSchema = new UplSchema();
                uplSchema.setSchemaId(schemaId);
                uplSchema.setSchemaUrl(schema);
                uplSchema.setDescription(desc);
                uplSchema.setUplSchemaId(uplSchemaId);
                uplSchema.setUplSchemaFile(uplSchemaFile);
                uplSchema.setUplSchemaFileUrl(uplSchemaFileUrl);
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
    public String addSchema(String user, String schemaUrl, String descr, String schemaLang, boolean doValidation) throws DCMException {
        String schemaID = null;
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
            schemaID = schemaDao.getSchemaID(schemaUrl);


            //Schema URL should be unique
            if(!Utils.isNullStr(schemaID)){
                throw new DCMException(BusinessConstants.EXCEPTION_UPLSCHEMA_URL_EXISTS);
            }
            if (schemaID == null) schemaID = schemaDao.addSchema(schemaUrl, descr, schemaLang, doValidation, null);

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error adding uploaded schema",e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return schemaID;
    }


    public void addUplSchema(String user, FormFile file, String fileName, String fkSchemaId) throws DCMException {

        try{
            InputStream fileInputStream = file.getInputStream();
            addUplSchema(user, fileInputStream, fileName, fkSchemaId);
            file.destroy();
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error adding upoaded schema",e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
    }
    public void addUplSchema(String user, InputStream fileInputStream, String fileName, String fkSchemaId) throws DCMException {

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


            //FK Schema ID should be unique
            if(!Utils.isNullStr(fkSchemaId)){
                if (uplSchemaDao.checkUplSchemaFK(fkSchemaId)) {
                    throw new DCMException(BusinessConstants.EXCEPTION_UPLSCHEMA_URL_EXISTS);
                }
            }

            String filepath = new String(Properties.schemaFolder + File.separatorChar + fileName);
            OutputStream w = new FileOutputStream(filepath);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = fileInputStream.read(buffer, 0, 8192)) != -1) {
                w.write(buffer, 0, bytesRead);
            }
            w.close();
            fileInputStream.close();

            uplSchemaDao.addUplSchema(fileName, null, fkSchemaId);

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error adding upoaded schema",e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }


    /**
     *
     * @param user - user name stored in Http session attribute
     * @param schemaId	- schema DB identifier that will be deleted
     * @param delSchema - false=delete only row in T_UPL_SCHEMA (local file);
     * 						true=delete both row in T_SCHEMA and T_UPL_SCHEMA
     * @return	0= nothing deleted; 1 = T_SCHEMA deleted; 2= T_UPL_SCHEMA deleted; 3= T_SCHEMA & T_UPL_SCHEMA deleted
     * @throws DCMException
     */
    public int deleteUplSchema(String user, String schemaId, boolean delSchema) throws DCMException {

        int ret = 0;

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
             HashMap<String,String> uplSchema = uplSchemaDao.getUplSchemaByFkSchemaId(schemaId);

            if(delSchema){
                if (!Utils.isNullVector(schemaDao.getSchemaQueries(schemaId))||
                    !Utils.isNullVector(schemaDao.getSchemaStylesheets(schemaId)))
                    delSchema = false;
            }

            //delete uploaded files and schema if needed
            schemaDao.removeSchema(schemaId, false, false, true, delSchema);

            //delete uplSchema files only if db operation succeeded
            if (uplSchema != null) {

                String schemaFile = (String)uplSchema.get("upl_schema_file");

                if (schemaFile != null) {
                    try {
                        Utils.deleteFile(Properties.schemaFolder + "/" + schemaFile);
                    } catch (Exception e) {
                        _logger.error("Error deleting upoladed schema file",e);
                        throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
                    }
                    ret=ret+2;
                }
            }
        } catch (Exception e) {
            _logger.error("Error deleting uploaded schema",e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        if(delSchema)ret++;
        return ret;

    }

    public ArrayList getDDSchemas() throws DCMException {
        return getDDSchemas(false);
    }
    /**
     * Get Schemas and stylesheets generated from DataDictioanry
     * @param getStylesheets - generate also stylesheet information
     * @return
     * @throws DCMException
     */
    public ArrayList getDDSchemas(boolean getStylesheets) throws DCMException {

        ArrayList schemas = new ArrayList();

        try {
            // retrive conversions for DD tables
            List ddTables = getDDTables();
            for (int i = 0; i < ddTables.size(); i++) {
                Hashtable schema = (Hashtable) ddTables.get(i);
                String tblId = (String) schema.get("tblId");
                String schemaUrl = Properties.ddURL + "/GetSchema?id=TBL" + tblId;
                Schema sc = new Schema();
                sc.setId("TBL" + tblId);
                sc.setSchema(schemaUrl);
                sc.setTable((String) schema.get("shortName"));
                sc.setDataset((String) schema.get("dataSet"));
                Date d = null;
                try{
                    d = Utils.parseDate((String)schema.get("dateReleased"), "ddMMyy");
                }
                catch(Exception e){
                    _logger.error("Unable to parse DataDictionary dataset released date: " + (String)schema.get("dateReleased"),e);
                }
                sc.setDatasetReleased(d);
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
            ComparatorChain comparatorChain = new ComparatorChain( );
            comparatorChain.addComparator( new BeanComparator( "table", new NullComparator() ) );
            comparatorChain.addComparator( new BeanComparator( "dataset", new NullComparator() ) );
            comparatorChain.addComparator( new BeanComparator( "datasetReleased", new NullComparator() ), true );

            Collections.sort(schemas, comparatorChain);
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

            sch = schemaDao.getSchema(schemaId);
            schema = new Schema();
            schema.setId(schemaId);
            schema.setSchema((String) sch.get("xml_schema"));
            schema.setDescription((String) sch.get("description"));
            schema.setDtdPublicId((String) sch.get("dtd_public_id"));
            schema.setSchemaLang((String) sch.get("schema_lang"));
            schema.setExpireDate(Utils.parseDate((String) sch.get("expire_date"),"yyyy-MM-dd HH:mm:ss"));

        } catch (Exception e) {
            _logger.error("Error getting schema",e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return schema;
    }

    public String getSchemaId(String schema) throws DCMException {
        try {
            return schemaDao.getSchemaID(schema);
        } catch (SQLException e) {
            _logger.error("Error getting schema",e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
    }


    public void updateUplSchema(String user, String uplSchemaId, String schemaId, String fileName, FormFile file) throws DCMException {

        try{
            InputStream fileInputStream = file.getInputStream();
            updateUplSchema(user, uplSchemaId, schemaId, fileName, fileInputStream);
            file.destroy();
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error adding upoaded schema",e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
    }
    public void updateUplSchema(String user, String uplSchemaId, String schemaId, String fileName, InputStream fileInputStream) throws DCMException {


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
            //store the uploaded content into schema folder with the given filename
            if(fileInputStream!=null && !Utils.isNullStr(fileName)){

                String filepath = new String(Properties.schemaFolder + File.separatorChar + fileName);
                OutputStream w = new FileOutputStream(filepath);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = fileInputStream.read(buffer, 0, 8192)) != -1) {
                    w.write(buffer, 0, bytesRead);
                }
                w.close();
                fileInputStream.close();
            }

            //  DB update needed
            //uplSchemaDao.updateUplSchema(uplSchemaId, null, fileName, schemaId);


        //} catch (DCMException dcme) {
        //	_logger.error("Error updating uploaded schema", dcme);
        //	throw dcme;
        } catch (Exception e) {
            _logger.error("Error updating uploaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }


    public UplSchema getUplSchemasById(String schemaId) throws DCMException {

        UplSchema uplSchema = new UplSchema();
        try {

            HashMap ht = uplSchemaDao.getUplSchemaByFkSchemaId(schemaId);
            //String schema = Properties.gdemURL + "/schema/" + (String) hash.get("schema");
            String schema =(String) ht.get("xml_schema");
            String desc = (String) ht.get("description");
            String uplSchemaFile = (String) ht.get("upl_schema_file");
            String uplSchemaId = (String) ht.get("upl_schema_id");
            String uplSchemaFileUrl = Properties.gdemURL + "/schema/" +uplSchemaFile;

            uplSchema = new UplSchema();
            uplSchema.setSchemaId(schemaId);
            uplSchema.setSchemaUrl(schema);
            uplSchema.setDescription(desc);
            uplSchema.setUplSchemaId(uplSchemaId);
            uplSchema.setUplSchemaFile(uplSchemaFile);
            uplSchema.setUplSchemaFileUrl(uplSchemaFileUrl);

            if(!Utils.isNullStr(uplSchemaFile)){
                try{
                    File f=new File(Properties.schemaFolder,uplSchemaFile);
                    if (f!=null)
                        uplSchema.setLastModified(Utils.getDateTime(new Date(f.lastModified())));
                }
                catch(Exception e){
                }
            }

        } catch (Exception e) {
            //e.printStackTrace();
            _logger.error("Error getting uploaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return uplSchema;

    }

    /**
     * If the schema is stored in local repository, then this method returns the file name of locally stored schema
     * @param schemaUrl remote schema URL
     * @return filename stored in schemas folder
     * @throws DCMException
     */
    public String getUplSchemaURL(String schemaUrl) throws DCMException {
        String retURL = schemaUrl;
        try {

            HashMap ht = uplSchemaDao.getUplSchemaByUrl(schemaUrl);
            if(ht!=null){
                retURL = (String) ht.get("schema");
            }

        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("Error getting uploaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return retURL;

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
    /*
     * Search XML files from Conent Registry
     */
    public List<CrFileDto> getCRFiles(String schema) throws DCMException {

        List<CrFileDto> files = new ArrayList<CrFileDto>();
        try{
            // retrive conversions for DD tables
            List crXmlFiles = getXmlFilesBySchema(schema);

            if(crXmlFiles!=null){
                for (Object crXmlFile : crXmlFiles) {
                    Hashtable xmlfile = (Hashtable) crXmlFile;
                    String url = (String) xmlfile.get("uri");
                    String lastModified = (String) xmlfile.get("lastModified");

                    CrFileDto cf = new CrFileDto();
                    cf.setUrl(url);
                    cf.setLastModified(lastModified);

                    files.add(cf);
                }
            }
        } catch (Exception e) {
            _logger.error("Error getting XML files from CR for schema",e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return files;
    }

    /**
     * Returns the list of DD tables retreived from xml-rpx request
     * @return
     */
    protected List getDDTables(){
        return DDServiceClient.getDDTables();
    }
    /**
     * Returns the list of xml files retreived from xml-rpx request
     * @return
     * @throws Exception
     */
    protected List getXmlFilesBySchema(String schema) throws Exception{
        if(!GDEMServices.isTestConnection()){
            return CRServiceClient.getXmlFilesBySchema(schema);
        }
        else{
            return CRServiceClient.getMockXmlFilesBySchema(schema);
        }

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
    public String generateUniqueSchemaFilename(String filepart, String ext) throws DCMException{

        String ret = "";
        if(Utils.isNullStr(ext))ext=Schema.getDefaultSchemaLang().toLowerCase();
        StringBuilder uniq = new StringBuilder(filepart);

        try{
            uniq.append(String.valueOf(System.currentTimeMillis()));

            String hash = Utils.md5digest(uniq.toString());

            ret = "schema-".concat(hash).concat(".").concat(ext);

        } catch (Exception e) {
            _logger.error("Error generating unque schema file name",e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return ret;

    }
    public String generateSchemaFilenameByID(String folderName, String schemaID, String ext) throws DCMException{

        if(Utils.isNullStr(ext))ext=Schema.getDefaultSchemaLang().toLowerCase();
        String fileName = "schema-".concat(schemaID).concat(".").concat(ext);
        fileName = MultipartFileUpload.getUniqueFileName(folderName, fileName);

        return fileName;

    }
    /**
     * compares the differences between remote schema and the local copy of it
     * @param schemaUrl
     * @param schemaFile
     * @return if the result is empty string, then the files are identical,
     *  otherwise BusinessConstants with AppReosurce identifier is returned
     * @throws DCMException
     */
    public String diffRemoteSchema(byte[] remoteSchema, String schemaFile) throws DCMException{

        String remoteSchemaHash ="";
        String fileHash = "";
        String result = "";

        // make md5
        try{
            remoteSchemaHash = Utils.digest(remoteSchema,"md5");
        }
        catch(Exception e){
            e.printStackTrace();
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        // make local file md5
        //if there is no local file, then there is nothing to diff
        if(Utils.isNullStr(schemaFile))
            return "";

        File f = new File (Properties.schemaFolder, schemaFile);
        if(!f.exists()){
            return BusinessConstants.WARNING_LOCALFILE_NOTAVAILABLE;
        }
        try{
            fileHash = Utils.digest(f, "md5");
        }
        catch(Exception e){
            e.printStackTrace();
            return BusinessConstants.WARNING_LOCALFILE_NOTAVAILABLE;
        }
        // compare
        result=remoteSchemaHash.equals(fileHash) && remoteSchemaHash.length()>0 ?
                BusinessConstants.WARNING_FILES_IDENTICAL:BusinessConstants.WARNING_FILES_NOTIDENTICAL;

        return result;
    }
    /**
     * Download remote schema from specified URL and return it as byte array
     *
     * @param url
     * @return
     * @throws DCMException
     */
    public byte[] downloadRemoteSchema(String url)throws DCMException{
        // download schema
        byte[] remoteSchema=null;
        try{
            remoteSchema = HttpUtils.downloadRemoteFile(url);
        }
        catch(DCMException dce){
            dce.printStackTrace();
            throw dce;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new DCMException(BusinessConstants.EXCEPTION_SCHEMAOPEN_ERROR);
        }
        return remoteSchema;
    }
    /**
     * Method tries to download the remote XML Schema and store it in the local cache.
     * Method registers the schema in T_UPL_SCHEMA table.
     *
     * @param user
     * @param schemaUrl
     * @param schemaFileName
     * @param schemaId
     * @param uplSchemaId
     * @throws DCMException
     */
    public void storeRemoteSchema(String user, String schemaUrl, String schemaFileName,
            String schemaId, String uplSchemaId) throws DCMException{

        byte[] remoteSchema = downloadRemoteSchema(schemaUrl);
        ByteArrayInputStream in = new ByteArrayInputStream(remoteSchema);
        if(Utils.isNullStr(schemaFileName))
            schemaFileName=generateSchemaFilenameByID(Properties.schemaFolder,schemaId, Utils.extractExtension(schemaUrl,"xsd"));
        if(Utils.isNullStr(uplSchemaId))
            addUplSchema(user, in, schemaFileName, schemaId);
        else
            updateUplSchema(user, uplSchemaId, schemaId, schemaFileName, in);

    }


}
