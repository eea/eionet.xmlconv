package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.PropertiesEntry;
import eionet.gdem.jpa.errors.DatabaseException;

import java.util.List;

public interface PropertiesService {

    PropertiesEntry findByName(String name);

    Object getValue(String name) throws DatabaseException;

    List<PropertiesEntry> findAll();

    void save(PropertiesEntry propertiesEntry);
}
