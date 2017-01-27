package eionet.gdem.data.projects;

import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@Repository
public class ProjectDaoImpl implements ProjectDao {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Project insert(Project project) {
        manager.persist(project);
        return project;
    }

    public Project findById(Integer id) {
        return manager.find(Project.class, id);
    }

    @Override
    public Project update(Project project) {
        return manager.merge(project);
    }

    @Override
    public void delete(Project project) {
        manager.remove(project);
    }

    @Override
    public List<Project> findAll() {
        String query = "SELECT DISTINCT e FROM Project e LEFT JOIN FETCH e.schemata LEFT JOIN FETCH e.transformations LEFT JOIN FETCH e.scripts";
        Query query1 = manager.createQuery(query, Project.class);
        return query1.getResultList();
    }

}
