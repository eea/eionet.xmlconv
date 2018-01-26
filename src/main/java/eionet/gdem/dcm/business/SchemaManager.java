package eionet.gdem.dcm.business;

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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
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

import eionet.gdem.Constants;
import eionet.gdem.web.spring.FileUploadWrapper;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.io.IOUtils;

import eionet.gdem.Properties;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.dto.CrFileDto;
import eionet.gdem.dto.DDDatasetTable;
import eionet.gdem.dto.QAScript;
import eionet.gdem.dto.RootElem;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.dto.UplSchema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IRootElemDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.services.db.dao.IUPLSchemaDao;
import eionet.gdem.utils.HttpUtils;
import eionet.gdem.utils.MultipartFileUpload;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.scripts.QAScriptListHolder;
import eionet.gdem.web.spring.schemas.SchemaElemHolder;
import eionet.gdem.web.spring.schemas.UplSchemaHolder;
import eionet.gdem.web.spring.stylesheet.StylesheetListHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * Business logic for managing XML schemas in XMLCONV.
 *
 * @author Enriko Käsper
 */
public class SchemaManager {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaManager.class);
    /** */
    private ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
    /** */
    private IRootElemDao rootElemDao = GDEMServices.getDaoService().getRootElemDao();
    /** */
    private IUPLSchemaDao uplSchemaDao = GDEMServices.getDaoService().getUPLSchemaDao();

    /**
     * Deletes XML Schema and related stylesheets from database.
     * @param user login name
     * @param schemaId XML Schema database ID.
     * @throws DCMException in case of database error occurs.
     */
    public void deleteSchemaStylesheets(String user, String schemaId) throws DCMException {

        boolean hasOtherStuff = false;

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_SCHEMA_PATH, "d")) {
                LOGGER.debug("You don't have permissions to delete schemas!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_DELETE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.debug(e.toString(), e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {

            Vector stylesheets = schemaDao.getSchemaStylesheets(schemaId);

            if (!Utils.isNullVector(schemaDao.getSchemaQueries(schemaId)) || uplSchemaDao.checkUplSchemaFK(schemaId)) {
                hasOtherStuff = true;
            }

            schemaDao.removeSchema(schemaId, false, false, !hasOtherStuff);

            // delete stylesheet files only if db operation succeeded
            if (stylesheets != null) {
                for (int i = 0; i < stylesheets.size(); i++) {
                    HashMap hash = (HashMap) stylesheets.get(i);
                    String xslFile = (String) hash.get("xsl");

                    String xslFolder = Properties.xslFolder;
                    if (!xslFolder.endsWith(File.separator)) {
                        xslFolder = xslFolder + File.separator;
                    }

                    try {
                        Utils.deleteFile(xslFolder + xslFile);
                    } catch (Exception e) {
                        LOGGER.debug("Error deleting file", e);
                        throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.debug("Error removing schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Method creates StylesheetListHolder object, that stores the list of stylesheets both for handcoded and generated. The object
     * stores also user permissions info to manage stylesheets.
     *
     * @param type
     *            if type==null, then show only handcoded stylesheets. If type==generated, then show only generated stylesheets. If
     *            type==both, then show both handcoded and generated stylesheets
     * @return StylesheetListHolder object holding schema stylesheet and user permission information
     * @throws DCMException in case of database error occurs.
     */
    public StylesheetListHolder getSchemas(String type) throws DCMException {

        StylesheetListHolder st = new StylesheetListHolder();
        if (Utils.isNullStr(type)) {
            type = "handcoded";
        }
        Vector hcSchemas;
        List<Schema> schemas;

        try {
            schemas = new ArrayList<Schema>();

            // By default show only handcoded stylesheets
            if (type.equals("handcoded") || type.equals("all")) {
                hcSchemas = schemaDao.getSchemas(null);
                if (hcSchemas == null) {
                    hcSchemas = new Vector();
                }

                for (int i = 0; i < hcSchemas.size(); i++) {
                    HashMap schema = (HashMap) hcSchemas.get(i);
                    Schema sc = new Schema();
                    sc.setId((String) schema.get("schema_id"));
                    sc.setSchema((String) schema.get("xml_schema"));
                    sc.setDescription((String) schema.get("description"));
                    sc.setExpireDate(Utils.parseDate((String) schema.get("expire_date"), "yyyy-MM-dd HH:mm:ss"));

                    Vector stylesheets = new Vector();
                    if (schema.containsKey("stylesheets")) {
                        stylesheets = (Vector) schema.get("stylesheets");
                    }

                    ArrayList<Stylesheet> stls = new ArrayList<Stylesheet>();
                    for (int j = 0; j < stylesheets.size(); j++) {
                        HashMap stylesheet = (HashMap) stylesheets.get(j);
                        Stylesheet stl = new Stylesheet();
                        stl.setConvId((String) stylesheet.get("convert_id"));
                        stl.setType((String) stylesheet.get("content_type_out"));
                        stl.setXsl(Constants.XSL_FOLDER + (String) stylesheet.get("xsl"));
                        stl.setXslFileName((String) stylesheet.get("xsl"));
                        stl.setDescription((String) stylesheet.get("description"));
                        stl.setDdConv(false);
                        stls.add(stl);
                    }
                    if (stls.size() > 0) {
                        sc.setStylesheets(stls);
                        schemas.add(sc);
                    }
                }
                if (schemas.size() > 0) {
                    st.setHandCodedStylesheets(schemas);
                }
            }

            // if type==all, then show both handcoded and generated stylesheets
            // if type==generated, then show only generated from DD stylesheets
            if (type.equals("generated") || type.equals("all")) {
                // retrieve conversions for DD tables
                schemas = getDDSchemas(true);
                st.setDdStylesheets(schemas);
            }

        } catch (Exception e) {
            LOGGER.error("Error getting schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return st;

    }

    /**
     * Get XML Schema and related stylesheets information.
     * @param schema XML Schema URL or database ID.
     * @return StylesheetListHolder object holding schema stylesheet and user permission information
     * @throws DCMException in case of database error occurs.
     */
    public StylesheetListHolder getSchemaStylesheetsList(String schemaId) throws DCMException {
        StylesheetListHolder st = new StylesheetListHolder();

        ArrayList<Schema> schemas;

        try {

            schemas = new ArrayList<Schema>();

            /*String schemaId = schemaDao.getSchemaID(schema);*/
            HashMap<String, String> schemaMap = schemaDao.getSchema(schemaId);
            String schema = schemaMap.get("xml_schema");


            if (schemaId == null) {
                st.setHandcoded(false);
            } else {
                st.setHandcoded(true);
            }

            ConversionServiceIF cs = new ConversionService();
            Vector stylesheets = cs.listConversions(schema);
            ArrayList<Stylesheet> stls = new ArrayList<Stylesheet>();
            Schema sc = new Schema();
            sc.setId(schemaId);
            sc.setSchema(schema);

            for (int i = 0; i < stylesheets.size(); i++) {
                Hashtable hash = (Hashtable) stylesheets.get(i);
                String xsl = (String) hash.get("xsl");
                String type;
                String lastModified = "";
                boolean ddConv = false;
                String xslUrl;

                if (!xsl.startsWith(Properties.gdemURL + "/conversions/")) {

                    File f = new File(Properties.xslFolder + File.separatorChar + xsl);
                    if (f != null && f.exists()) {
                        lastModified = Utils.getDateTime(new Date(f.lastModified()));
                    }
                    // DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(f.lastModified()));
                    xslUrl = Constants.XSL_FOLDER + (String) hash.get("xsl");
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
                stl.setXslFileName(xsl);
                stl.setDescription((String) hash.get("description"));
                stl.setModified(lastModified);
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
            LOGGER.debug("Error getting schema stylesheets", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return st;
    }

    /**
     * Method creates QAScriptListHolder object, that stores the list of qa script The object stores also user permissions info to
     * manage stylesheets.
     *
     * @return QAScriptListHolder object holding QA script and permissions info.
     * @throws DCMException in case of database error.
     */
    public QAScriptListHolder getSchemasWithQAScripts() throws DCMException {
        return getSchemasWithQAScripts(null);
    }

    /**
     * Get XML Schema info with related QA scripts.
     * @param schemaId XML Schema database ID.
     * @return QAScriptListHolder holding XML Schema and QA scripts information.
     * @throws DCMException in case of database error.
     */
    public QAScriptListHolder getSchemasWithQAScripts(String schemaId) throws DCMException {

        QAScriptListHolder st = new QAScriptListHolder();
        Vector hcSchemas;
        List<Schema> schemas;

        try {
            schemas = new ArrayList<Schema>();
            hcSchemas = schemaDao.getSchemas(schemaId);
            if (hcSchemas == null) {
                hcSchemas = new Vector();
            }

            for (int i = 0; i < hcSchemas.size(); i++) {
                HashMap schema = (HashMap) hcSchemas.get(i);
                Schema sc = new Schema();
                sc.setId((String) schema.get("schema_id"));
                sc.setSchema((String) schema.get("xml_schema"));
                sc.setDescription((String) schema.get("description"));
                boolean validate =
                        (!Utils.isNullStr((String) schema.get("validate")) && ((String) schema.get("validate")).equals("1"));
                sc.setDoValidation(validate);
                boolean blocker =
                        (!Utils.isNullStr((String) schema.get("blocker")) && ((String) schema.get("blocker")).equals("1"));
                sc.setBlocker(blocker);

                Vector qascripts = new Vector();
                if (schema.containsKey("queries")) {
                    qascripts = (Vector) schema.get("queries");
                }

                List<QAScript> qases = new ArrayList<QAScript>();
                for (int j = 0; j < qascripts.size(); j++) {
                    HashMap qascript = (HashMap) qascripts.get(j);
                    QAScript qas = new QAScript();
                    qas.setScriptId((String) qascript.get(QaScriptView.QUERY_ID));
                    qas.setFileName((String) qascript.get(QaScriptView.QUERY));
                    qas.setDescription((String) qascript.get(QaScriptView.DESCRIPTION));
                    qas.setShortName((String) qascript.get(QaScriptView.SHORT_NAME));
                    qas.setScriptType((String) qascript.get(QaScriptView.SCRIPT_TYPE));
                    qas.setResultType((String) qascript.get(QaScriptView.RESULT_TYPE));
                    qas.setActive((String) qascript.get(QaScriptView.IS_ACTIVE));
                    qases.add(qas);

                    // get file last modified only if schemaId is known
                    if (schemaId != null) {
                        if (!Utils.isNullStr(qas.getFileName())) {
                            qas.setFilePath(Constants.QUERY_FOLDER + qas.getFileName());
                            String queryFolder = Properties.queriesFolder;
                            if (!queryFolder.endsWith(File.separator)) {
                                queryFolder = queryFolder + File.separator;
                            }
                            try {
                                File f = new File(queryFolder + qas.getFileName());
                                if (f != null && f.exists()) {
                                    qas.setModified(Utils.getDateTime(new Date(f.lastModified())));
                                }
                            } catch (Exception e) {
                                LOGGER.error("Unable to read QA script file last modified time.", e);
                            }

                        }
                    }

                }
                if (qases.size() > 0) {
                    sc.setQascripts(qases);
                }
                if (qases.size() > 0 || schemaId != null || sc.isDoValidation()) {
                    schemas.add(sc);
                }
            }
            if (schemas.size() > 0) {
                st.setQascripts(schemas);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error getting schemas with QA scripts", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return st;

    }

    /**
     * Update XML Schema object in database.
     * @param user user name stored in HTTP session.This for checking user permisssions
     * @param schemaId XML Schema database ID.
     * @param schema XML Schema URL.
     * @param description description
     * @param schemaLang schema language (XSD or DTD).
     * @param doValidation is schema validation part of QA.
     * @param dtdPublicId DTD public ID
     * @param expireDate date when the XML Schema is expired and it should not be part of QA after that date.
     * @param blocker return blocker flag in QA for failed XML Schema validation.
     * @throws DCMException in case of database error.
     */
    public void update(String user, String schemaId, String schema, String description, String schemaLang, boolean doValidation,
            String dtdPublicId, Date expireDate, boolean blocker) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_SCHEMA_PATH, "u")) {
                LOGGER.error("You don't have permissions to delete schemas!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            schemaDao.updateSchema(schemaId, schema, description, schemaLang, doValidation, dtdPublicId, expireDate, blocker);

        } catch (Exception e) {
            LOGGER.error("Error updating schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Get XML schema information from database. The info can be queried by XML Schema URL or by schema database ID.
     * The return object holds also information about the user permissions and Schema root elements.
     * @param userName User login name who is asking the schema information
     * @param schemaId Accepts both type of IDs: XML schema numeric database ID or XML Schema URL
     * @return SchemaElemHolder object holding XML Schema and user rights information
     * @throws DCMException in case of database error occurred.
     */
    public SchemaElemHolder getSchemaElems(String userName, String schemaId) throws DCMException {

        SchemaElemHolder se = new SchemaElemHolder();

        boolean xsduPrm = false;
        boolean xsddPrm = false;
        String schemaDbId = "0";
        Schema schema;
        List<RootElem> elems;

        try {
            elems = new ArrayList<RootElem>();
            xsduPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_SCHEMA_PATH, "u");
            xsddPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_SCHEMA_PATH, "d");

            se.setXsduPrm(xsduPrm);
            se.setXsddPrm(xsddPrm);

            Vector list = schemaDao.getSchemas(schemaId, false);

            if (list == null) {
                list = new Vector();
            }

            if (list.size() > 0) {

                schema = new Schema();

                HashMap schemaHash = (HashMap) list.get(0);
                schemaDbId = (String) schemaHash.get("schema_id");
                schema.setId(schemaDbId);
                schema.setSchema((String) schemaHash.get("xml_schema"));
                schema.setDescription((String) schemaHash.get("description"));
                schema.setSchemaLang((String) schemaHash.get("schema_lang"));
                boolean validate =
                        (!Utils.isNullStr((String) schemaHash.get("validate")) && ((String) schemaHash.get("validate")).equals("1"));
                schema.setDoValidation(validate);
                schema.setDtdPublicId((String) schemaHash.get("dtd_public_id"));
                schema.setExpireDate(Utils.parseDate((String) schemaHash.get("expire_date"), "yyyy-MM-dd HH:mm:ss"));
                boolean blocker =
                        (!Utils.isNullStr((String) schemaHash.get("blocker")) && ((String) schemaHash.get("blocker")).equals("1"));
                schema.setBlocker(blocker);

                // get uploaded schema information
                HashMap uplSchemaMap = uplSchemaDao.getUplSchemaByFkSchemaId(schemaDbId);

                if (uplSchemaMap != null && uplSchemaMap.get("upl_schema_file") != null) {
                    UplSchema uplSchema = new UplSchema();

                    String uplSchemaFile = (String) uplSchemaMap.get("upl_schema_file");
                    String uplSchemaId = (String) uplSchemaMap.get("upl_schema_id");
                    String uplSchemaFileUrl = Properties.gdemURL + "/schema/" + uplSchemaFile;

                    if (!Utils.isNullStr(uplSchemaFile)) {
                        try {
                            File f = new File(Properties.schemaFolder, uplSchemaFile);
                            if (f != null) {
                                uplSchema.setLastModified(Utils.getDateTime(new Date(f.lastModified())));
                            }
                        } catch (Exception e) {
                            LOGGER.error("unable to read schema file last modified time.", e);
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

            Vector rootElems = rootElemDao.getSchemaRootElems(schemaDbId);

            if (rootElems == null) {
                rootElems = new Vector();
            }

            for (int i = 0; i < rootElems.size(); i++) {
                HashMap hash = (HashMap) rootElems.get(i);

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
            LOGGER.error("Error getting root elements", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return se;

    }

    /**
     * Get DD Schemas and append schemas found from database.
     *
     * @return list of XML Schemas
     * @throws DCMException in case of database error.
     */
    public List<Schema> getSchemas() throws DCMException {

        List<Schema> schemas = new ArrayList<Schema>();
        List<String> schemasChk = new ArrayList<String>();
        Vector hcSchemas;

        try {

            // retrive conversions for DD tables
            List<DDDatasetTable> ddTables = getDDTables();

            for (DDDatasetTable ddTable : ddTables) {
                String tblId = ddTable.getTblId();
                String schemaUrl = Properties.ddURL + "/GetSchema?id=TBL" + tblId;

                Schema sc = new Schema();
                sc.setId("TBL" + tblId);
                sc.setSchema(schemaUrl);
                sc.setTable(ddTable.getShortName());
                sc.setDataset(ddTable.getDataSet());
                Date d = null;
                try {
                    d = Utils.parseDate(ddTable.getDateReleased(), "ddMMyy");
                } catch (Exception e) {
                    LOGGER.error("Unable to parse DataDictionary dataset released date: " + ddTable.getDateReleased(), e);
                }
                sc.setDatasetReleased(d);
                schemas.add(sc);
                schemasChk.add(schemaUrl);
            }

            // append handcoded conversions
            hcSchemas = schemaDao.getSchemasWithStl();

            if (hcSchemas != null) {
                for (int i = 0; i < hcSchemas.size(); i++) {
                    HashMap schema = (HashMap) hcSchemas.get(i);
                    if (!schemasChk.contains(schema.get("xml_schema"))) {
                        Schema sc = new Schema();
                        sc.setId((String) schema.get("schema_id"));
                        sc.setSchema((String) schema.get("xml_schema"));
                        sc.setDescription((String) schema.get("description"));
                        schemas.add(sc);
                        schemasChk.add((String) schema.get("xml_schema"));
                    }
                }
            }
            BeanComparator comparator = new BeanComparator("schema", new NullComparator());
            Collections.sort(schemas, comparator);

        } catch (Exception e) {
            LOGGER.debug("Error getting schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        return schemas;

    }

    /**
     * Get stylesheet information for given XML Schema.
     * @param schema XML Schema URL or database ID
     * @return List of styleseeht objects.
     * @throws DCMException in case of database error.
     */

    public ArrayList getSchemaStylesheets(String schema) throws DCMException {

        ArrayList<Stylesheet> stls = new ArrayList<Stylesheet>();

        try {

            ConversionServiceIF cs = new ConversionService();
            Vector stylesheets = cs.listConversions(schema);

            for (int i = 0; i < stylesheets.size(); i++) {
                Hashtable hash = (Hashtable) stylesheets.get(i);
                String convertId = (String) hash.get("convert_id");
                String xslFileName = (String) hash.get("xsl");
                String type = (String) hash.get("result_type");
                String description = (String) hash.get("description");
                boolean ddConv = false;
                String xslUrl;

                if (!xslFileName.startsWith(Properties.gdemURL + "/do/getStylesheet?id=")) {
                    xslUrl = Properties.gdemURL + "/" + Constants.XSL_FOLDER + xslFileName;
                } else {
                    xslUrl = (String) hash.get("xsl");
                    ddConv = true;
                }

                Stylesheet stl = new Stylesheet();
                stl.setConvId(convertId);
                stl.setType(type);
                stl.setXsl(xslUrl);
                stl.setXslFileName(xslFileName);
                stl.setDescription(description);
                stl.setDdConv(ddConv);
                stls.add(stl);
            }

        } catch (Exception e) {
            LOGGER.error("Error getting schema stylesheets", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return stls;
    }

    /**
     * Get all schemas with relations to files, stylesheets and QA scripts and permissions.
     * @param userName user login name.
     * @return UplSchemaHolder object holding schema and permissions information.
     * @throws DCMException in case of database error.
     */
    public UplSchemaHolder getAllSchemas(String userName) throws DCMException {

        UplSchemaHolder sc = new UplSchemaHolder();
        List<Schema> schemas;

        boolean ssiPrm = false;
        boolean ssdPrm = false;
        boolean ssuPrm = false;

        try {

            ssiPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_SCHEMA_PATH, "i");
            ssdPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_SCHEMA_PATH, "d");
            ssuPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_SCHEMA_PATH, "u");

            sc.setSsdPrm(ssdPrm);
            sc.setSsiPrm(ssiPrm);
            sc.setSsuPrm(ssuPrm);

            schemas = schemaDao.getSchemasWithRelations();

            if (schemas.size() > 0) {
                sc.setSchemas(schemas);
            }

        } catch (Exception e) {
            LOGGER.error("Error getting uploaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return sc;

    }

    /**
     * Add new XML Schema into database.
     * @param user login name
     * @param schemaUrl XML Schema URL.
     * @param descr description
     * @param schemaLang Schema language (XSD or DTD)
     * @param doValidation is schema part of QA.
     * @param blocker return blocker flag in QA for failed XML Schema validation.
     * @return the database ID of added XML Schema
     * @throws DCMException in case of database error.
     */
    public String addSchema(String user, String schemaUrl, String descr, String schemaLang, boolean doValidation, boolean blocker)
            throws DCMException {
        String schemaID = null;
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_SCHEMA_PATH, "i")) {
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_INSERT);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding upoaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        try {
            schemaID = schemaDao.getSchemaID(schemaUrl);

            // Schema URL should be unique
            if (!Utils.isNullStr(schemaID)) {
                throw new DCMException(BusinessConstants.EXCEPTION_UPLSCHEMA_URL_EXISTS);
            }
            if (schemaID == null) {
                schemaID = schemaDao.addSchema(schemaUrl, descr, schemaLang, doValidation, null, blocker);
            }

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding uploaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return schemaID;
    }

    /**
     * Store uploaded XML Schema file in the system.
     * @param user login name
     * @param file Struts FormFile object holding XML Schema content
     * @param fileName file name
     * @param fkSchemaId XML Schema object ID.
     * @throws DCMException in case of IO or database errors.
     */
    public void addUplSchema(String user, MultipartFile file, String fileName, String fkSchemaId) throws DCMException {

        try {
            InputStream fileInputStream = file.getInputStream();
            addUplSchema(user, fileInputStream, fileName, fkSchemaId);
            // TODO: Fix this
            // file.destroy();
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding upoaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
    }

    /**
     * Store uploaded XML Schema file in the system.
     * @param user login name
     * @param fileInputStream file content
     * @param fileName file name
     * @param fkSchemaId XML Schema object ID.
     * @throws DCMException in case of IO or database errors.
     */
    public void addUplSchema(String user, InputStream fileInputStream, String fileName, String fkSchemaId) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_SCHEMA_PATH, "i")) {
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_INSERT);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding upoaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        try {

            // FK Schema ID should be unique
            if (!Utils.isNullStr(fkSchemaId)) {
                if (uplSchemaDao.checkUplSchemaFK(fkSchemaId)) {
                    throw new DCMException(BusinessConstants.EXCEPTION_UPLSCHEMA_URL_EXISTS);
                }
            }
            OutputStream output = null;
            String filepath = Properties.schemaFolder + File.separatorChar + fileName;

            try {
                output = new FileOutputStream(filepath);
                IOUtils.copy(fileInputStream, output);
            } finally {
                IOUtils.closeQuietly(fileInputStream);
                IOUtils.closeQuietly(output);
            }

            uplSchemaDao.addUplSchema(fileName, null, fkSchemaId);

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding upoaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     *
     * @param user
     *            - user name stored in Http session attribute
     * @param schemaId
     *            - schema DB identifier that will be deleted
     * @param delSchema
     *            - false=delete only row in T_UPL_SCHEMA (local file); true=delete both row in T_SCHEMA and T_UPL_SCHEMA
     * @return 0= nothing deleted; 1 = T_SCHEMA deleted; 2= T_UPL_SCHEMA deleted; 3= T_SCHEMA and T_UPL_SCHEMA deleted
     * @throws DCMException in case of database error.
     */
    public int deleteUplSchema(String user, String schemaId, boolean delSchema) throws DCMException {

        int ret = 0;

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_SCHEMA_PATH, "d")) {
                LOGGER.debug("You don't have permissions to delete schemas!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_DELETE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error deleting upoaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            HashMap<String, String> uplSchema = uplSchemaDao.getUplSchemaByFkSchemaId(schemaId);

            if (delSchema) {
                if (!Utils.isNullVector(schemaDao.getSchemaQueries(schemaId))
                        || !Utils.isNullVector(schemaDao.getSchemaStylesheets(schemaId))) {
                    delSchema = false;
                }
            }

            // delete uploaded files and schema if needed
            schemaDao.removeSchema(schemaId, false, true, delSchema);

            // delete uplSchema files only if db operation succeeded
            if (uplSchema != null) {

                String schemaFile = uplSchema.get("upl_schema_file");

                if (schemaFile != null) {
                    try {
                        Utils.deleteFile(Properties.schemaFolder + File.separator + schemaFile);
                    } catch (Exception e) {
                        LOGGER.error("Error deleting upoladed schema file", e);
                        throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
                    }
                    ret = ret + 2;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting uploaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        if (delSchema) {
            ret++;
        }
        return ret;

    }

    /**
     * Get Data Dictionary XML Schemas without stylesheet information.
     * @return list of XML Schema objects.
     * @throws DCMException in case of database or DD service error.
     */
    public List getDDSchemas() throws DCMException {
        return getDDSchemas(false);
    }

    /**
     * Get Schemas and stylesheets generated from DataDictionary.
     *
     * @param getStylesheets true, if generate also stylesheet information. Otherwise stylesheet info is not returned.
     * @return list of XML Schema objects.
     * @throws DCMException in case of database or DD service error.
     */
    public List getDDSchemas(boolean getStylesheets) throws DCMException {

        List schemas = new ArrayList();

        try {
            // retrive conversions for DD tables
            List<DDDatasetTable> ddTables = getDDTables();
            for (DDDatasetTable ddTable : ddTables) {
                String tblId = ddTable.getTblId();
                String schemaUrl = Properties.ddURL + "/GetSchema?id=TBL" + tblId;
                Schema sc = new Schema();
                sc.setId("TBL" + tblId);
                sc.setSchema(schemaUrl);
                sc.setTable(ddTable.getShortName());
                sc.setDataset(ddTable.getDataSet());
                Date d = null;
                try {
                    d = Utils.parseDate(ddTable.getDateReleased(), "ddMMyy");
                } catch (Exception e) {
                    LOGGER.error("Unable to parse DataDictionary dataset released date: " + ddTable.getDateReleased(), e);
                }
                sc.setDatasetReleased(d);
                List<ConversionDto> ddStylesheets = Conversion.getConversions();
                ArrayList stls = new ArrayList();

                for (ConversionDto ddConv : ddStylesheets) {
                    String convId = ddConv.getConvId();
                    String xslUrl = Properties.gdemURL + "/do/getStylesheet?id=" + tblId + "&conv=" + convId;

                    Stylesheet stl = new Stylesheet();
                    stl.setType(ddConv.getResultType());
                    stl.setXsl(xslUrl);
                    stl.setDescription(ddConv.getDescription());
                    stl.setDdConv(true);
                    stls.add(stl);
                }
                sc.setStylesheets(stls);
                schemas.add(sc);
            }
            ComparatorChain comparatorChain = new ComparatorChain();
            comparatorChain.addComparator(new BeanComparator("table", new NullComparator()));
            comparatorChain.addComparator(new BeanComparator("dataset", new NullComparator()));
            comparatorChain.addComparator(new BeanComparator("datasetReleased", new NullComparator()), true);

            Collections.sort(schemas, comparatorChain);
        } catch (Exception e) {
            LOGGER.error("Error getting DD schemas", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return schemas;
    }

    /**
     * Get XML schema information by database ID or schema URL.
     * @param schemaId XML Schema database ID or URl.
     * @return Schema object holding XML Schema information
     * @throws DCMException in case of database error.
     */
    public Schema getSchema(String schemaId) throws DCMException {

        HashMap sch = null;
        Schema schema = null;

        try {

            sch = schemaDao.getSchema(schemaId);
            if (sch != null) {
                schema = new Schema();
                schema.setId(schemaId);
                schema.setSchema((String) sch.get("xml_schema"));
                schema.setDescription((String) sch.get("description"));
                schema.setDtdPublicId((String) sch.get("dtd_public_id"));
                schema.setSchemaLang((String) sch.get("schema_lang"));
                schema.setExpireDate(Utils.parseDate((String) sch.get("expire_date"), "yyyy-MM-dd HH:mm:ss"));
                boolean blocker = (!Utils.isNullStr((String) sch.get("blocker")) && ((String) sch.get("blocker")).equals("1"));
                schema.setBlocker(blocker);
            }

        } catch (Exception e) {
            LOGGER.error("Error getting schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return schema;
    }

    /**
     * Get XML Schema database ID by Schema URL.
     * @param schema Schema URL
     * @return numeric database ID
     * @throws DCMException in case of database error occurred.
     */
    public String getSchemaId(String schema) throws DCMException {
        try {
            return schemaDao.getSchemaID(schema);
        } catch (SQLException e) {
            LOGGER.error("Error getting schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
    }

    /**
     * Update uploaded XML Schema metadata or file content.
     * @param user user login name
     * @param uplSchemaId Uploaded XML Schema database ID.
     * @param schemaId XML Schema database ID
     * @param fileName XML Schema file name stored in the system.
     * @param file new content of the XML Schema
     * @throws DCMException in case of IO or database error.
     */
    public void updateUplSchema(String user, String uplSchemaId, String schemaId, String fileName, FileUploadWrapper file)
            throws DCMException {

        try {
            InputStream fileInputStream = file.getFile().getInputStream();
            updateUplSchema(user, uplSchemaId, schemaId, fileName, fileInputStream);
            // TODO: Fix this
            // file.destroy();
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding upoaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
    }

    /**
     * Update uploaded XML Schema metadata or file content.
     * @param user user login name
     * @param uplSchemaId Uploaded XML Schema database ID.
     * @param schemaId XML Schema database ID
     * @param fileName XML Schema file name stored in the system.
     * @param fileInputStream new content of the XML Schema
     * @throws DCMException in case of IO or database error.
     */
    public void updateUplSchema(String user, String uplSchemaId, String schemaId, String fileName, InputStream fileInputStream)
            throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_SCHEMA_PATH, "u")) {
                LOGGER.debug("You don't have permissions to update schemas!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating uploaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            // store the uploaded content into schema folder with the given filename
            if (fileInputStream != null && !Utils.isNullStr(fileName)) {

                OutputStream output = null;
                String filepath = Properties.schemaFolder + File.separatorChar + fileName;

                try {
                    output = new FileOutputStream(filepath);
                    IOUtils.copy(fileInputStream, output);
                } finally {
                    IOUtils.closeQuietly(fileInputStream);
                    IOUtils.closeQuietly(output);
                }
            }

            // DB update needed
            // uplSchemaDao.updateUplSchema(uplSchemaId, null, fileName, schemaId);

            // } catch (DCMException dcme) {
            // _logger.error("Error updating uploaded schema", dcme);
            // throw dcme;
        } catch (Exception e) {
            LOGGER.error("Error updating uploaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Get uploaded XML Schemas by database ID.
     * @param schemaId XML Schema database ID.
     * @return UplSchema object.
     * @throws DCMException in case of database error.
     */
    public UplSchema getUplSchemasById(String schemaId) throws DCMException {

        UplSchema uplSchema = new UplSchema();
        try {

            HashMap ht = uplSchemaDao.getUplSchemaByFkSchemaId(schemaId);
            // String schema = Properties.gdemURL + "/schema/" + (String) hash.get("schema");
            String schema = (String) ht.get("xml_schema");
            String desc = (String) ht.get("description");
            String uplSchemaFile = (String) ht.get("upl_schema_file");
            String uplSchemaId = (String) ht.get("upl_schema_id");
            String uplSchemaFileUrl = Properties.gdemURL + "/schema/" + uplSchemaFile;

            uplSchema = new UplSchema();
            uplSchema.setSchemaId(schemaId);
            uplSchema.setSchemaUrl(schema);
            uplSchema.setDescription(desc);
            uplSchema.setUplSchemaId(uplSchemaId);
            uplSchema.setUplSchemaFile(uplSchemaFile);
            uplSchema.setUplSchemaFileUrl(uplSchemaFileUrl);

            if (!Utils.isNullStr(uplSchemaFile)) {
                try {
                    File f = new File(Properties.schemaFolder, uplSchemaFile);
                    if (f != null) {
                        uplSchema.setLastModified(Utils.getDateTime(new Date(f.lastModified())));
                    }
                } catch (Exception e) {
                    LOGGER.error("Error reading file last modified information", e);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error getting uploaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return uplSchema;

    }

    /**
     * If the schema is stored in local repository, then this method returns the file name of locally stored schema.
     *
     * @param schemaUrl
     *            remote schema URL
     * @return filename stored in schemas folder
     * @throws DCMException in case of database error.
     */
    public String getUplSchemaURL(String schemaUrl) throws DCMException {
        String retURL = schemaUrl;
        try {

            HashMap ht = uplSchemaDao.getUplSchemaByUrl(schemaUrl);
            if (ht != null) {
                retURL = (String) ht.get("schema");
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error getting uploaded schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return retURL;

    }

    /**
     * Returns the list of xml files retrieved from CR sparql client.
     *
     * @param schema URL of XML Schema
     * @return list of CR file objects
     * @throws DCMException in case of CR connection or SPARQL errors.
     */
    public List<CrFileDto> getCRFiles(String schema) throws DCMException {

        if (!GDEMServices.isTestConnection()) {
            return CrServiceSparqlClient.getXmlFilesBySchema(schema);
        } else {
            return CrServiceSparqlClient.getMockXmlFilesBySchema(schema);
        }
    }

    /**
     * Returns the list of DD tables retrieved from xml-rpc request.
     *
     * @return list of DD tables.
     */
    protected List<DDDatasetTable> getDDTables() {
        return DDServiceClient.getDDTables();
    }

    /**
     * Generates the unique filename for uploaded schemas.
     * @param filepart prefix for file name.
     * @param ext file extension
     * @return generated file name
     * @throws DCMException in case of IO error.
     */
    public String generateUniqueSchemaFilename(String filepart, String ext) throws DCMException {

        String ret = "";
        if (Utils.isNullStr(ext)) {
            ext = Schema.getDefaultSchemaLang().toLowerCase();
        }
        StringBuilder uniq = new StringBuilder(filepart);

        try {
            uniq.append(String.valueOf(System.currentTimeMillis()));

            String hash = Utils.md5digest(uniq.toString());

            ret = "schema-".concat(hash).concat(".").concat(ext);

        } catch (Exception e) {
            LOGGER.error("Error generating unque schema file name", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return ret;

    }

    /**
     * Generates unique file name for uploaded schema file by database ID.
     * @param folderName local folder for storing XML Schemas.
     * @param schemaID XML Schema database ID.
     * @param ext file extension.
     * @return generated filename.
     * @throws DCMException in case of IO error.
     */
    public String generateSchemaFilenameByID(String folderName, String schemaID, String ext) throws DCMException {

        if (Utils.isNullStr(ext)) {
            ext = Schema.getDefaultSchemaLang().toLowerCase();
        }
        String fileName = "schema-".concat(schemaID).concat(".").concat(ext);
        fileName = MultipartFileUpload.getUniqueFileName(folderName, fileName);

        return fileName;

    }

    /**
     * Compares the differences between remote schema and the local copy of it.
     *
     * @param remoteSchema byte array of remote XML Schema.
     * @param schemaFile local schema file name.
     * @return if the result is empty string, then the files are identical, otherwise BusinessConstants with AppReosurce identifier
     *         is returned
     * @throws DCMException in case of IO errors.
     */
    public String diffRemoteSchema(byte[] remoteSchema, String schemaFile) throws DCMException {

        String remoteSchemaHash = "";
        String fileHash = "";
        String result = "";

        // make md5
        try {
            remoteSchemaHash = Utils.digest(remoteSchema, "md5");
        } catch (Exception e) {
            e.printStackTrace();
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        // make local file md5
        // if there is no local file, then there is nothing to diff
        if (Utils.isNullStr(schemaFile)) {
            return "";
        }

        File f = new File(Properties.schemaFolder, schemaFile);
        if (!f.exists()) {
            return BusinessConstants.WARNING_LOCALFILE_NOTAVAILABLE;
        }
        try {
            fileHash = Utils.digest(f, "md5");
        } catch (Exception e) {
            e.printStackTrace();
            return BusinessConstants.WARNING_LOCALFILE_NOTAVAILABLE;
        }
        // compare
        result =
                remoteSchemaHash.equals(fileHash) && remoteSchemaHash.length() > 0 ? BusinessConstants.WARNING_FILES_IDENTICAL
                        : BusinessConstants.WARNING_FILES_NOTIDENTICAL;

        return result;
    }

    /**
     * Download remote schema from specified URL and return it as byte array.
     *
     * @param url URL of remote XML Schema.
     * @return byte array of remote schema.
     * @throws DCMException in case of connection error.
     */
    public byte[] downloadRemoteSchema(String url) throws DCMException {
        // download schema
        byte[] remoteSchema = null;
        try {
            remoteSchema = HttpUtils.downloadRemoteFile(url);
        } catch (DCMException dce) {
            dce.printStackTrace();
            throw dce;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DCMException(BusinessConstants.EXCEPTION_SCHEMAOPEN_ERROR);
        }
        return remoteSchema;
    }

    /**
     * Method tries to download the remote XML Schema and store it in the local cache. Method registers the schema in T_UPL_SCHEMA
     * table.
     *
     * @param user user login name
     * @param schemaUrl XML Schema URL
     * @param schemaFileName file name of remote schema
     * @param schemaId XML Schema database ID.
     * @param uplSchemaId uploaded schema file ID.
     * @throws DCMException in case of HTTP connection or database errors.
     */
    public void storeRemoteSchema(String user, String schemaUrl, String schemaFileName, String schemaId, String uplSchemaId)
            throws DCMException {

        byte[] remoteSchema = downloadRemoteSchema(schemaUrl);
        ByteArrayInputStream in = new ByteArrayInputStream(remoteSchema);
        if (Utils.isNullStr(schemaFileName)) {
            schemaFileName =
                    generateSchemaFilenameByID(Properties.schemaFolder, schemaId, Utils.extractExtension(schemaUrl, "xsd"));
        }
        if (Utils.isNullStr(uplSchemaId)) {
            addUplSchema(user, in, schemaFileName, schemaId);
        } else {
            updateUplSchema(user, uplSchemaId, schemaId, schemaFileName, in);
        }

    }

}
