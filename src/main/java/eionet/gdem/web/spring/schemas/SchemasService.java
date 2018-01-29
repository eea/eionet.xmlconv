package eionet.gdem.web.spring.schemas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 *
 */
@Service
@Transactional
public class SchemasService {

    private SchemasDao schemasDao;

    @Autowired
    public SchemasService(SchemasDao schemasDao) {
        this.schemasDao = schemasDao;
    }

    public String schemaUrl(String schemaId) {
        return schemasDao.schemaUrl(schemaId);
    }
}
