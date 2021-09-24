package eionet.gdem.services.impl;

import eionet.gdem.Constants;
import eionet.gdem.jpa.Entities.QueryMetadataEntry;
import eionet.gdem.jpa.Entities.QueryMetadataHistoryEntry;
import eionet.gdem.jpa.repositories.QueryMetadataHistoryRepository;
import eionet.gdem.jpa.repositories.QueryMetadataRepository;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.utils.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.sql.DataSource;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryMetadataServiceImplTestIT {

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
        queryMetadataService.storeScriptInformation(queryID, "testFile1", "xq", durationOfJob, Constants.XQ_READY);

        //Check entry in table QUERY_METADATA
        List<QueryMetadataEntry> queryMetadataEntryList = queryMetadataRepository.findByQueryId(queryID);
        Assert.assertThat(queryMetadataEntryList, is(notNullValue()));
        Assert.assertThat(queryMetadataEntryList.size(), is(1));

        QueryMetadataEntry queryMetadataEntry = queryMetadataEntryList.get(0);

        Assert.assertThat(queryMetadataEntry.getQueryId(), is(queryID));
        Assert.assertThat(queryMetadataEntry.getScriptFilename(), is("testFile1"));
        Assert.assertThat(queryMetadataEntry.getScriptType(), is("xq"));
        Assert.assertThat(queryMetadataEntry.getAverageDuration(), is(durationOfJob));
        Assert.assertThat(queryMetadataEntry.getNumberOfExecutions(), is(1));
        Assert.assertThat(queryMetadataEntry.getMarkedHeavy(), is(false));
        Assert.assertThat(queryMetadataEntry.getVersion(), is(1));

        //Check entry in table QUERY_METADATA_HISTORY
        List<QueryMetadataHistoryEntry> queryMetadataHistoryEntryList = queryMetadataHistoryRepository.findByQueryId(queryID);
        Assert.assertThat(queryMetadataHistoryEntryList, is(notNullValue()));
        Assert.assertThat(queryMetadataHistoryEntryList.size(), is(1));

        QueryMetadataHistoryEntry queryMetadataHistoryEntry = queryMetadataHistoryEntryList.get(0);

        Assert.assertThat(queryMetadataHistoryEntry.getQueryId(), is(queryID));
        Assert.assertThat(queryMetadataHistoryEntry.getScriptFilename(), is("testFile1"));
        Assert.assertThat(queryMetadataHistoryEntry.getScriptType(), is("xq"));
        Assert.assertThat(queryMetadataHistoryEntry.getDuration(), is(durationOfJob));
        Assert.assertThat(queryMetadataHistoryEntry.getJobStatus(), is(3));
        Assert.assertThat(queryMetadataHistoryEntry.getMarkedHeavy(), is(false));
        Assert.assertThat(queryMetadataHistoryEntry.getVersion(), is(1));

    }

    /*There is an entry in QUERY_METADATA table for the given queryID and numberOfExecutions = 1*/
    @Test
    public void testStoreScriptInformationForExistingScript() throws Exception {
        Integer queryID = 2;
        Long durationOfJob = Long.valueOf(180000); //3 minutes
        queryMetadataService.storeScriptInformation(queryID, "testFile2", "xq", durationOfJob, Constants.XQ_READY);

        //Check entry in table QUERY_METADATA
        List<QueryMetadataEntry> queryMetadataEntryList = queryMetadataRepository.findByQueryId(queryID);
        if(Utils.isNullList(queryMetadataEntryList)){
            throw new Exception("Entry with queryId 2 was not added in QUERY_METADATA table");
        }
        else if(queryMetadataEntryList.size() > 1){
            throw new Exception("More than one entries were found in QUERY_METADATA table for queryId 2");
        }
        QueryMetadataEntry queryMetadataEntry = queryMetadataEntryList.get(0);
        Long averageDuration = (durationOfJob + 720000)/2;

        Assert.assertThat(queryMetadataEntry.getQueryId(), is(queryID));
        Assert.assertThat(queryMetadataEntry.getScriptFilename(), is("testFile2"));
        Assert.assertThat(queryMetadataEntry.getScriptType(), is("xq"));
        Assert.assertThat(queryMetadataEntry.getAverageDuration(), is(averageDuration));
        Assert.assertThat(queryMetadataEntry.getNumberOfExecutions(), is(2));
        Assert.assertThat(queryMetadataEntry.getMarkedHeavy(), is(false));
        Assert.assertThat(queryMetadataEntry.getVersion(), is(1));

        //Check entry in table QUERY_METADATA_HISTORY
        List<QueryMetadataHistoryEntry> queryMetadataHistoryEntryList = queryMetadataHistoryRepository.findByQueryId(queryID);
        Assert.assertThat(queryMetadataHistoryEntryList, is(notNullValue()));
        Assert.assertThat(queryMetadataHistoryEntryList.size(), is(2));

        //get the last inserted entry
        QueryMetadataHistoryEntry newQueryMetadataHistoryEntry = queryMetadataHistoryEntryList.get(1);

        Assert.assertThat(newQueryMetadataHistoryEntry.getQueryId(), is(queryID));
        Assert.assertThat(newQueryMetadataHistoryEntry.getScriptFilename(), is("testFile2"));
        Assert.assertThat(newQueryMetadataHistoryEntry.getScriptType(), is("xq"));
        Assert.assertThat(newQueryMetadataHistoryEntry.getDuration(), is(durationOfJob));
        Assert.assertThat(newQueryMetadataHistoryEntry.getJobStatus(), is(3));
        Assert.assertThat(newQueryMetadataHistoryEntry.getMarkedHeavy(), is(false));
        Assert.assertThat(newQueryMetadataHistoryEntry.getVersion(), is(1));
    }

    /*There is an entry in QUERY_METADATA table for the given queryID and numberOfExecutions = 4*/
    @Test
    public void testStoreScriptInformationForExistingScriptWithMultipleExecutions() throws Exception {
        //(Integer queryID, String scriptFile, String scriptType, Long durationOfJob, Integer jobStatus)
        Integer queryID = 3;
        Long durationOfJob = Long.valueOf(180000); //3 minutes
        queryMetadataService.storeScriptInformation(queryID, "testFile3", "xq", durationOfJob, Constants.XQ_READY);

        //Check entry in table QUERY_METADATA
        List<QueryMetadataEntry> queryMetadataEntryList = queryMetadataRepository.findByQueryId(queryID);
        if(Utils.isNullList(queryMetadataEntryList)){
            throw new Exception("Entry with queryId 3 was not added in QUERY_METADATA table");
        }
        else if(queryMetadataEntryList.size() > 1){
            throw new Exception("More than one entries were found in QUERY_METADATA table for queryId 3");
        }
        QueryMetadataEntry queryMetadataEntry = queryMetadataEntryList.get(0);
        Long averageDuration = (durationOfJob + 240000)/5;

        Assert.assertThat(queryMetadataEntry.getQueryId(), is(queryID));
        Assert.assertThat(queryMetadataEntry.getScriptFilename(), is("testFile3"));
        Assert.assertThat(queryMetadataEntry.getScriptType(), is("xq"));
        Assert.assertThat(queryMetadataEntry.getAverageDuration(), is(averageDuration));
        Assert.assertThat(queryMetadataEntry.getNumberOfExecutions(), is(5));
        Assert.assertThat(queryMetadataEntry.getMarkedHeavy(), is(false));
        Assert.assertThat(queryMetadataEntry.getVersion(), is(1));

        //Check entry in table QUERY_METADATA_HISTORY
        List<QueryMetadataHistoryEntry> queryMetadataHistoryEntryList = queryMetadataHistoryRepository.findByQueryId(queryID);
        Assert.assertThat(queryMetadataHistoryEntryList, is(notNullValue()));
        Assert.assertThat(queryMetadataHistoryEntryList.size(), is(5));

        //get the last inserted entry
        QueryMetadataHistoryEntry newQueryMetadataHistoryEntry = queryMetadataHistoryEntryList.get(4);

        Assert.assertThat(newQueryMetadataHistoryEntry.getQueryId(), is(queryID));
        Assert.assertThat(newQueryMetadataHistoryEntry.getScriptFilename(), is("testFile3"));
        Assert.assertThat(newQueryMetadataHistoryEntry.getScriptType(), is("xq"));
        Assert.assertThat(newQueryMetadataHistoryEntry.getDuration(), is(durationOfJob));
        Assert.assertThat(newQueryMetadataHistoryEntry.getJobStatus(), is(3));
        Assert.assertThat(newQueryMetadataHistoryEntry.getMarkedHeavy(), is(false));
        Assert.assertThat(newQueryMetadataHistoryEntry.getVersion(), is(1));
    }
}
