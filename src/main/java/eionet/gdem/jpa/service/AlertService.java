package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.AlertEntry;

import java.util.List;

public interface AlertService {

    void save(AlertEntry alertEntry);

    List<AlertEntry> findAll();

    void delete(Integer id);
}
