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

    @Override
    public Project insert(Project project) {
        manager.persist(project);
        return project;
    }

    @Override
    public Project read(Integer id) {
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
    public List<Project> getProjectList() {
        String query = "SELECT e FROM Project e";
        Query query1 = manager.createQuery(query, Project.class);
        return query1.getResultList();
    }

}
