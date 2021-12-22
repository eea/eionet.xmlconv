package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.PropertiesEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertiesRepository extends JpaRepository<PropertiesEntry, Integer> {

    PropertiesEntry findByName(String name);

}
