package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryBackupEntry;

public interface QueryBackupService {

    QueryBackupEntry save(QueryBackupEntry entry);
}
