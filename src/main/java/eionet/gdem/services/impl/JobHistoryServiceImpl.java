package eionet.gdem.services.impl;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.services.JobHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("jobHistoryService")
public class JobHistoryServiceImpl implements JobHistoryService {

    @Qualifier("jobHistoryRepository")
    @Autowired
    JobHistoryRepository repository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobHistoryServiceImpl.class);

    @Override
    public List<JobHistoryEntry> getJobHistoryEntriesOfJob(String jobId){
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
                    //if status is in processing and duration field was filled show the duration in hours and minutes
                    String row = "PROCESSING JOB";
                    if(entry.getDuration() != null){
                        row += String.format(" for %d hours, %02d minutes, %02d seconds",
                                TimeUnit.MILLISECONDS.toHours(entry.getDuration()),
                                TimeUnit.MILLISECONDS.toMinutes(entry.getDuration()) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(entry.getDuration())),
                                TimeUnit.MILLISECONDS.toSeconds(entry.getDuration()) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(entry.getDuration()))
                        );
                    }
                    entry.setFullStatusName(row);
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
                case 7:
                    entry.setFullStatusName("JOB INTERRUPTED");
                    break;
                case 8:
                    entry.setFullStatusName("DELETED");
                    break;
                case 9:
                    entry.setFullStatusName("CANCELLED BY USER");
                    break;
                default:
                    entry.setFullStatusName("UNKNOWN STATUS");
            }
            if (entry.getJobExecutorName()==null) {
                entry.setJobExecutorName("Not defined yet");
            }
        }
        return entries;
    }

    @Override
    public void updateJobHistory(JobEntry jobEntry) throws DatabaseException {
        try {
            JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(jobEntry.getId().toString(), jobEntry.getnStatus(), new Timestamp(new Date().getTime()), jobEntry.getUrl(), jobEntry.getFile(), jobEntry.getResultFile(), jobEntry.getScriptType());
            jobHistoryEntry.setIntSchedulingStatus(jobEntry.getIntSchedulingStatus().getId()).setJobExecutorName(jobEntry.getJobExecutorName()).setDuration(jobEntry.getDuration()!=null ? jobEntry.getDuration().longValue() : null).setJobType(jobEntry.getJobType())
                    .setWorkerRetries(jobEntry.getWorkerRetries()).setHeavy(jobEntry.isHeavy()).setHeavyRetriesOnFailure(jobEntry.getHeavyRetriesOnFailure());
            jobHistoryEntry.setDuplicateIdentifier(jobEntry.getDuplicateIdentifier());
            jobHistoryEntry.setXmlSize(jobEntry.getXmlSize());
            if(jobEntry.getFmeJobId() != null){
                jobHistoryEntry.setFmeJobId(jobEntry.getFmeJobId());
            }
            repository.save(jobHistoryEntry);
            LOGGER.info("Job with id=" + jobEntry.getId() + " has been inserted in table JOB_HISTORY ");
        } catch (Exception e) {
            LOGGER.error("Database exception while saving history of job with id " + jobEntry.getId() + ", " + e.toString());
            throw new DatabaseException(e);
        }
    }

    @Override
    public JobHistoryEntry save(JobHistoryEntry jobHistoryEntry) throws DatabaseException {
        JobHistoryEntry result;
        try {
            result = repository.save(jobHistoryEntry);
        } catch (Exception e) {
            LOGGER.error("Database exception while saving job history entry for job with id " + jobHistoryEntry.getJobName());
            throw new DatabaseException(e);
        }
        return result;
    }
}









