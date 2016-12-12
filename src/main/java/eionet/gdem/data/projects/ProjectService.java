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
    public List<Project> getAllProjects() {
        return dao.getProjectList();
    }
}
