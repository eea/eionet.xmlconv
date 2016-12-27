package eionet.gdem.data.scripts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service
@Transactional
public class ScriptService {

    private final ScriptDao dao;

    /**
     * DI constructor
     * @param dao Script DAO
     */
    @Autowired
    public ScriptService(ScriptDao dao) {
        this.dao = dao;
    }

    public List<Script> findAll() {
        return dao.findAll();
    }

    public void delete(Integer id) {
        Script sc = dao.findById(id);
        dao.delete(sc);
    }

    public Script insert(Script s) {
        return dao.insert(s);
    }

    public Script update(Script s) {
        return dao.update(s);
    }

}
