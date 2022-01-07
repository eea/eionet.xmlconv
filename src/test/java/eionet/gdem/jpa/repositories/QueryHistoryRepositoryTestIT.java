package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryHistoryRepositoryTestIT {

    @Autowired
    private DataSource db;

    @Autowired
    private QueryHistoryRepository queryHistoryRepository;
    @Autowired
    private QueryRepository queryRepository;

    @Before
    public void setUp() throws Exception {
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QA_XML);
    }

    @Test
    public void testFindByQueryId() {
        List<QueryHistoryEntry> queryHistoryEntries = queryHistoryRepository.findEntriesByQueryId(25);
        assertTrue(queryHistoryEntries.size()==1);
    }

    @Transactional
    @Test
    public void testUpdateQueryId() {
        QueryEntry queryEntry = new QueryEntry(25);
        QueryHistoryEntry queryHistoryEntry = new QueryHistoryEntry().setQueryEntry(queryEntry).setShortName("test1").setQueryFileName("queryFileName").setSchemaId(100)
                .setResultType("HTML").setScriptType("xquery 1.0").setVersion(4).setMarkedHeavy(false);
        queryHistoryRepository.save(queryHistoryEntry);
        queryHistoryRepository.updateQueryId(null, 25);
        List<QueryHistoryEntry> result = queryHistoryRepository.findEntriesByQueryId(56);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSave() {
        QueryEntry queryEntry = new QueryEntry(25);
        QueryHistoryEntry queryHistoryEntry = new QueryHistoryEntry().setQueryEntry(queryEntry).setShortName("test2").setQueryFileName("queryFileName").setSchemaId(101)
                .setResultType("HTML").setScriptType("xquery 1.0").setVersion(3).setMarkedHeavy(false);
        QueryHistoryEntry result = queryHistoryRepository.save(queryHistoryEntry);
        assertThat(result.getShortName(), is("test2"));
        assertThat(result.getVersion(), is(3));
    }
}


















