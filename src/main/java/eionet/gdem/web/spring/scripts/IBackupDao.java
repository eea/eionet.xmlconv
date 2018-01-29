package eionet.gdem.web.spring.scripts;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import eionet.gdem.dto.BackupDto;

/**
 * Backup DAO interface.
 * @author Unknown
 * @author George Sofianos
 */
public interface IBackupDao {
    /**
     * Adds backup.
     * @param backup backup
     * @throws SQLException If an error occurs.
     */
    void addBackup(BackupDto backup) throws SQLException;

    /**
     * Gets list of backups
     * @param objectID object id
     * @return Backups list
     * @throws SQLException If an error occurs.
     */
    List<BackupDto> getBackups(String objectID) throws SQLException;

    /**
     * Removes backups older than timestamp
     * @param purgeDate Timestamp
     * @throws SQLException If an error occurs.
     */
    void removeBackupsOlderThan(Timestamp purgeDate) throws SQLException;
}
