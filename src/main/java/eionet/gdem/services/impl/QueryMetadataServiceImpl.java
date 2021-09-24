package eionet.gdem.services.impl;

import eionet.gdem.jpa.Entities.QueryMetadataEntry;
import eionet.gdem.jpa.Entities.QueryMetadataHistoryEntry;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.jpa.repositories.QueryMetadataHistoryRepository;
import eionet.gdem.jpa.repositories.QueryMetadataRepository;
import eionet.gdem.services.QueryMetadataService;
import eionet.gdem.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QueryMetadataServiceImpl implements QueryMetadataService {

    @Autowired
    public QueryMetadataServiceImpl() {
    }

    @Autowired
    QueryMetadataRepository queryMetadataRepository;

    @Autowired
    QueryMetadataHistoryRepository queryMetadataHistoryRepository;

    @Autowired
    JobRepository jobRepository;

    @Override
    public void storeScriptInformation(Integer queryID, String scriptFile, String scriptType, Long durationOfJob, Integer jobStatus){
        //Store script information
        List<QueryMetadataEntry> queryMetadataList = queryMetadataRepository.findByQueryId(queryID);
        if (Utils.isNullList(queryMetadataList)){
            QueryMetadataEntry queryMetadataEntry = new QueryMetadataEntry(scriptFile,queryID, scriptType, durationOfJob, 1, false, 1);
            queryMetadataRepository.save(queryMetadataEntry);
            QueryMetadataHistoryEntry queryMetadataHistoryEntry = new QueryMetadataHistoryEntry(scriptFile, queryID, scriptType, durationOfJob , false, jobStatus, 1);
            queryMetadataHistoryRepository.save(queryMetadataHistoryEntry);
        }
        else{
            //the information regarding the script will be updated
            QueryMetadataEntry oldEntry = queryMetadataList.get(0);
            oldEntry.setNumberOfExecutions(oldEntry.getNumberOfExecutions() + 1);
            //adjust the duration of the entry.
            Long newAverageJobDuration = (oldEntry.getAverageDuration() + durationOfJob) / oldEntry.getNumberOfExecutions();
            oldEntry.setAverageDuration(newAverageJobDuration);
            queryMetadataRepository.save(oldEntry);
            QueryMetadataHistoryEntry queryMetadataHistoryEntry = new QueryMetadataHistoryEntry(scriptFile, queryID, scriptType, durationOfJob , oldEntry.getMarkedHeavy(), jobStatus, oldEntry.getVersion());
            queryMetadataHistoryRepository.save(queryMetadataHistoryEntry);
        }
    }
}
