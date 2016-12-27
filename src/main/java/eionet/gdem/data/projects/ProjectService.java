package eionet.gdem.data.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service
@Transactional
public class ProjectService {

    private final ProjectDao dao;

    /**
     * DI constructor
     * @param dao Project DAO
     */
    @Autowired
    ProjectService(ProjectDao dao) {
        this.dao = dao;
    }

    /**
     * Get All Projects and their dependencies
     * @return Projects
     */
    public List<Project> findAll() {
        return dao.findAll();
    }

    public void delete(Integer id) {
        Project p = dao.findById(id);
        dao.delete(p);
    }

    public Project insert(Project p) {
        Project pr = dao.insert(p);
        return pr;
    }

    public Project update(Project p) {
        Project pr = dao.update(p);
        return pr;
    }

    public Project findById(Integer id) {
        Project pr = dao.findById(id);
        pr.getSchemata().size();
        pr.getScripts().size();
        pr.getTransformations().size();
        return pr;
    }
}
