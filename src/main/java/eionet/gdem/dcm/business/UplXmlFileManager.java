/*
 * Created on 16.11.2007
 */
package eionet.gdem.dcm.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import eionet.gdem.Constants;
import eionet.gdem.web.spring.FileUploadWrapper;
import org.apache.commons.io.IOUtils;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.UplXmlFile;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IUPLXmlFileDao;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.xmlfile.UplXmlFileHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Business logic for uploading XML files into XMLCONV repository, editing file metadata and deleting files.
 *
 * @author Enriko Käsper (TietoEnator)
 *
 */

public class UplXmlFileManager {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(UplXmlFileManager.class);

    private IUPLXmlFileDao uplXmlFileDao = GDEMServices.getDaoService().getUPLXmlFileDao();

    /**
     * Stores the new XML file in server filesystem and adds the metadata into database
     *
     * @param user
     *            User name
     * @param xmlfile
     *            FormFile uploaded through webform.
     * @param title
     *            String inserted into webform
     * @throws DCMException
     *             If the database or file storing operation fails
     */
    public void addUplXmlFile(String user, FileUploadWrapper xmlfile, String title) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_XMLFILE_PATH, "i")) {
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_XMLFILE_INSERT);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding XML file", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        try {
            String fileName = xmlfile.getFile().getName();

            if (fileExists(fileName)) {
                throw new DCMException(BusinessConstants.EXCEPTION_XMLFILE_FILE_EXISTS);
            }
            // write XML file into filesystem
            storeXmlFile(xmlfile, fileName);
            // TODO: Fix this
            // xmlfile.destroy();

            // store metadata in DB
            uplXmlFileDao.addUplXmlFile(fileName, title);

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error adding xml file", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Deletes the specified XML file from filesystem and deletes the metadata from database
     *
     * @param user
     *            User name, that is used to check the permission
     * @param uplXmlFileId
     *            XML file ID
     * @throws DCMException
     *             If database or file deleting operation fails
     */
    public void deleteUplXmlFile(String user, String uplXmlFileId) throws DCMException {

        boolean hasOtherStuff = false;

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_XMLFILE_PATH, "d")) {
                LOGGER.debug("You don't have permissions to delete xml files!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_XMLFILE_DELETE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error deleting uploaded xnl file", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            String xmlfilename = uplXmlFileDao.getUplXmlFileName(uplXmlFileId);

            if (xmlfilename != null) {
                // delete XML file from filesystem
                try {
                    Utils.deleteFile(Properties.xmlfileFolder + "/" + xmlfilename);
                } catch (Exception e) {
                    LOGGER.error("Error deleting uploaded XML file", e);
                    throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);

                }
            }
            // delete metadata from DB
            uplXmlFileDao.removeUplXmlFile(uplXmlFileId);

        } catch (Exception e) {
            LOGGER.error("Error deleting uploaded XML file", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Updates XML file metadata in database.
     *
     * @param user
     *            User name, that is used to check the permission
     * @param xmlFileId
     *            XML file unique ID
     * @param title
     *            XML file title
     * @param curFileName Current file name
     * @param file Form file
     * @throws DCMException
     *             If database or file deleting operation fails
     */

    public void updateUplXmlFile(String user, String xmlFileId, String title, String curFileName, FileUploadWrapper file)
            throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_XMLFILE_PATH, "u")) {
                LOGGER.debug("You don't have permissions to update xml file!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_XMLFILE_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating uploaded XML file", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            String fileName = file.getFile().getName().trim();
            // upload file
            if (!Utils.isNullStr(fileName)) {
                if (Utils.isNullStr(curFileName)) {
                    // check if file exists
                    if (fileExists(fileName)) {
                        throw new DCMException(BusinessConstants.EXCEPTION_QASCRIPT_FILE_EXISTS);
                    }
                }
                // write XML file into filesystem
                storeXmlFile(file, curFileName);
                // TODO: Fix this
                // file.destroy();
            }
            // update metadata in DB
            uplXmlFileDao.updateUplXmlFile(xmlFileId, title, curFileName);

        } catch (Exception e) {
            LOGGER.error("Error updating uploaded XML file", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Renames XML File
     * @param user User
     * @param xmlFileId XML file id
     * @param title Title
     * @param curFileName Current file name
     * @param newFileName New file name
     * @throws DCMException If an error occurs.
     */
    public void renameXmlFile(String user, String xmlFileId, String title, String curFileName, String newFileName) throws DCMException {
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_XMLFILE_PATH, "u")) {
                LOGGER.debug("You don't have permissions to update xml file!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_XMLFILE_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error renaming uploaded XML file", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            // check if file exists
            if (fileExists(newFileName)) {
                throw new DCMException("label.uplXmlFile.error.fileExists");
            }
            // rename file
            File originalFile = new File(Properties.xmlfileFolder + "/" + curFileName);
            File newFile = new File(Properties.xmlfileFolder + "/" + newFileName);
            boolean renameSuccess = originalFile.renameTo(newFile);
            if (!renameSuccess) {
                LOGGER.error("Failed to rename file: " + originalFile + " -> " + newFile);
                throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
            }

            // update metadata in DB
            uplXmlFileDao.updateUplXmlFile(xmlFileId, title, newFileName);
        } catch (SQLException e) {
            LOGGER.error("Error updating uploaded XML file", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
    }

    /**
     * Returns UplXmlFile bean with XML file metada.
     *
     * @param xmlFileId XML file id
     * @return Uploaded XML file
     * @throws DCMException If an error occurs.
     */
    public UplXmlFile getUplXmlFileById(String xmlFileId) throws DCMException {

        UplXmlFile xmlfile = new UplXmlFile();
        try {

            Hashtable ht = uplXmlFileDao.getUplXmlFileById(xmlFileId);
            String file_name = (String) ht.get("file_name");
            String title = (String) ht.get("title");
            String lastModified = "";
            if (!Utils.isNullStr(file_name)) {
                File f = new File(Properties.xmlfileFolder + File.separatorChar + file_name);
                if (f != null && f.exists()) {
                    lastModified = Utils.getDateTime(new Date(f.lastModified()));
                }
            }

            xmlfile.setTitle(title);
            xmlfile.setId(xmlFileId);
            xmlfile.setFileName(file_name);
            xmlfile.setLastModified(lastModified);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error getting uploaded XML file", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return xmlfile;

    }

    /**
     * Get all the XML files stored in repository.
     *
     * @param user_name User
     * @return Vector containing UplXmlFile objects
     * @throws DCMException If an error occurs.
     */
    public UplXmlFileHolder getUplXmlFiles(String user_name) throws DCMException {

        UplXmlFileHolder xh = new UplXmlFileHolder();
        ArrayList xmlfiles;

        boolean ssiPrm = false;
        boolean ssdPrm = false;
        boolean ssuPrm = false;

        try {

            ssiPrm = SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_XMLFILE_PATH, "i");
            ssdPrm = SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_XMLFILE_PATH, "d");
            ssuPrm = SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_XMLFILE_PATH, "u");

            xh.setSsdPrm(ssdPrm);
            xh.setSsiPrm(ssiPrm);
            xh.setSsuPrm(ssuPrm);
            xh.setXmlfileFolder(Properties.xmlfileFolder);

            xmlfiles = new ArrayList();

            // query DB
            Vector xmlfileVec = uplXmlFileDao.getUplXmlFile();

            // create UplXmlFile objects and add them into Vector
            for (int i = 0; i < xmlfileVec.size(); i++) {
                Hashtable hash = (Hashtable) xmlfileVec.get(i);
                String id = (String) hash.get("id");
                String fileName = (String) hash.get("file_name");
                String title = (String) hash.get("title");
                String lastModified = "";

                if (!Utils.isNullStr(fileName)) {
                    File f = new File(Properties.xmlfileFolder + File.separatorChar + fileName);
                    if (f != null && f.exists()) {
                        lastModified = Utils.getDateTime(new Date(f.lastModified()));
                    }
                }

                UplXmlFile uplXmlFile = new UplXmlFile();
                uplXmlFile.setId(id);
                uplXmlFile.setFileName(fileName);
                uplXmlFile.setTitle(title);
                uplXmlFile.setLastModified(lastModified);
                xmlfiles.add(uplXmlFile);
            }
            if (xmlfiles.size() > 0) {
                xh.setXmlfiles(xmlfiles);
            }
        } catch (Exception e) {
            LOGGER.error("Error getting uploaded XML files", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return xh;

    }

    /**
     * Checks if the xml file with the given filename exists whether in db or in fs
     *
     * @param fileName File name
     * @return True if file exists
     * @throws SQLException If an error occurs.
     */
    public boolean fileExists(String fileName) throws SQLException {

        if (uplXmlFileDao.checkUplXmlFile(fileName)) {
            // file name exists in database
            return true;
        }

        File file = new File(Properties.xmlfileFolder, fileName);

        if (file == null) {
            return false;
        }

        return file.exists();

    }

    /**
     * Stores the xml file into filesystem
     *
     * @param file Form file
     * @param fileName File name
     * @throws FileNotFoundException File not found
     * @throws IOException IO Exception
     */
    public void storeXmlFile(FileUploadWrapper file, String fileName) throws FileNotFoundException, IOException {

        OutputStream output = null;
        InputStream in = file.getFile().getInputStream();
        String filepath = Properties.xmlfileFolder + File.separator + fileName;

        try {
            output = new FileOutputStream(filepath);
            IOUtils.copy(in, output);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(output);
            // TODO: FIX THIS
            // file.destroy();
        }

    }

}
