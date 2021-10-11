package eionet.gdem.services.impl;

import eionet.gdem.jpa.Entities.QueryMetadataHistoryEntry;
import eionet.gdem.jpa.repositories.QueryMetadataHistoryRepository;
import eionet.gdem.paging.*;
import eionet.gdem.services.PaginationService;
import eionet.gdem.jpa.service.QueryMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaginationServiceImpl implements PaginationService {

    @Autowired
    public PaginationServiceImpl() {
    }

    @Autowired
    QueryMetadataHistoryRepository queryMetadataHistoryRepository;

    @Autowired
    QueryMetadataService queryMetadataService;

    @Override
    public Paged<QueryMetadataHistoryEntry> getQueryMetadataHistoryEntries(Integer pageNumber, Integer size, Integer scriptId) {
        List<QueryMetadataHistoryEntry> historyList = queryMetadataHistoryRepository.findByQueryId(Integer.valueOf(scriptId));
        historyList = queryMetadataService.fillQueryMetadataAdditionalInfo(historyList);

        int totalPages = ( (historyList.size() - 1 ) / size ) +1 ;
        int skip = pageNumber > 1 ? (pageNumber - 1) * size : 0;

        List<QueryMetadataHistoryEntry> paged = historyList.stream()
                .skip(skip)
                .limit(size)
                .collect(Collectors.toList());

        return new Paged<>(new Page<>(paged, totalPages), Paging.of(totalPages, pageNumber, size));
    }
}
