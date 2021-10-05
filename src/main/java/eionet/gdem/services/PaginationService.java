package eionet.gdem.services;

import eionet.gdem.jpa.Entities.QueryMetadataHistoryEntry;
import eionet.gdem.paging.Paged;

public interface PaginationService {

    Paged<QueryMetadataHistoryEntry> getQueryMetadataHistoryEntries(Integer pageNumber, Integer size, Integer scriptId);
}
