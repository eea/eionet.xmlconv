package eionet.gdem.data.schemata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class SchemaService {

    private final SchemaDao dao;

    @Autowired
    public SchemaService(SchemaDao dao) {
        this.dao = dao;
    }
}
