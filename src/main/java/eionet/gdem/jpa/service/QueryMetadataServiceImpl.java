package eionet.gdem.jpa.service;

import eionet.gdem.Constants;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.Entities.QueryMetadataEntry;
import eionet.gdem.jpa.Entities.QueryMetadataHistoryEntry;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.jpa.repositories.QueryMetadataHistoryRepository;
import eionet.gdem.jpa.repositories.QueryMetadataRepository;
import eionet.gdem.paging.*;
import eionet.gdem.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryMetadataServiceImpl implements QueryMetadataService {

    @Autowired
    public QueryMetadataServiceImpl() {
    }

    @Qualifier("queryMetadataRepository")
    @Autowired
    QueryMetadataRepository queryMetadataRepository;

    @Qualifier("queryMetadataHistoryRepository")
    @Autowired
    QueryMetadataHistoryRepository queryMetadataHistoryRepository;

    @Qualifier("jobRepository")
    @Autowired
    JobRepository jobRepository;

    @Autowired
    QueryJpaService queryJpaService;

    @Override
    public void storeScriptInformation(Integer queryID, String scriptFile, String scriptType, Long durationOfJob, Integer jobStatus, Integer jobId, Long fmeJobId){
        //if queryID does not exist in T_QUERY do nothing
        QueryEntry queryEntry = queryJpaService.findByQueryId(queryID);
        if(queryEntry == null){
            return;
        }

        //Store script information
        List<QueryMetadataEntry> queryMetadataList = queryMetadataRepository.findByQueryIdAndMaxVersion(queryID);
        if (Utils.isNullList(queryMetadataList)){
            QueryMetadataEntry queryMetadataEntry = new QueryMetadataEntry(scriptFile,queryID, scriptType, durationOfJob, 1, queryEntry.getMarkedHeavy(), queryEntry.getVersion(), durationOfJob);
            queryMetadataRepository.save(queryMetadataEntry);
            QueryMetadataHistoryEntry queryMetadataHistoryEntry = new QueryMetadataHistoryEntry(scriptFile, queryID, scriptType, durationOfJob , queryEntry.getMarkedHeavy(), jobStatus, queryEntry.getVersion(), new Timestamp(new Date().getTime()), jobId);
            if(fmeJobId != null){
                queryMetadataHistoryEntry.setFmeJobId(fmeJobId);
            }
            queryMetadataHistoryRepository.save(queryMetadataHistoryEntry);
        }
        else{
            //the information regarding the script will be updated
            QueryMetadataEntry oldEntry = queryMetadataList.get(0);

            /*if version of script is different from the version that was stored in the old entry, the average duration will be recalculated from now on
             and a new entry will be stored into QUERY_METADATA table for the new version */
            if(oldEntry.getVersion() != queryEntry.getVersion()){
                QueryMetadataEntry newEntry = new QueryMetadataEntry(scriptFile, queryID, scriptType, durationOfJob, 1 , queryEntry.getMarkedHeavy(), queryEntry.getVersion(), durationOfJob);
                queryMetadataRepository.save(newEntry);
            }
            else{
                //update the old entry
                oldEntry.setNumberOfExecutions(oldEntry.getNumberOfExecutions() + 1);
                oldEntry.setDurationSum(oldEntry.getDurationSum() + durationOfJob);
                //adjust the duration of the entry.
                Long newAverageJobDuration = oldEntry.getDurationSum() / oldEntry.getNumberOfExecutions();
                oldEntry.setAverageDuration(newAverageJobDuration);
                queryMetadataRepository.save(oldEntry);
            }
            QueryMetadataHistoryEntry queryMetadataHistoryEntry = new QueryMetadataHistoryEntry(scriptFile, queryID, scriptType, durationOfJob , queryEntry.getMarkedHeavy(), jobStatus, queryEntry.getVersion(), new Timestamp(new Date().getTime()), jobId);
            if(fmeJobId != null){
                queryMetadataHistoryEntry.setFmeJobId(fmeJobId);
            }
            queryMetadataHistoryRepository.save(queryMetadataHistoryEntry);
        }
    }

    @Override
    public List<QueryMetadataHistoryEntry> fillQueryHistoryMetadataAdditionalInfo(List<QueryMetadataHistoryEntry> historyEntries){
        for (QueryMetadataHistoryEntry entry: historyEntries){
            entry.setDurationFormatted(Utils.createFormatForMs(entry.getDuration()));
            List<String> filenameList = Arrays.asList(entry.getScriptFilename().split("/"));
            if(filenameList.size() > 0){
                entry.setShortFileName(filenameList.get(filenameList.size()-1));
            }
            else{
                entry.setShortFileName(entry.getScriptFilename());
            }

            if(entry.getJobStatus() == Constants.XQ_READY){
                entry.setStatusName("Successful");
            }
            else  if(entry.getJobStatus() == Constants.XQ_FATAL_ERR){
                entry.setStatusName("Failed");
            }
            else{
                entry.setStatusName("Unknown status (" + entry.getJobStatus() + ")");
            }
        }
        return historyEntries;
    }

    @Override
    public List<QueryMetadataEntry> fillQueryMetadataAdditionalInfo(List<QueryMetadataEntry> entries){
        for (QueryMetadataEntry entry: entries){
            entry.setAverageDurationFormatted(Utils.createFormatForMs(entry.getAverageDuration()));
        }
        return entries;
    }

     /*Method to retrieve paginated results for html page*/
    @Override
    public Paged<QueryMetadataHistoryEntry> getQueryMetadataHistoryEntries(Integer pageNumber, Integer size, Integer scriptId) {
        List<QueryMetadataHistoryEntry> historyList = queryMetadataHistoryRepository.findByQueryId(Integer.valueOf(scriptId));
        historyList = fillQueryHistoryMetadataAdditionalInfo(historyList);

        int totalPages = ( (historyList.size() - 1 ) / size ) +1 ;
        int skip = pageNumber > 1 ? (pageNumber - 1) * size : 0;

        List<QueryMetadataHistoryEntry> paged = historyList.stream()
                .skip(skip)
                .limit(size)
                .collect(Collectors.toList());

        return new Paged<>(new Page<>(paged, totalPages), Paging.of(totalPages, pageNumber, size));
    }

    /*Method to retrieve paginated results for html page*/
    @Override
    public Paged<QueryMetadataEntry> getQueryMetadataEntries(Integer pageNumber, Integer size, Integer scriptId) {
        List<QueryMetadataEntry> queryVersionsList = queryMetadataRepository.findByQueryId(Integer.valueOf(scriptId));
        queryVersionsList = fillQueryMetadataAdditionalInfo(queryVersionsList);

        int totalPages = ( (queryVersionsList.size() - 1 ) / size ) +1 ;
        int skip = pageNumber > 1 ? (pageNumber - 1) * size : 0;

        List<QueryMetadataEntry> paged = queryVersionsList.stream()
                .skip(skip)
                .limit(size)
                .collect(Collectors.toList());

        return new Paged<>(new Page<>(paged, totalPages), Paging.of(totalPages, pageNumber, size));
    }

    @Override
    public Integer getCountOfHistoryEntriesByScript(Integer scriptId){
        return queryMetadataHistoryRepository.findNumberOfEntriesByQueryId(scriptId);
    }

    @Override
    public Integer getCountOfEntriesByScript(Integer scriptId){
        return queryMetadataRepository.findNumberOfEntriesByQueryId(scriptId);
    }

    @Override
    public List<QueryMetadataHistoryEntry> findByJobId(Integer jobId) {
        return queryMetadataHistoryRepository.findByJobId(jobId);
    }

    @Override
    public QueryMetadataHistoryEntry saveQueryMetadataHistoryEntry(QueryMetadataHistoryEntry entry) {
        return queryMetadataHistoryRepository.save(entry);
    }

    @Override
    public List<QueryMetadataHistoryEntry> getAllQueryMetadataHistoryEntries(Integer scriptId) {
        return queryMetadataHistoryRepository.findByQueryId(scriptId);
    }

    @Override
    public List<QueryMetadataEntry> getAllQueryMetadataEntries(Integer scriptId) {
        return queryMetadataRepository.findByQueryId(scriptId);
    }
}
