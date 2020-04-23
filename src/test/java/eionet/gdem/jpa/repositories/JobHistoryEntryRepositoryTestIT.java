package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
@EnableJpaRepositories(basePackages = {
        "eionet.gdem.jpa.repositories"
})
public class JobHistoryEntryRepositoryTestIT {
    @Autowired
    private DataSource db;

    @Autowired
    private JobHistoryEntryRepository repository;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_JOB_HISTORY_XML);
    }

    @Test
    public void findByIdTestIdDoesntExist() throws IOException {
        Assert.assertThat(repository.findById(0), is(nullValue()));
    }

    @Test
    public void findByIdTestIdExists() throws IOException {
        JobHistoryEntry entry = repository.findById(2);
        Assert.assertThat(entry.getId(), is(2));
        Assert.assertThat(entry.getJobName(), is("job2"));
        Assert.assertThat(entry.getStatus(), is(1));
        Assert.assertThat(entry.getDateAdded().toString(), is("2017-07-23 13:10:11"));
        Assert.assertThat(entry.getJobGroup(), is("jobGroup1"));
        Assert.assertThat(entry.getDescription(), is(nullValue()));
        Assert.assertThat(entry.getJobClassName(), is(nullValue()));
        Assert.assertThat(entry.getUrl(), is(nullValue()));
        Assert.assertThat(entry.getXqFile(), is(nullValue()));
        Assert.assertThat(entry.getResultFile(), is(nullValue()));
        Assert.assertThat(entry.getXqType(), is(nullValue()));
    }


}
