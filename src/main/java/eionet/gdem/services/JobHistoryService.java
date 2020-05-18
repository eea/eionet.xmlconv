package eionet.gdem.services;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import org.jooq.tools.json.JSONObject;

import java.util.List;

public interface JobHistoryService {

    List<JobHistoryEntry> getAdditionalInfoOfJob(String jobId);
}
