package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryBackupEntry;
import eionet.gdem.jpa.repositories.QueryBackupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("queryBackupServiceImpl")
public class QueryBackupServiceImpl implements QueryBackupService {

    private QueryBackupRepository queryBackupRepository;

    @Autowired
    public QueryBackupServiceImpl(QueryBackupRepository queryBackupRepository) {
        this.queryBackupRepository = queryBackupRepository;
    }

    @Override
    public QueryBackupEntry save(QueryBackupEntry entry) {
        return queryBackupRepository.save(entry);
    }
}
