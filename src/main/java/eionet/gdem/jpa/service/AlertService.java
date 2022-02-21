package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.AlertEntry;

public interface AlertService {

    void save(AlertEntry alertEntry);
}
