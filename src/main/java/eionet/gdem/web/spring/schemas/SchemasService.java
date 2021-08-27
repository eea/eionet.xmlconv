package eionet.gdem.web.spring.schemas;

import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 *
 *
 */
@Service
@Transactional
public class SchemasService {

    private SchemasJooqDao schemasDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemasService.class);

    @Autowired
    public SchemasService(SchemasJooqDao schemasDao) {
        this.schemasDao = schemasDao;
    }

    public String getSchemaUrl(String schemaId) {
        return schemasDao.getSchemaUrl(schemaId);
    }

    public void updateSchemaValidation(String schemaId, boolean validation) throws DCMException {

        try {

            this.getSchemaDao().updateSchemaValidate(schemaId,validation);
        } catch (SQLException e) {
            LOGGER.error("Error updating schema", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }


    protected ISchemaDao getSchemaDao(){
        return GDEMServices.getDaoService().getSchemaDao();
    }
}
