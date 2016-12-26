package eionet.gdem.data.scripts;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 *
 */
@Repository
public class ScriptDaoImpl implements ScriptDao {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<Script> findAll() {
        String query = "SELECT id FROM Script";
        return manager.createQuery(query).getResultList();
    }

}
