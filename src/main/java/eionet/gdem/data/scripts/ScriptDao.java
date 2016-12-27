package eionet.gdem.data.scripts;

import java.util.List;

/**
 *
 */
public interface ScriptDao {
    Script insert(Script script);
    Script findById(Integer id);
    Script update(Script script);
    void delete(Script script);
    List<Script> findAll();
}
