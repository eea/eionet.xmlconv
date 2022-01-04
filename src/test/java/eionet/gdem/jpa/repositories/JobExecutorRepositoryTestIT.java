package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.utils.JobExecutorType;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class JobExecutorRepositoryTestIT {

    @Autowired
    private DataSource db;

    @Autowired
    JobExecutorRepository jobExecutorRepository;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_JOB_EXECUTOR_XML);
    }

    @Test
    public void testFindByStatus() {
        List<JobExecutor> jobExecutors = jobExecutorRepository.findByStatus(1);
        assertThat(jobExecutors.size(), is(2));
    }

    @Test
    public void testFindByName() {
        JobExecutor jobExecutor = jobExecutorRepository.findByName("jobExecutor2");
        assertThat(jobExecutor.getJobId(), is(46));
        assertThat(jobExecutor.getStatus(), is(1));
    }

    @Transactional
    @Test
    public void testUpdateStatusAndJobId() {
        jobExecutorRepository.updateJobExecutor(0, 47, JobExecutorType.Light.getId(), null,"jobExecutor3");
        JobExecutor jobExecutor = jobExecutorRepository.findByName("jobExecutor3");
        assertThat(jobExecutor.getStatus(), is(0));
    }
}







































