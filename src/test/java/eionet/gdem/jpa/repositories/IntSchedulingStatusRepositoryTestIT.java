package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
@Ignore
public class IntSchedulingStatusRepositoryTestIT {

    @Autowired
    private DataSource db;

    @Qualifier("intSchedulingStatusRepository")
    @Autowired
    IntSchedulingStatusRepository intSchedulingStatusRepository;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_INTERNAL_STATUS_XML);
    }

    @Test
    public void testFindAll() {
        MatcherAssert.assertThat(intSchedulingStatusRepository.findAll().size(), is(3));
    }

    @Test(expected = Exception.class)
    public void testSaveNull() {
        intSchedulingStatusRepository.save((InternalSchedulingStatus) null);
    }

}
































