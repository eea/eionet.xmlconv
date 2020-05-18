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
    public List<JobHistoryEntry> getAdditionalInfoOfJob(String jobId){
        List<JobHistoryEntry> entries = repository.findByJobName(jobId);
        for(JobHistoryEntry entry: entries){
            switch (entry.getStatus()) {
                case 0:
                    entry.setFullStatusName("RECEIVED JOB");
                    break;
                case 1:
                    entry.setFullStatusName("DOWNLOADING SOURCE FILE");
                    break;
                case 2:
                    entry.setFullStatusName("PROCESSING JOB");
                    break;
                case 3:
                    entry.setFullStatusName("READY");
                    break;
                case 4:
                    entry.setFullStatusName("FATAL ERROR");
                    break;
                case 5:
                    entry.setFullStatusName("LIGHT ERROR");
                    break;
                case 6:
                    entry.setFullStatusName("JOB NOT FOUND ERROR");
                    break;
                default:
                    entry.setFullStatusName("UNKNOWN STATUS");
            }
        }
        return entries;
    }
}
