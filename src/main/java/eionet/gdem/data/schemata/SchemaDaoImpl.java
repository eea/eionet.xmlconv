package eionet.gdem.data.schemata;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 *
 */
@Repository
public class SchemaDaoImpl implements SchemaDao {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Schema insert(Schema schema) {
       manager.persist(schema);
       return schema;
    }

    @Override
    public Schema findById(Integer id) {
        return manager.find(Schema.class, id);
    }

    @Override
    public Schema update(Schema schema) {
        return manager.merge(schema);
    }

    @Override
    public void delete(Schema schema) {
        manager.remove(schema);
    }

    @Override
    public List<Schema> findAll() {
        String query = "SELECT e FROM Schema e";
        Query query1 = manager.createQuery(query, Schema.class);
        return query1.getResultList();
    }

    public List<Schema> findByProjectId(Integer id) {
        Query q = manager.createQuery("SELECT e FROM Schema e WHERE e.project.id = :id", Schema.class);
        q.setParameter("id", id);
        return q.getResultList();
    }

}
