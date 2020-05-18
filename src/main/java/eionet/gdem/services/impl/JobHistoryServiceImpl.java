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
       return repository.findByJobName(jobId);
    }
}
