package eionet.gdem.services.impl;

import eionet.gdem.web.spring.workqueue.JobEntryAndJobHistoryEntriesObject;

public interface JobEntryAndJobHistoryEntriesService {

    JobEntryAndJobHistoryEntriesObject getJobEntryAndJobHistoryEntriesOfJob(String jobId);
}
