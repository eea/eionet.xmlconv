package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryBackupEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryBackupRepository extends JpaRepository<QueryBackupEntry, Integer> {

}
