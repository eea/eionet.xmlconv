package eionet.gdem.data.schemata;

import eionet.gdem.data.projects.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service
@Transactional
public class SchemaService {

    private final SchemaDao dao;

    @Autowired
    public SchemaService(SchemaDao dao) {
        this.dao = dao;
    }

    /**
     * Get All Schemata and their dependencies
     * @return Schemata
     */
    public List<Schema> findAll() {
        return dao.findAll();
    }

    public void delete(Integer id) {
        Schema p = dao.findById(id);
        dao.delete(p);
    }

    public Schema insert(Schema p) {
        return dao.insert(p);
    }

    public Schema update(Schema p) {
        return dao.update(p);
    }

}
