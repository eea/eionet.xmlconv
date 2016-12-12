package eionet.gdem.data.project;

import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

/**
 *
 */
@Repository
public class ProjectDaoImpl implements ProjectDao {

    @PersistenceContext
    private final EntityManager manager;

    /**
     * DI constructor
     * @param manager Entity Manager
     */
    public ProjectDaoImpl(EntityManager manager) {
        this.manager = manager;
    }

    /**
     * Returns all projects
     * @return
     */
    public List<Project> getProjectList() {
        manager.getTransaction().begin();
        String query = "SELECT id FROM Project";
        Query query1 = manager.createQuery(query);
        return query1.getResultList();
    }
}
