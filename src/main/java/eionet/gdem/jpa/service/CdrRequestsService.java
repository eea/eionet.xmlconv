package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.CdrRequestEntry;

public interface CdrRequestsService {

    void save(CdrRequestEntry cdrRequestEntry);

    Integer findByUuid(String uuid);
}
