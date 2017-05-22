package eionet.gdem.data.scripts;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 *
 */
@Repository
public class ScriptDaoImpl implements ScriptDao {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Script insert(Script script) {
        manager.persist(script);
        return script;
    }

    @Override
    public Script findById(Integer id) {
        Query q = manager.createQuery("SELECT e FROM Script e LEFT JOIN FETCH e.linkedSchemata WHERE e.id = :id", Script.class);
        q.setParameter("id", id);
        return (Script) q.getSingleResult();
    }

    @Override
    public Script update(Script script) {
        return manager.merge(script);
    }

    @Override
    public void delete(Script script) {
        manager.remove(script);
    }

    @Override
    public List<Script> findAll() {
        String query = "SELECT id FROM Script";
        return manager.createQuery(query).getResultList();
    }

}
