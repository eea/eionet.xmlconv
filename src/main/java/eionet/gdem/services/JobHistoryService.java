package eionet.gdem.services;

import org.jooq.tools.json.JSONObject;

public interface JobHistoryService {

    JSONObject getAdditionalInfoOfJob(String jobId);
}
