package eionet.gdem.data.scripts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
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


}
