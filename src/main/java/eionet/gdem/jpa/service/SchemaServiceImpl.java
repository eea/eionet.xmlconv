package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.SchemaEntry;
import eionet.gdem.jpa.repositories.SchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchemaServiceImpl implements SchemaService {

    private SchemaRepository schemaRepository;

    @Autowired
    public SchemaServiceImpl(SchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }


    @Override
    public SchemaEntry findById(Integer id) {
        return schemaRepository.findBySchemaId(id);
    }
}
