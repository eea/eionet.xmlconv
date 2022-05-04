package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class JobHistoryRepositoryTestIT {
    @Autowired
    private DataSource db;

    @Qualifier("jobHistoryRepository")
    @Autowired
    private JobHistoryRepository repository;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_JOB_HISTORY_XML);
    }

    @Test
    public void findByIdTestIdDoesntExist() {
        MatcherAssert.assertThat(repository.findById(0), is(nullValue()));
    }

    @Test
    public void findByIdTestIdExists(){
        JobHistoryEntry entry = repository.findById(2);
        MatcherAssert.assertThat(entry.getId(), is(2));
        MatcherAssert.assertThat(entry.getJobName(), is("job2"));
        MatcherAssert.assertThat(entry.getStatus(), is(1));
        MatcherAssert.assertThat(entry.getDateAdded().toString(), is("2017-07-23 13:10:11.0"));
        MatcherAssert.assertThat(entry.getUrl(), is(nullValue()));
        MatcherAssert.assertThat(entry.getXqFile(), is(nullValue()));
        MatcherAssert.assertThat(entry.getResultFile(), is(nullValue()));
        MatcherAssert.assertThat(entry.getXqType(), is(nullValue()));
    }

    @Test(expected = Exception.class)
    public void saveTestNull()  {
        repository.save((JobHistoryEntry) null);
    }

    @Test(expected = Exception.class)
    public void saveTestNullMandatoryField() {
        repository.save(new JobHistoryEntry(7, null, 1, new Timestamp(new Date().getTime()),null, null, null , null));
    }

}
