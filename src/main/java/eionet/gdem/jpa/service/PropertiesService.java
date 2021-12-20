package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.PropertiesEntry;
import eionet.gdem.jpa.errors.DatabaseException;

public interface PropertiesService {

    PropertiesEntry findByName(String name);

    Object getValue(String name) throws DatabaseException;
}
