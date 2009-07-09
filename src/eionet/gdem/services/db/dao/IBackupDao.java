package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import eionet.gdem.dto.BackupDto;

public interface IBackupDao extends IDbSchema{
	

	  public void addBackup(BackupDto backup) throws SQLException;

	  public List<BackupDto> getBackups(String objectID) throws SQLException;

	  public void removeBackupsOlderThan(Timestamp purgeDate) throws SQLException;
}
