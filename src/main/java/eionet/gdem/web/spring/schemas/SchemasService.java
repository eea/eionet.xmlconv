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

    private SchemasJooqDao schemasDao;

    @Autowired
    public SchemasService(SchemasJooqDao schemasDao) {
        this.schemasDao = schemasDao;
    }

    public String getSchemaUrl(String schemaId) {
        return schemasDao.getSchemaUrl(schemaId);
    }
}
