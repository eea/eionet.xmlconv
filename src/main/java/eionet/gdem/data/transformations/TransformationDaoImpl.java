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
    public List<Transformation> findALl() {
        String query = "SELECT e FROM Transformation e";
        return manager.createQuery(query).getResultList();
    }

}
