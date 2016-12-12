package eionet.gdem.data.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class ProjectService {

    private final ProjectDao dao;

    /**
     * DI constructor
     * @param dao
     */
    @Autowired
    ProjectService(ProjectDao dao) {
        this.dao = dao;
    }
}
