package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryRepositoryTestIT {

    @Autowired
    private DataSource db;

    @Autowired
    private QueryRepository queryRepository;

    @Before
    public void setUp() throws Exception {
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QA_XML);
    }

    @Test
    public void testFindByQueryId() {
        QueryEntry queryEntry = queryRepository.findByQueryId(25);
        assertThat(queryEntry.getShortName(), is("Summer ozone - information"));
        assertThat(queryEntry.getQueryFileName(), is("sum-oz_info_1920_1.xql"));
    }

    @Test
    public void testFindMaxVersion() {
        Integer version = queryRepository.findMaxVersion(25);
        assertThat(version, is(5));
    }

    @Test
    public void testSave() {
        QueryEntry queryEntry = new QueryEntry().setShortName("test").setQueryFileName("queryFileName").setSchemaId(100)
                .setResultType("HTML").setScriptType("xquery 1.0").setVersion(4).setMarkedHeavy(false);
        QueryEntry result = queryRepository.save(queryEntry);
        assertThat(result.getShortName(), is("test"));
        assertThat(result.getQueryFileName(), is("queryFileName"));
    }
}


















