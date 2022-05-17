package eionet.gdem.jpa.service;

import eionet.gdem.Constants;
import eionet.gdem.jpa.Entities.QueryMetadataEntry;
import eionet.gdem.jpa.Entities.QueryMetadataHistoryEntry;
import eionet.gdem.jpa.repositories.QueryMetadataHistoryRepository;
import eionet.gdem.jpa.repositories.QueryMetadataRepository;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.sql.DataSource;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryMetadataServiceTestIT {

    @Autowired
    QueryMetadataServiceImpl queryMetadataService;

    @Autowired
    QueryMetadataRepository queryMetadataRepository;

    @Autowired
    QueryMetadataHistoryRepository queryMetadataHistoryRepository;

    @Autowired
    DataSource db;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QUERY_METADATA_XML);

    }

    /*There is no entry in QUERY_METADATA table for the given queryID */
    @Test
    public void testStoreScriptInformationForNewScript(){
        Integer queryID = 1;
        Long durationOfJob = Long.valueOf(324000); //5.4 minutes
        queryMetadataService.storeScriptInformation(queryID, "testFile1", "xq", durationOfJob, Constants.XQ_READY, 1, null, "testUrl", 10000L);

        //Check entry in table QUERY_METADATA
        List<QueryMetadataEntry> queryMetadataEntryList = queryMetadataRepository.findByQueryIdAndMaxVersion(queryID);
        assertThat(queryMetadataEntryList, is(notNullValue()));
        assertThat(queryMetadataEntryList.size(), is(1));

        QueryMetadataEntry queryMetadataEntry = queryMetadataEntryList.get(0);

        assertThat(queryMetadataEntry.getQueryId(), is(queryID));
        assertThat(queryMetadataEntry.getScriptFilename(), is("testFile1"));
        assertThat(queryMetadataEntry.getScriptType(), is("xq"));
        assertThat(queryMetadataEntry.getAverageDuration(), is(durationOfJob));
        assertThat(queryMetadataEntry.getNumberOfExecutions(), is(1));
        assertThat(queryMetadataEntry.getMarkedHeavy(), is(false));
        assertThat(queryMetadataEntry.getVersion(), is(1));

        //Check entry in table QUERY_METADATA_HISTORY
        List<QueryMetadataHistoryEntry> queryMetadataHistoryEntryList = queryMetadataHistoryRepository.findByQueryId(queryID);
        assertThat(queryMetadataHistoryEntryList, is(notNullValue()));
        assertThat(queryMetadataHistoryEntryList.size(), is(1));

        QueryMetadataHistoryEntry queryMetadataHistoryEntry = queryMetadataHistoryEntryList.get(0);

        assertThat(queryMetadataHistoryEntry.getQueryId(), is(queryID));
        assertThat(queryMetadataHistoryEntry.getScriptFilename(), is("testFile1"));
        assertThat(queryMetadataHistoryEntry.getScriptType(), is("xq"));
        assertThat(queryMetadataHistoryEntry.getDuration(), is(durationOfJob));
        assertThat(queryMetadataHistoryEntry.getJobStatus(), is(3));
        assertThat(queryMetadataHistoryEntry.getMarkedHeavy(), is(false));
        assertThat(queryMetadataHistoryEntry.getVersion(), is(1));
        assertThat(queryMetadataHistoryEntry.getFmeJobId(), is(nullValue()));
        assertThat(queryMetadataHistoryEntry.getXmlUrl(), is("testUrl"));
        assertThat(queryMetadataHistoryEntry.getXmlSize(), is(10000L));

    }

    /*There is an entry in QUERY_METADATA table for the given queryID and numberOfExecutions = 1*/
    @Test
    public void testStoreScriptInformationForExistingScript() throws Exception {
        Integer queryID = 2;
        Long durationOfJob = Long.valueOf(180000); //3 minutes
        queryMetadataService.storeScriptInformation(queryID, "testFile2", "xq", durationOfJob, Constants.XQ_READY, 1, 1000000L, "testUrl", 10000L);

        //Check entry in table QUERY_METADATA
        List<QueryMetadataEntry> queryMetadataEntryList = queryMetadataRepository.findByQueryIdAndMaxVersion(queryID);
        assertThat(queryMetadataEntryList.size(), is(1));

        QueryMetadataEntry queryMetadataEntry = queryMetadataEntryList.get(0);
        Long averageDuration = (durationOfJob + 720000)/2;

        assertThat(queryMetadataEntry.getQueryId(), is(queryID));
        assertThat(queryMetadataEntry.getScriptFilename(), is("testFile2"));
        assertThat(queryMetadataEntry.getScriptType(), is("xq"));
        assertThat(queryMetadataEntry.getAverageDuration(), is(averageDuration));
        assertThat(queryMetadataEntry.getNumberOfExecutions(), is(2));
        assertThat(queryMetadataEntry.getMarkedHeavy(), is(false));
        assertThat(queryMetadataEntry.getVersion(), is(1));

        //Check entry in table QUERY_METADATA_HISTORY
        List<QueryMetadataHistoryEntry> queryMetadataHistoryEntryList = queryMetadataHistoryRepository.findByQueryId(queryID);
        assertThat(queryMetadataHistoryEntryList, is(notNullValue()));
        assertThat(queryMetadataHistoryEntryList.size(), is(2));

        //get the last inserted entry
        QueryMetadataHistoryEntry newQueryMetadataHistoryEntry = queryMetadataHistoryEntryList.get(1);

        assertThat(newQueryMetadataHistoryEntry.getQueryId(), is(queryID));
        assertThat(newQueryMetadataHistoryEntry.getScriptFilename(), is("testFile2"));
        assertThat(newQueryMetadataHistoryEntry.getScriptType(), is("xq"));
        assertThat(newQueryMetadataHistoryEntry.getDuration(), is(durationOfJob));
        assertThat(newQueryMetadataHistoryEntry.getJobStatus(), is(3));
        assertThat(newQueryMetadataHistoryEntry.getMarkedHeavy(), is(false));
        assertThat(newQueryMetadataHistoryEntry.getVersion(), is(1));
        assertThat(newQueryMetadataHistoryEntry.getFmeJobId(), is(1000000L));
        assertThat(newQueryMetadataHistoryEntry.getXmlUrl(), is("testUrl"));
        assertThat(newQueryMetadataHistoryEntry.getXmlSize(), is(10000L));
    }

    /*There is an entry in QUERY_METADATA table for the given queryID and numberOfExecutions = 4*/
    @Test
    public void testStoreScriptInformationForExistingScriptWithMultipleExecutions() throws Exception {
        Integer queryID = 3;
        Long durationOfJob = Long.valueOf(180000); //3 minutes
        queryMetadataService.storeScriptInformation(queryID, "testFile3", "xq", durationOfJob, Constants.XQ_READY, 1, 1000000L, "testUrl", 10000L);

        //Check entry in table QUERY_METADATA
        List<QueryMetadataEntry> queryMetadataEntryList = queryMetadataRepository.findByQueryIdAndMaxVersion(queryID);
        assertThat(queryMetadataEntryList.size(), is(1));

        QueryMetadataEntry queryMetadataEntry = queryMetadataEntryList.get(0);

        assertThat(queryMetadataEntry.getQueryId(), is(queryID));
        assertThat(queryMetadataEntry.getScriptFilename(), is("testFile3"));
        assertThat(queryMetadataEntry.getScriptType(), is("xq"));
        assertThat(queryMetadataEntry.getAverageDuration(), is(Long.valueOf(228000)));
        assertThat(queryMetadataEntry.getNumberOfExecutions(), is(5));
        assertThat(queryMetadataEntry.getMarkedHeavy(), is(false));
        assertThat(queryMetadataEntry.getVersion(), is(1));

        //Check entry in table QUERY_METADATA_HISTORY
        List<QueryMetadataHistoryEntry> queryMetadataHistoryEntryList = queryMetadataHistoryRepository.findByQueryId(queryID);
        assertThat(queryMetadataHistoryEntryList, is(notNullValue()));
        assertThat(queryMetadataHistoryEntryList.size(), is(5));

        //get the last inserted entry
        QueryMetadataHistoryEntry newQueryMetadataHistoryEntry = queryMetadataHistoryEntryList.get(4);

        assertThat(newQueryMetadataHistoryEntry.getQueryId(), is(queryID));
        assertThat(newQueryMetadataHistoryEntry.getScriptFilename(), is("testFile3"));
        assertThat(newQueryMetadataHistoryEntry.getScriptType(), is("xq"));
        assertThat(newQueryMetadataHistoryEntry.getDuration(), is(durationOfJob));
        assertThat(newQueryMetadataHistoryEntry.getJobStatus(), is(3));
        assertThat(newQueryMetadataHistoryEntry.getMarkedHeavy(), is(false));
        assertThat(newQueryMetadataHistoryEntry.getVersion(), is(1));
        assertThat(newQueryMetadataHistoryEntry.getFmeJobId(), is(1000000L));
        assertThat(newQueryMetadataHistoryEntry.getXmlUrl(), is("testUrl"));
        assertThat(newQueryMetadataHistoryEntry.getXmlSize(), is(10000L));
    }

    /*Query id does not exist */
    @Test
    public void testStoreScriptInformationForNotExistingQueryId() throws Exception {
        Integer queryID = -1;
        Long durationOfJob = Long.valueOf(180000); //3 minutes
        queryMetadataService.storeScriptInformation(queryID, "testFile3", "xq", durationOfJob, Constants.XQ_READY, 1, null, "testUrl", 10000L);

        //Check entry in table QUERY_METADATA
        List<QueryMetadataEntry> queryMetadataEntryList = queryMetadataRepository.findByQueryIdAndMaxVersion(queryID);
        assertThat(queryMetadataEntryList.size(), is(0));

        //Check entry in table QUERY_METADATA_HISTORY
        List<QueryMetadataHistoryEntry> queryMetadataHistoryEntryList = queryMetadataHistoryRepository.findByQueryId(queryID);
        assertThat(queryMetadataHistoryEntryList.size(), is(0));
    }
}
