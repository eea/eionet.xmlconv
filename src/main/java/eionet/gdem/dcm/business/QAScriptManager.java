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

package eionet.gdem.dcm.business;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.QAScript;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;


import org.apache.struts.upload.FormFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

/**
 * QA Script manager.
 * @author Enriko Käsper, Tieto Estonia QAScriptManager
 * @author George Sofianos
 */

public class QAScriptManager {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(QAScriptManager.class);
    /** */
    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
    /** */
    private ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();

    /**
     * Returns QAScript object with all the data incl. file content.
     *
     * @param queryId QA script Id.
     * @return QAScript object.
     * @throws DCMException if database operation fails.
     */
    public QAScript getQAScript(String queryId) throws DCMException {
        QAScript qaScript = new QAScript();

        try {
            if (!queryId.equals("")) {
                HashMap scriptData = queryDao.getQueryInfo(queryId);

                if (scriptData == null) {
                    scriptData = new HashMap<String, String>();
                }

                qaScript.setScriptId((String) scriptData.get(QaScriptView.QUERY_ID));
                qaScript.setSchemaId((String) scriptData.get(QaScriptView.SCHEMA_ID));
                qaScript.setSchema((String) scriptData.get(QaScriptView.XML_SCHEMA));
                qaScript.setDescription((String) scriptData.get(QaScriptView.DESCRIPTION));
                qaScript.setShortName((String) scriptData.get(QaScriptView.SHORT_NAME));
                qaScript.setResultType((String) scriptData.get(QaScriptView.CONTENT_TYPE));
                qaScript.setScriptType((String) scriptData.get(QaScriptView.SCRIPT_TYPE));
                qaScript.setFileName((String) scriptData.get(QaScriptView.QUERY));
                qaScript.setUpperLimit((String) scriptData.get(QaScriptView.UPPER_LIMIT));
                qaScript.setUrl((String) scriptData.get(QaScriptView.URL));
                qaScript.setActive((String) scriptData.get(QaScriptView.IS_ACTIVE));

                String queryFolder = Properties.queriesFolder;

                if (!Utils.isNullStr(qaScript.getFileName())) {
                    qaScript.setFilePath(Names.QUERY_FOLDER + qaScript.getFileName());
                    if (!queryFolder.endsWith(File.separator)) {
                        queryFolder = queryFolder + File.separator;
                    }
                    String queryContent = null;
                    if (!qaScript.getScriptType().equals(XQScript.SCRIPT_LANG_FME)) {
                        try {
                            queryContent = Utils.readStrFromFile(queryFolder + qaScript.getFileName());
                        } catch (IOException e) {
                            queryContent = Constants.FILEREAD_EXCEPTION + queryFolder + qaScript.getFileName() + "\n " + e.toString();
                        }
                    }
                    qaScript.setScriptContent(queryContent);
                    String checksum = null;
                    try {
                        checksum = Utils.getChecksumFromFile(queryFolder + qaScript.getFileName());
                    } catch (IOException e) {
                        checksum = "";
                    }
                    qaScript.setChecksum(checksum);
                    try {
                        File f = new File(queryFolder + qaScript.getFileName());
                        if (f != null && f.exists()) {
                            qaScript.setModified(Utils.getDateTime(new Date(f.lastModified())));
                        }
                    } catch (Exception e) {
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error getting QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return qaScript;

    }

    /**
     * Update QA script record in database.
     * @param user logged in user name.
     * @param scriptId QA script Id.
     * @param shortName QA script short name.
     * @param schemaId XML Schema Id.
     * @param resultType QA script execution result type (XML, HTML, ...).
     * @param descr QA script textual description.
     * @param scriptType QA script type (XQUERY, XSL, XGAWK).
     * @param curFileName QA script file name.
     * @param file FormFile uploaded through web interface.
     * @param upperLimit Maximum size of XML to be sent to ad-hoc QA.
     * @param url URL of the QA script file if maintained in web.
     * @throws DCMException if DB or file operation fails.
     */
    public void update(String user, String scriptId, String shortName, String schemaId, String resultType, String descr,
            String scriptType, String curFileName, FormFile file, String upperLimit, String url) throws DCMException {
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "u")) {
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        if (Utils.isNullStr(scriptId) || Utils.isNullStr(schemaId)) {
            LOGGER.error("Cannot update QA script. Script ID or schema ID is empty.");
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            String fileName = file.getFileName().trim();
            // upload file
            if (!Utils.isNullStr(fileName)) {
                if (Utils.isNullStr(curFileName)) {
                    // check if file exists
                    if (fileExists(fileName)) {
                        throw new DCMException(BusinessConstants.EXCEPTION_QASCRIPT_FILE_EXISTS);
                    }
                }
                // create backup of existing file
                BackupManager bum = new BackupManager();
                bum.backupFile(Properties.queriesFolder, curFileName, scriptId, user);

                storeQAScriptFile(file, curFileName);
            }
            queryDao.updateQuery(scriptId, schemaId, shortName, descr, curFileName, resultType, scriptType, upperLimit, url);
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error updating QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Update script properties.
     *
     * @param user logged in user name.
     * @param scriptId QA script Id.
     * @param shortName QA script short name.
     * @param schemaId XML Schema Id.
     * @param resultType QA script execution result type (XML, HTML, ...).
     * @param descr QA script textual description.
     * @param scriptType QA script type (XQUERY, XSL, XGAWK).
     * @param curFileName QA script file name.
     * @param content File content
     * @param updateContent Update content
     * @throws DCMException If an error occurs.
     */
    public void update(String user, String scriptId, String shortName, String schemaId, String resultType, String descr,
            String scriptType, String curFileName, String upperLimit, String url, String content, boolean updateContent)
            throws DCMException {
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "u")) {
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        if (Utils.isNullStr(scriptId) || Utils.isNullStr(schemaId)) {
            LOGGER.error("Cannot update QA script. Script ID or schema ID is empty.");
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            if (!Utils.isNullStr(curFileName) && !Utils.isNullStr(content) && content.indexOf(Constants.FILEREAD_EXCEPTION) == -1
                    && updateContent) {

                // create backup of existing file
                BackupManager bum = new BackupManager();
                bum.backupFile(Properties.queriesFolder, curFileName, scriptId, user);

                Utils.saveStrToFile(Properties.queriesFolder + File.separator + curFileName, content, null);
            }

            // If the script type is 'FME' update the 'fileName'
            if (XQScript.SCRIPT_LANG_FME.equals(scriptType)) {
                curFileName = StringUtils.substringAfterLast(url, "/");
            }

            queryDao.updateQuery(scriptId, schemaId, shortName, descr, curFileName, resultType, scriptType, upperLimit, url);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error updating QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Checks if the script with the given filename exists whether in db or in fs.
     *
     * @param fileName QA scriptfile name.
     * @return true if file exists.
     * @throws SQLException If an error occurs.
     */
    public boolean fileExists(String fileName) throws SQLException {

        if (queryDao.checkQueryFile(fileName)) {
            // file name exists in database
            return true;
        }

        File file = new File(Properties.queriesFolder, fileName);

        if (file == null) {
            return false;
        }

        return file.exists();

    }

    /**
     * Store QA script file into file system.
     *
     * @param file FormFile object uploaded through web interface.
     * @param fileName File name.
     * @throws FileNotFoundException File is not found.
     * @throws IOException file store operations failed.
     */
    public void storeQAScriptFile(FormFile file, String fileName) throws FileNotFoundException, IOException {

        OutputStream output = null;
        InputStream in = file.getInputStream();
        String filepath = new String(Properties.queriesFolder + File.separator + fileName);

        try {
            output = new FileOutputStream(filepath);
            IOUtils.copy(in, output);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(output);
            file.destroy();
        }

    }

    /**
     * Store QA script content into file system.
     *
     * @param user logged in user name.
     * @param scriptId QA script Id.
     * @param fileContent File content
     * @throws FileNotFoundException File not found
     * @throws IOException IO Exception
     * @throws DCMException If an error occurs.
     */
    public void storeQAScriptFromString(String user, String scriptId, String fileContent) throws FileNotFoundException,
            IOException, DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "u")) {
                LOGGER.debug("You don't have permissions to update QA script!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating QA script content", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        QAScript script = getQAScript(scriptId);

        String sep = Properties.queriesFolder.endsWith(File.separator) ? "" : File.separator;
        String fileName = Properties.queriesFolder + sep + script.getFileName();

        // create backup of existing file
        BackupManager bum = new BackupManager();
        bum.backupFile(Properties.queriesFolder, script.getFileName(), scriptId, user);

        Utils.saveStrToFile(fileName, fileContent, null);
    }

    /**
     * Delete the selected QA script from database and file system
     *
     * @param user logged in user name.
     * @param scriptId QA script Id.
     * @throws DCMException If an error occurs.
     */
    public void delete(String user, String scriptId) throws DCMException {
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "d")) {
                LOGGER.debug("You don't have permissions to delete QA script!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_DELETE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error deleting QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        if (Utils.isNullStr(scriptId)) {
            LOGGER.error("Cannot delete QA script. Script ID is empty.");
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            HashMap hash = queryDao.getQueryInfo(scriptId);
            String fileName = (String) hash.get(QaScriptView.QUERY);

            String queriesFolder = Properties.queriesFolder;
            if (!queriesFolder.endsWith(File.separator)) {
                queriesFolder = queriesFolder + File.separator;
            }
            Utils.deleteFile(queriesFolder + fileName);

            queryDao.removeQuery(scriptId);

        } catch (Exception e) {
            LOGGER.error("Error deleting QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
    }

    /**
     * Add a new QA script into the repository
     *
     * @param user logged in user name.
     * @param shortName QA script short name.
     * @param schemaId XML Schema Id.
     * @param resultType QA script execution result type (XML, HTML, ...).
     * @param description QA script textual description.
     * @param scriptType QA script type (XQUERY, XSL, XGAWK).
     * @param schema Schema
     * @param scriptFile QA script file
     * @return Script id
     * @throws DCMException If an error occurs.
     */
    public String add(String user, String shortName, String schemaId, String schema, String resultType, String description,
            String scriptType, FormFile scriptFile, String upperLimit, String url) throws DCMException {

        String scriptId = null;
        // If remote file URL and local file are specified use local file

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "i")) {
                LOGGER.debug("You don't have permissions to insert QA script!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_INSERT);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error deleting QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        boolean useLocalFile = !Utils.isNullStr(scriptFile.getFileName());
        try {
            String fileName = "";
            if (useLocalFile) {
                fileName = scriptFile.getFileName().trim();
            } else {
                fileName = StringUtils.substringAfterLast(url, "/");
            }
            // upload file
            if (!Utils.isNullStr(fileName)) {
                // check if file exists
                if (fileExists(fileName)) {
                    throw new DCMException((useLocalFile ? BusinessConstants.EXCEPTION_QASCRIPT_FILE_EXISTS
                            : BusinessConstants.EXCEPTION_QAREMOTESCRIPT_FILE_EXISTS));
                }
            }
            if (Utils.isNullStr(schemaId) || "0".equals(schemaId)) {
                schemaId = schemaDao.getSchemaID(schema);
                if (Utils.isNullStr(schemaId) || "0".equals(schemaId)) {
                    schemaId = schemaDao.addSchema(schema, null);
                }
            }

            scriptId = queryDao.addQuery(schemaId, shortName, fileName, description, resultType, scriptType, upperLimit, url);
            if (useLocalFile) {
                storeQAScriptFile(scriptFile, fileName);
            } else {
                // If the script type is 'FME' there is no file to download
                if (!XQScript.SCRIPT_LANG_FME.equals(scriptType)) {
                    replaceScriptFromRemoteFile(user, url, fileName);
                }
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error updating QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return scriptId;
    }

    /**
     * Update schema validation and blocker flag.
     *
     * @param user logged in username.
     * @param schemaId XML Schema Id.
     * @param validate XML Schema validation is part of QA.
     * @param blocker return blocker flag in QA if XML Schema validation fails.
     * @throws DCMException if database operation fails.
     */
    public void updateSchemaValidation(String user, String schemaId, boolean validate, boolean blocker) throws DCMException {
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "u")) {
                LOGGER.debug("You don't have permissions to update XML Schema validation!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating schema validation", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            schemaDao.updateSchemaValidate(schemaId, validate, blocker);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error updating XML Schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Method tries to download the remote script and replace the local file. Method updates table.
     *
     * @param user user login name.
     * @param remoteUrl where to download script file.
     * @param fileName QA script file name.
     * @throws DCMException in case of HTTP connection or database errors.
     */
    public void replaceScriptFromRemoteFile(String user, String remoteUrl, String fileName) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "u")) {
                LOGGER.debug("You don't have permissions to update QA script!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        byte[] remoteFile = Utils.downloadRemoteFile(remoteUrl);
        ByteArrayInputStream in = new ByteArrayInputStream(remoteFile);

        updateScript(fileName, in);

    }

    /**
     * Update QA script content from InputStream.
     * @param fileName QA script file name stored in the system.
     * @param fileInputStream new content of the QA script
     * @throws DCMException in case of IO or database error.
     */
    public void updateScript(String fileName, InputStream fileInputStream) throws DCMException {

        try {
            // store the uploaded content into schema folder with the given filename
            if (fileInputStream != null && !Utils.isNullStr(fileName)) {

                OutputStream output = null;
                String filepath = new String(Properties.queriesFolder + File.separatorChar + fileName);

                try {
                    output = new FileOutputStream(filepath);
                    IOUtils.copy(fileInputStream, output);
                } finally {
                    IOUtils.closeQuietly(fileInputStream);
                    IOUtils.closeQuietly(output);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error updating remote script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }
    
    /**
     * Set/Unset "ACTIVE" flag on a specific scriptId 
     * @param user User
     * @param scriptId Script id
     * @param setActive Active flag
     * @throws DCMException If an error occurs.
     */
    public void activateDeactivate (String user, String scriptId, boolean setActive) throws DCMException {
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "u")) {
                LOGGER.debug("You don't have permissions to activate or deactivate QA script!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error setting activation status for QA script.", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        
        if (Utils.isNullStr(scriptId)) {
            LOGGER.error("Cannot set activation status for QA script. Script ID is empty.");
            throw new DCMException(BusinessConstants.EXCEPTION_NO_QASCRIPT_SELECTED);
        }
        
        try {
            if (setActive){
                System.out.println(this.getClass().toString() + " : "+ scriptId);
                queryDao.activateQuery(scriptId);
            }
            else {
                queryDao.deactivateQuery(scriptId);
            }
        } catch (Exception e) {
            LOGGER.error("Error setting activation status for QA script.", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        
        

    }

}
