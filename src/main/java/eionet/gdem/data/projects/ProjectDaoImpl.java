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
        String jpql = "SELECT e FROM Project e LEFT JOIN FETCH e.schemata LEFT JOIN FETCH e.transformations LEFT JOIN FETCH e.scripts as s LEFT JOIN FETCH s.linkedSchemata LEFT JOIN FETCH e.obligations WHERE e.id = :id";
        Query q = manager.createQuery(jpql, Project.class);
        q.setParameter("id", id);
        return (Project) q.getSingleResult();
    }

    @Override
    public Project update(Project project) {
        return manager.merge(project);
    }

    @Override
    public void delete(Project project) {
        manager.remove(project);
        manager.flush();
    }

    @Override
    public List<Project> findAll() {
        String query = "SELECT DISTINCT e FROM Project e LEFT JOIN FETCH e.schemata LEFT JOIN FETCH e.transformations LEFT JOIN FETCH e.scripts";
        Query query1 = manager.createQuery(query, Project.class);
        return query1.getResultList();
    }

}
