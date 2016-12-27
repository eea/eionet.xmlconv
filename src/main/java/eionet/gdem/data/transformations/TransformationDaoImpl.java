package eionet.gdem.data.transformations;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 *
 */
@Repository
public class TransformationDaoImpl implements TransformationDao {

    @PersistenceContext
    private EntityManager manager;


    @Override
    public List<Transformation> findAll() {
        String query = "SELECT e FROM Transformation e";
        return manager.createQuery(query).getResultList();
    }

    @Override
    public Transformation insert(Transformation transformation) {
        manager.persist(transformation);
        return transformation;
    }

    @Override
    public Transformation findById(Integer id) {
        return manager.find(Transformation.class, id);
    }

    @Override
    public Transformation update(Transformation transformation) {
        return manager.merge(transformation);
    }

    @Override
    public void delete(Transformation transformation) {
        manager.remove(transformation);
    }
}
