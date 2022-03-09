package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.SchemaEntry;

public interface SchemaService {

    SchemaEntry findById(Integer id);
}
