package eionet.gdem.services.impl;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.services.JobHistoryService;
import org.jooq.tools.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.jooq.tools.json.JSONObject;

import java.util.List;

@Service
public class JobHistoryServiceImpl implements JobHistoryService {

    @Qualifier("jobHistoryRepository")
    @Autowired
    JobHistoryRepository repository;

    @Override
    public JSONObject getAdditionalInfoOfJob(String jobId){
        List<JobHistoryEntry> entries = repository.findByJobName(jobId);
        /*create and return json object  which contains the status and when the status was modified for each job history entry that was retrieved above */

        JSONObject fullJsonObject = new JSONObject();
        JSONArray jsonArrayForAllEntries = new JSONArray();
        for (JobHistoryEntry entry: entries ){
            JSONObject jsonObjectForEntry = new JSONObject();
            jsonObjectForEntry.put("status", entry.getStatus());
            jsonObjectForEntry.put("statusDateModified", entry.getDateAdded());
            jsonArrayForAllEntries.add(jsonObjectForEntry);
        }

        fullJsonObject.put("entries", jsonArrayForAllEntries);
        return fullJsonObject;
    }
}
