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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;




import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.BackupDto;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IBackupDao;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backup manager.
 * @author Enriko Käsper, Tieto Estonia BackupManager
 * @author George Sofianos
 */

public class BackupManager {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(BackupManager.class);

    private IBackupDao backupDao = GDEMServices.getDaoService().getBackupDao();

    /**
     * Backups file
     * @param folderName Folder name
     * @param fileName File name
     * @param id Id
     * @param user user
     */
    public void backupFile(String folderName, String fileName, String id, String user) {

        File origFile = new File(folderName, fileName);
        if (!origFile.exists())
        {
            return; // there's nothing to backup since file does not exist
        }

        long timestamp = System.currentTimeMillis();
        String backupFileName =
            Constants.BACKUP_FILE_PREFIX + id + "_" + timestamp + fileName.substring(fileName.lastIndexOf("."));

        // backup folder is the subfolder
        String backupFolderName = folderName + File.separator + Constants.BACKUP_FOLDER_NAME;
        File backupFolder = new File(backupFolderName);
        if (!backupFolder.exists())
        {
            backupFolder.mkdir(); // create backup folder if it does not exist
        }

        File backupFile = new File(backupFolderName, backupFileName);

        try {
            Utils.copyFile(origFile, backupFile);
            BackupDto backup = new BackupDto();
            backup.setFileName(backupFileName);
            backup.setQueryId(id);
            backup.setUser(user);
            backup.setTimestamp(new Timestamp(timestamp));
            backupDao.addBackup(backup);
        } catch (Exception e) {
            LOGGER.error("Unable to create backupfile - copy original file " + origFile.getPath() + " to " + backupFile.getPath()
                    + ". " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Gets backup list
     * @param objectId Object id
     * @return Backups
     * @throws DCMException If an error occurs.
     */
    public List<BackupDto> getBackups(String objectId) throws DCMException {
        try {
            return backupDao.getBackups(objectId);
        } catch (Exception e) {
            LOGGER.error("Error getting backups for QA script: " + objectId, e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Remove backup files and refereces in database
     *
     * @param nofDays
     *            - number of days to keep
     * @return Number of files purged
     * @throws DCMException If an error occurs.
     */
    public int purgeBackup(int nofDays) throws DCMException {
        int result = 0;

        Calendar purgeDate = Calendar.getInstance();
        purgeDate.add(Calendar.DATE, -nofDays);

        SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        LOGGER.info("Purge backup files created before: " + sf.format(new Date(purgeDate.getTimeInMillis())));

        long purgeDateInMillis = purgeDate.getTimeInMillis();
        Timestamp ts = new Timestamp(purgeDateInMillis);

        File backupFolder = new File(Properties.queriesFolder, Constants.BACKUP_FOLDER_NAME);

        if (backupFolder.exists() && backupFolder.isDirectory()) {
            File[] files = backupFolder.listFiles();
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                // check if it is backup file
                if (!files[i].isFile() || !fileName.startsWith(Constants.BACKUP_FILE_PREFIX)) {
                    continue;
                }
                int start = fileName.indexOf("_", Constants.BACKUP_FILE_PREFIX.length());
                int end = fileName.lastIndexOf(".");
                String fileTimestamp = fileName.substring(start + 1, end);

                long lFileTimestamp = 0;
                try {
                    lFileTimestamp = Long.parseLong(fileTimestamp);
                } catch (ClassCastException e) {
                    continue;
                }
                if (lFileTimestamp < purgeDateInMillis) {
                    try {
                        files[i].delete();
                        result++;
                    } catch (Exception ioe) {
                        LOGGER.error("Unable to delete backup file: " + files[i].getPath(), ioe);
                    }
                }

            }
        }
        LOGGER.info("Number of back files deleted: " + result);
        // remove database rows
        try {
            backupDao.removeBackupsOlderThan(ts);
        } catch (Exception e) {
            // e.printStackTrace();
            LOGGER.error("Error removing backups.", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return result;
    }

}
