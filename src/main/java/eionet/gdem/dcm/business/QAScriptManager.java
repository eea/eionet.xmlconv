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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.upload.FormFile;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.QAScript;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, Tieto Estonia QAScriptManager
 */

public class QAScriptManager {

    /** */
    private static final Log LOGGER = LogFactory.getLog(QAScriptManager.class);
    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
    private ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();

    /**
     * Returns QAScript object with all the data incl. file contebnt
     *
     * @param queryId
     * @return
     * @throws DCMException
     */
    public QAScript getQAScript(String queryId) throws DCMException {
        QAScript qaScript = new QAScript();

        try {
            if (!queryId.equals("")) {
                HashMap scriptData = queryDao.getQueryInfo(queryId);

                if (scriptData == null) {
                    scriptData = new HashMap<String, String>();
                }

                qaScript.setScriptId((String) scriptData.get("query_id"));
                qaScript.setSchemaId((String) scriptData.get("schema_id"));
                qaScript.setSchema((String) scriptData.get("xml_schema"));
                qaScript.setDescription((String) scriptData.get("description"));
                qaScript.setShortName((String) scriptData.get("short_name"));
                qaScript.setResultType((String) scriptData.get("content_type"));
                qaScript.setScriptType((String) scriptData.get("script_type"));
                qaScript.setFileName((String) scriptData.get("query"));
                qaScript.setUpperLimit((String) scriptData.get("upper_limit"));

                String queryFolder = Properties.queriesFolder;

                if (!Utils.isNullStr(qaScript.getFileName())) {
                    qaScript.setFilePath(Names.QUERY_FOLDER + qaScript.getFileName());
                    if (!queryFolder.endsWith(File.separator)) {
                        queryFolder = queryFolder + File.separator;
                    }
                    String queryContent = null;
                    try {
                        queryContent = Utils.readStrFromFile(queryFolder + qaScript.getFileName());
                    } catch (IOException e) {
                        queryContent = Constants.FILEREAD_EXCEPTION + queryFolder + qaScript.getFileName() + "\n " + e.toString();
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
            LOGGER.error("Error getting QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return qaScript;

    }

    public void update(String user, String scriptId, String shortName, String schemaId, String resultType, String descr,
            String scriptType, String curFileName, FormFile file, String upperLimit) throws DCMException {
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
            queryDao.updateQuery(scriptId, schemaId, shortName, descr, curFileName, resultType, scriptType, upperLimit);
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error updating QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Update script properties
     *
     * @param user
     * @param scriptId
     * @param shortName
     * @param schemaId
     * @param resultType
     * @param descr
     * @param scriptType
     * @param curFileName
     * @param content
     * @param updateContent
     * @throws DCMException
     */
    public void update(String user, String scriptId, String shortName, String schemaId, String resultType, String descr,
            String scriptType, String curFileName, String upperLimit, String content, boolean updateContent) throws DCMException {
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
            queryDao.updateQuery(scriptId, schemaId, shortName, descr, curFileName, resultType, scriptType, upperLimit);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error updating QA script", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Checks if the script with the given filename exists whether in db or in fs
     *
     * @param fileName
     * @return
     * @throws SQLException
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
     * Store QA script file into file system
     *
     * @param file
     * @param fileName
     * @throws FileNotFoundException
     * @throws IOException
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
     * Store QA script content into file system
     *
     * @param user
     * @param scriptId
     * @param fileContent
     * @throws FileNotFoundException
     * @throws IOException
     * @throws DCMException
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
     * @param user
     * @param scriptId
     * @throws DCMException
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
            String fileName = (String) hash.get("query");

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
     * @param user
     * @param shortName
     * @param schemaId
     * @param schema
     * @param resultType
     * @param description
     * @param scriptType
     * @param scriptFile
     * @return
     * @throws DCMException
     */
    public String add(String user, String shortName, String schemaId, String schema, String resultType, String description,
            String scriptType, FormFile scriptFile, String upperLimit) throws DCMException {

        String scriptId = null;
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

        try {
            String fileName = scriptFile.getFileName().trim();
            // upload file
            if (!Utils.isNullStr(fileName)) {
                // check if file exists
                if (fileExists(fileName)) {
                    throw new DCMException(BusinessConstants.EXCEPTION_QASCRIPT_FILE_EXISTS);
                }
            }
            if (Utils.isNullStr(schemaId) || "0".equals(schemaId)) {
                schemaId = schemaDao.getSchemaID(schema);
                if (Utils.isNullStr(schemaId) || "0".equals(schemaId)) {
                    schemaId = schemaDao.addSchema(schema, null);
                }
            }

            scriptId = queryDao.addQuery(schemaId, shortName, fileName, description, resultType, scriptType, upperLimit);
            storeQAScriptFile(scriptFile, fileName);
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
     * Update schema validation flag
     *
     * @param user
     * @param schemaId
     * @param validate
     * @throws DCMException
     */
    public void updateSchemaValidation(String user, String schemaId, boolean validate) throws DCMException {
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "u")) {
                LOGGER.debug("You don't have permissions to update XML Schema validationt!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating schema validation", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            schemaDao.updateSchemaValidate(schemaId, validate);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error updating XML Schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

}
