/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */

package eionet.gdem.qa;

import eionet.gdem.Constants;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static eionet.gdem.web.listeners.JobScheduler.getQuartzHeavyScheduler;
import static eionet.gdem.web.listeners.JobScheduler.getQuartzScheduler;

/**
 * Interrupts all jobs that their duration is longer than corresponding schema's max execution time.
 *
 */
public class InterruptLongRunningJobsTask implements Job {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(InterruptLongRunningJobsTask.class);
    /** Dao for getting job data. */
    private WorkqueueManager jobsManager = new WorkqueueManager();
    private SchemaManager schemaManager = new SchemaManager();


    @Override
    public void execute(JobExecutionContext paramJobExecutionContext) {
        LOGGER.debug("RUN InterruptLongRunningJobsTask.");
        try {
            List<WorkqueueJob> jobs = jobsManager.getRunningJobs();

            if (jobs != null && jobs.size() > 0) {
                for (WorkqueueJob job : jobs) {
                    if (job.getDuration()==0) {
                        continue;
                    }
                    String schemaUrl = findSchemaFromXml(job.getUrl());
                    if (job.getDuration() > schemaManager.getSchemaMaxExecutionTime(schemaUrl)) {
                        JobKey qJob = new JobKey(job.getJobId(), "XQueryJob");
                        try {
                            if (getQuartzScheduler().checkExists(qJob)) {
                                // try to interrupt running job
                                getQuartzScheduler().interrupt(qJob);
                                GDEMServices.getDaoService().getXQJobDao().changeJobStatus(job.getJobId(), Constants.XQ_INTERRUPTED);
                                getJobHistoryRepository().save(new JobHistoryEntry(job.getJobId(), Constants.XQ_INTERRUPTED, new Timestamp(new Date().getTime()), null, null, null, null));
                            }
                            else if (getQuartzHeavyScheduler().checkExists(qJob)) {
                                // try to interrupt running job
                                getQuartzHeavyScheduler().interrupt(qJob);
                                GDEMServices.getDaoService().getXQJobDao().changeJobStatus(job.getJobId(), Constants.XQ_INTERRUPTED);
                                getJobHistoryRepository().save(new JobHistoryEntry(job.getJobId(), Constants.XQ_INTERRUPTED, new Timestamp(new Date().getTime()), null, null, null, null));
                            }
                        } catch (SchedulerException | SQLException e) {
                            LOGGER.info("error trying to interrupt job with id: " + job.getJobId());
                            continue;
                        }
                    }
                }
            }
        } catch (DCMException e) {
            LOGGER.error("Error when running InterruptLongRunningJobsTask: ", e);
        }
    }

    private static JobHistoryRepository getJobHistoryRepository() {
        return (JobHistoryRepository) SpringApplicationContext.getBean("jobHistoryRepository");
    }

    /**
     * Finds schema from XML
     *
     * @param xml XML
     * @return Result
     */
    private String findSchemaFromXml(String xml) {
        InputAnalyser analyser = new InputAnalyser();
        try {
            analyser.parseXML(xml);
            String schemaOrDTD = analyser.getSchemaOrDTD();
            return schemaOrDTD;
        } catch (Exception e) {
            // do nothing - did not find XML Schema
            // handleError(request, response, e);
        }
        return null;
    }
}
