package eionet.gdem.data.projects;

import java.util.List;

/**
 *
 */
public interface ProjectDao {

    Project insert(Project project);
    Project findById(Integer id);
    Project update(Project project);
    void delete(Project project);
    List<Project> findAll();
}
