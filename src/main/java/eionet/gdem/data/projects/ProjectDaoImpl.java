package eionet.gdem.data.projects;

import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

/**
 *
 */
@Repository
public class ProjectDaoImpl implements ProjectDao {

    @PersistenceContext
    private EntityManager manager;

    /**
     * Returns all projects
     * @return
     */
    @Override
    public List<Project> getProjectList() {
        String query = "SELECT e FROM Project";
        Query query1 = manager.createQuery(query);
        return query1.getResultList();
    }
}
