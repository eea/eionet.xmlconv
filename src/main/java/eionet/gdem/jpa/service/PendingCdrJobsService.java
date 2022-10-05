package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.PendingCdrJobEntry;

import java.util.List;

public interface PendingCdrJobsService {

    List<PendingCdrJobEntry> getAllPendingEntries();

    void savePendingEntry(PendingCdrJobEntry entry);

    void removePendingEntry(Integer id);
}
