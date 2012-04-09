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

package eionet.gdem.web.listeners;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.ParseException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import eionet.gdem.Properties;
import eionet.gdem.dcm.business.WorkqueueManager;
import eionet.gdem.qa.WQCheckerJob;
import eionet.gdem.qa.WQCleanerJob;
import eionet.gdem.qa.WQExecutor;
import eionet.gdem.web.job.DDTablesCacheUpdater;

/**
 * ContextListener for initialising scheduled jobs with quartz
 *
 * @author Enriko Käsper, TripleDev
 */
@SuppressWarnings("unchecked")
public class JobScheduler implements ServletContextListener {

    /** */
    private static final Log LOGGER = LogFactory.getLog(JobScheduler.class);

    /** */
    private static Scheduler quartzScheduler = null;

    private static final Pair<Integer, JobDetail>[] intervalJobs;

    static {
        intervalJobs =
            new Pair[] {
                Pair.of(new Integer(Properties.wqCheckInterval),
                        newJob(WQCheckerJob.class).withIdentity(WQCheckerJob.class.getSimpleName(),
                                WQCheckerJob.class.getName()).build()),
                                Pair.of(new Integer(Properties.wqCleanInterval),
                                        newJob(WQCleanerJob.class).withIdentity(WQCleanerJob.class.getSimpleName(),
                                                WQCleanerJob.class.getName()).build()),
                                                Pair.of(new Integer(Properties.ddTablesUpdateInterval),
                                                        newJob(DDTablesCacheUpdater.class).withIdentity(DDTablesCacheUpdater.class.getSimpleName(),
                                                                DDTablesCacheUpdater.class.getName()).build()) };
    }

    /**
     *
     * @return
     * @throws SchedulerException
     */
    private static void init() throws SchedulerException {

        SchedulerFactory schedFact = new StdSchedulerFactory();
        quartzScheduler = schedFact.getScheduler();
        quartzScheduler.start();
    }

    /**
     *
     * @param cronExpression
     * @param jobDetails
     * @throws SchedulerException
     * @throws ParseException
     */
    public static synchronized void scheduleCronJob(String cronExpression, JobDetail jobDetails) throws SchedulerException,
    ParseException {

        Trigger trigger =
            newTrigger().withIdentity(jobDetails.getKey().getName(), jobDetails.getKey().getGroup())
            .withSchedule(cronSchedule(cronExpression)).forJob(jobDetails.getKey()).build();

        if (quartzScheduler == null) {
            init();
        }

        quartzScheduler.scheduleJob(jobDetails, trigger);
    }

    /**
     *
     * @param repeatInterval
     * @param jobDetails
     * @throws SchedulerException
     * @throws ParseException
     */
    public static synchronized void scheduleIntervalJob(int repeatInterval, JobDetail jobDetails) throws SchedulerException,
    ParseException {

        SimpleTrigger trigger =
            newTrigger().withIdentity(jobDetails.getKey().getName(), jobDetails.getKey().getGroup()).startNow()
            .withSchedule(simpleSchedule().withIntervalInSeconds(repeatInterval).repeatForever()).build();

        if (quartzScheduler == null) {
            init();
        }

        quartzScheduler.scheduleJob(jobDetails, trigger);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

        if (quartzScheduler != null) {
            try {
                quartzScheduler.shutdown(false);
            } catch (SchedulerException e) {
            }
        }
        WQExecutor.getInstance().shutdown();
    }

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent) {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // schedule interval jobs
        for (Pair<Integer, JobDetail> job : intervalJobs) {

            try {
                scheduleIntervalJob(job.getLeft(), job.getRight());
                LOGGER.debug(job.getRight().getKey().getName() + " scheduled, interval=" + job.getLeft());
            } catch (Exception e) {
                LOGGER.fatal("Error when scheduling " + job.getRight().getKey().getName(), e);
            }
        }
        WorkqueueManager.resetActiveJobs();
    }
}
