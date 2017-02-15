package eionet.gdem.data.obligations;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 *
 *
 */
@Repository
public class ObligationDaoImpl implements ObligationDao {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Obligation insert(Obligation obligation) {
        manager.persist(obligation);
        return obligation;
    }

    @Override
    public Obligation findById(Integer id) {
        return manager.find(Obligation.class, id);
    }

    @Override
    public Obligation update(Obligation obligation) {
        return manager.merge(obligation);
    }

    @Override
    public void delete(Obligation obligation) {
        manager.remove(obligation);
    }

    @Override
    public List<Obligation> findAll() {
        Query q = manager.createQuery("SELECT e from Obligation e");
        return q.getResultList();
    }

}
