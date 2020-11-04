package eionet.gdem.web.spring.workqueue;

import eionet.gdem.Constants;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.services.GDEMServices;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import static eionet.gdem.web.listeners.JobScheduler.getQuartzHeavyScheduler;
import static eionet.gdem.web.listeners.JobScheduler.getQuartzScheduler;

@Controller
@RequestMapping("/testInterrupt")
public class JobInterruptController {
    @GetMapping
    public void test(@RequestParam("jobId") String jobId) throws SchedulerException, SQLException {
        JobKey qJob = new JobKey(jobId, "XQueryJob");
        if (getQuartzScheduler().checkExists(qJob)) {
            getQuartzScheduler().interrupt(qJob);
        }
        else if (getQuartzHeavyScheduler().checkExists(qJob)) {
            getQuartzScheduler().interrupt(qJob);
        }
    }

    private JobHistoryRepository getJobHistoryRepository() {
        return (JobHistoryRepository) SpringApplicationContext.getBean("jobHistoryRepository");
    }
}
