package eionet.gdem.data.schemata;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 *
 */
@Repository
public class SchemaDaoImpl implements SchemaDao {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<Schema> getSchemata() {
        String query = "SELECT id FROM Schema";
        return manager.createQuery(query).getResultList();
    }
}
