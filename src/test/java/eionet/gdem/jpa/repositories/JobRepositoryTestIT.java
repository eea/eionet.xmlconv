package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class JobRepositoryTestIT {

    @Autowired
    private DataSource db;

    @Qualifier("jobRepository")
    @Autowired
    JobRepository jobRepository;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QAJOBS_XML);
    }

    @Test
    public void testFindById() {
        JobEntry jobEntry = jobRepository.findById(1).get();
        assertThat(jobEntry.getId(), is(1));
        assertThat(jobEntry.getUrl(), is("http://test.dev/test.xml"));
        assertThat(jobEntry.getFile(), is("test.xq"));
    }

    @Test
    public void testFindByIdNotExists() {
        assertThat(jobRepository.findById(0), is(nullValue()));
    }

    @Test
    public void testGetRetryCounter() {
        assertThat(jobRepository.getRetryCounter(1), is(0));
    }

    @Test(expected = Exception.class)
    public void testSaveNull() {
        jobRepository.save((JobEntry) null);
    }
}
































