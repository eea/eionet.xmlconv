package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.SchemaEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchemaRepository extends JpaRepository<SchemaEntry, Integer> {

    SchemaEntry findBySchemaId(Integer id);
}
