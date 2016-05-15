package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import eionet.gdem.dto.BackupDto;
import eionet.gdem.services.db.dao.IBackupDao;

/**
 * Backup MySQL Dao class.
 * @author Unknown
 */
@Repository("backupDao")
public class BackupMySqlDao extends MySqlBaseDao implements IBackupDao {

    /** */
    private static final Log LOGGER = LogFactory.getLog(BackupMySqlDao.class);

    private static final String qInsertBackup = "INSERT INTO " + BACKUP_TABLE + " ( " + BACKUP_OBJECT_ID_FLD + ", "
    + BACKUP_FILENAME_FLD + ", " + BACKUP_TIMESTAMP_FLD + ", " + BACKUP_USER_FLD + ") " + " VALUES (?,?,?,?)";
    private static final String qBackup = "SELECT " + BACKUP_ID_FLD + ", " + BACKUP_OBJECT_ID_FLD + ", " + BACKUP_FILENAME_FLD
    + ", " + BACKUP_TIMESTAMP_FLD + ", " + BACKUP_USER_FLD + " FROM " + BACKUP_TABLE;

    private static final String qBackupByObjectId = qBackup + " WHERE " + BACKUP_OBJECT_ID_FLD + "=? ORDER BY "
    + BACKUP_TIMESTAMP_FLD + " DESC";

    private static final String qDeleteBackups = "DELETE FROM " + BACKUP_TABLE + " WHERE " + BACKUP_TIMESTAMP_FLD + "< ?";

    @Override
    public void addBackup(BackupDto backup) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qInsertBackup);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qInsertBackup);
            pstmt.setString(1, backup.getQueryId());
            pstmt.setString(2, backup.getFileName());
            pstmt.setTimestamp(3, backup.getTimestamp());
            pstmt.setString(4, backup.getUser());
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public List<BackupDto> getBackups(String objectID) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BackupDto> result = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qBackupByObjectId);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qBackupByObjectId);
            pstmt.setString(1, objectID);
            rs = pstmt.executeQuery();
            result = new ArrayList<BackupDto>();
            while (rs.next()) {
                BackupDto backup = new BackupDto();
                backup.setBackupId(String.valueOf(rs.getInt(BACKUP_ID_FLD)));
                backup.setQueryId(String.valueOf(rs.getInt(BACKUP_OBJECT_ID_FLD)));
                backup.setFileName(rs.getString(BACKUP_FILENAME_FLD));
                backup.setTimestamp(rs.getTimestamp(BACKUP_TIMESTAMP_FLD));
                backup.setUser(rs.getString(BACKUP_USER_FLD));
                result.add(backup);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return result;
    }

    @Override
    public void removeBackupsOlderThan(Timestamp purgeDate) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qDeleteBackups);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qDeleteBackups);
            pstmt.setTimestamp(1, purgeDate);
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

}
