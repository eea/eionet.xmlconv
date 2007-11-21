/*
 * Created on 16.11.2007
 */
package eionet.gdem.dcm.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.struts.upload.FormFile;

import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.UplXmlFile;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IUPLXmlFileDao;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.xmlfile.UplXmlFileHolder;

/**
 * Business logic for uploading XML files into XMLCONV repository, 
 * editing file metadata and deleting files.
 * 
 * @author Enriko Käsper (TietoEnator)
 *
 */

public class UplXmlFileManager {

	private static LoggerIF _logger = GDEMServices.getLogger();

	private  IUPLXmlFileDao uplXmlFileDao = GDEMServices.getDaoService().getUPLXmlFileDao();

	/**
	 * Stores the new XML file in server filesystem and adds the metadata into database
	 * 
	 * @param user			User name
	 * @param xmlfile		FormFile uploaded through webform.
	 * @param title			String inserted into webform
	 * @throws DCMException If the database or file storing operation fails 
	 */
	public void addUplXmlFile(String user, FormFile xmlfile, String title) throws DCMException {

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_XMLFILE_PATH, "i")) {
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_XMLFILE_INSERT);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error adding XML file",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		try {
			String fileName = xmlfile.getFileName();

			if (uplXmlFileDao.checkUplXmlFile(fileName)) {				
				throw new DCMException(BusinessConstants.EXCEPTION_XMLFILE_FILE_EXISTS);
			}
			//write XML file into filesystem
			InputStream in = xmlfile.getInputStream();
			String filepath = new String(Properties.xmlfileFolderPath + File.separatorChar + xmlfile.getFileName());
			OutputStream w = new FileOutputStream(filepath);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
				w.write(buffer, 0, bytesRead);
			}
			w.close();
			in.close();
			xmlfile.destroy();

			//store metadata in DB
			uplXmlFileDao.addUplXmlFile(fileName, title);

		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error adding xml file",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}

	/**
	 * Deletes the specified XML file from filesystem and deletes the metadata from database
	 * 
	 * @param user				User name, that is used to check the permission
	 * @param uplXmlFileId		XML file ID
	 * @throws DCMException 	If database or file deleting operation fails 
	 */
	public void deleteUplXmlFile(String user, String uplXmlFileId) throws DCMException {

		boolean hasOtherStuff = false;

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_XMLFILE_PATH, "d")) {
				_logger.debug("You don't have permissions to delete xml files!");
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_XMLFILE_DELETE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error deleting uploaded xnl file", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		try {
			String xmlfilename = uplXmlFileDao.getUplXmlFileName(uplXmlFileId);


			if (xmlfilename != null) {
				//delete XML file from filesystem
				try {
					Utils.deleteFile(Properties.xmlfileFolderPath + "/" + xmlfilename);
				} catch (Exception e) {
					_logger.error("Error deleting uploaded XML file",e);
					throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);

				}
			}
			//delete metadata from DB
			uplXmlFileDao.removeUplXmlFile(uplXmlFileId);

		} catch (Exception e) {
			_logger.error("Error deleting uploaded XML file",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}


	/**
	 * Updates XML file metadata in database.
	 * 
	 * @param user				User name, that is used to check the permission
	 * @param xmlFileId			XML file unique ID
	 * @param title				XML file title
	 * @throws DCMException 	If database or file deleting operation fails 
	 */

	public void updateUplXmlFile(String user, String xmlFileId, String title) throws DCMException {

		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_XMLFILE_PATH, "u")) {
				_logger.debug("You don't have permissions to update xml file!");
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_XMLFILE_UPDATE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error updating uploaded XML file", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		try {
			//update metadata in DB
			uplXmlFileDao.updateUplXmlFile(xmlFileId, title);

		} catch (Exception e) {
			_logger.error("Error updating uploaded XML file", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}
	
	/**
	 * Returns UplXmlFile bean with XML file metada.
	 * 
	 * @param xmlFileId
	 * @return
	 * @throws DCMException
	 */
	public UplXmlFile getUplXmlFileById(String xmlFileId) throws DCMException {

		UplXmlFile xmlfile = new UplXmlFile();
		try {

			Hashtable ht = uplXmlFileDao.getUplXmlFileById(xmlFileId);
			String file_name= (String) ht.get("file_name");
			String title = (String) ht.get("title");

			xmlfile.setTitle(title);
			xmlfile.setId(xmlFileId);
			xmlfile.setFileName(file_name);
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error getting uploaded XML file", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return xmlfile;

	}
	/**
	 * Get all the XML files stored in repository.
	 * 
	 * @param user_name
	 * @return					Vector containing UplXmlFile objects
	 * @throws DCMException
	 */
	public UplXmlFileHolder getUplXmlFiles(String user_name) throws DCMException {

		UplXmlFileHolder xh = new UplXmlFileHolder();
		ArrayList xmlfiles;

		boolean ssiPrm = false;
		boolean ssdPrm = false;
		boolean ssuPrm = false;

		try {

			ssiPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_XMLFILE_PATH, "i");
			ssdPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_XMLFILE_PATH, "d");
			ssuPrm = SecurityUtil.hasPerm(user_name, "/" + Names.ACL_XMLFILE_PATH, "u");

			xh.setSsdPrm(ssdPrm);
			xh.setSsiPrm(ssiPrm);
			xh.setSsuPrm(ssuPrm);
			xh.setXmlfileFolder(Properties.xmlfileFolder);

			xmlfiles = new ArrayList();

			//query DB
			Vector xmlfileVec = uplXmlFileDao.getUplXmlFile();


			// create UplXmlFile objects and add them into Vector
			for (int i = 0; i < xmlfileVec.size(); i++) {
				Hashtable hash = (Hashtable) xmlfileVec.get(i);
				String id = (String) hash.get("id");
				String fileName = (String) hash.get("file_name");
				String title = (String) hash.get("title");

				UplXmlFile uplXmlFile = new UplXmlFile();
				uplXmlFile.setId(id);
				uplXmlFile.setFileName(fileName);
				uplXmlFile.setTitle(title);
				xmlfiles.add(uplXmlFile);
			}
			if (xmlfiles.size() > 0) {
				xh.setXmlfiles(xmlfiles);
			}
		} catch (Exception e) {
			_logger.error("Error getting uploaded XML files",e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return xh;

	}

}
