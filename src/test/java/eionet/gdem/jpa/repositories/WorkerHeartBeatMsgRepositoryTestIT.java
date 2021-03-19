package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
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

import javax.sql.DataSource;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkerHeartBeatMsgRepositoryTestIT {

    @Autowired
    private DataSource db;

    @Autowired
    WorkerHeartBeatMsgRepository repository;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_WORKER_HEART_BEAT_XML);
    }

    @Test
    public void testFindJobHeartBeatMessages() {
        List<WorkerHeartBeatMsgEntry> workerHeartBeatMsgEntries = repository.findUnAnsweredHeartBeatMessages(15);
        assertEquals(workerHeartBeatMsgEntries.size(), 2);
    }

    @Test(expected = Exception.class)
    public void testSaveNull()  {
        repository.save((WorkerHeartBeatMsgEntry) null);
    }
}







































