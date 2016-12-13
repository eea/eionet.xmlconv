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
    public List<Transformation> getAllTransformations() {
        String query = "SELECT id FROM Transformation";
        return manager.createQuery(query).getResultList();
    }
}
