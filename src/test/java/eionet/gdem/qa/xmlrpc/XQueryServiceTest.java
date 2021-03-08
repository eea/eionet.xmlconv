package eionet.gdem.qa.xmlrpc;

import eionet.gdem.XMLConvException;
import eionet.gdem.qa.QueryService;
import eionet.gdem.services.JobRequestHandlerService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
//These tests will be ignored for now  the methods that are called are static
@Ignore
public class XQueryServiceTest {

    @Mock
    private XQueryService xQueryService;

    @Mock
    private QueryService queryService;

    @Mock
    private JobRequestHandlerService jobRequestHandlerService;

    private List<Hashtable> queriesList;

    private Hashtable testHashTable;

    private HashMap testHashMap;

    @Before
    public void setUp() throws XMLConvException {
        MockitoAnnotations.initMocks(this);
        queriesList = initQueriesList();
        testHashTable = initTestHashTable();
        testHashMap = initTestHashMap();
        when(queryService.listQueries(Mockito.anyString())).thenReturn(queriesList);
        when(jobRequestHandlerService.analyzeMultipleXMLFiles(Mockito.any())).thenReturn(testHashMap);
        when(xQueryService.getQueryService()).thenReturn(queryService);
        when(xQueryService.getJobRequestHandlerService()).thenReturn(jobRequestHandlerService);
    }

    @Test
    public void testListQueries() throws Exception {
        when(xQueryService.listQueries(Mockito.anyString())).thenCallRealMethod();
        Vector v = xQueryService.listQueries("test");
        Assert.assertThat(v.size(), is(2));
        Assert.assertThat(((Hashtable) v.get(0)).get("test1Key"), is("test1Value"));
        Assert.assertThat(((Hashtable) v.get(0)).get("test2Key"), is("test2Value"));
        Assert.assertThat(((Hashtable) v.get(0)).get("test3Key"), is("test3Value"));
        Assert.assertThat(((Hashtable) v.get(1)).get("test4Key"), is("test4Value"));
        Assert.assertThat(((Hashtable) v.get(1)).get("test5Key"), is("test5Value"));
        Assert.assertThat(((Hashtable) v.get(1)).get("test6Key"), is("test6Value"));
    }

    @Test
    public void testAnalyzeXMLFiles() throws Exception {
        when(xQueryService.analyzeXMLFiles(Mockito.any())).thenCallRealMethod();
        Vector v = xQueryService.analyzeXMLFiles(testHashTable);
        Assert.assertThat(v.size(), is(3));

        Assert.assertThat(((Vector) v.get(0)).size(), is(2));
        Assert.assertThat(((Vector) v.get(0)).get(0), is("test1Key"));
        Assert.assertThat(((Vector) v.get(0)).get(1), is("test1Value"));

        Assert.assertThat(((Vector) v.get(1)).size(), is(2));
        Assert.assertThat(((Vector) v.get(1)).get(0), is("test3Key"));
        Assert.assertThat(((Vector) v.get(1)).get(1), is("test3Value"));

        Assert.assertThat(((Vector) v.get(2)).size(), is(2));
        Assert.assertThat(((Vector) v.get(2)).get(0), is("test2Key"));
        Assert.assertThat(((Vector) v.get(2)).get(1), is("test2Value"));
    }

    private List<Hashtable> initQueriesList(){
        List<Hashtable> queries = new ArrayList<>();
        Hashtable ht1 = new Hashtable();
        ht1.put("test1Key", "test1Value");
        ht1.put("test2Key", "test2Value");
        ht1.put("test3Key", "test3Value");
        queries.add(ht1);

        Hashtable ht2 = new Hashtable();
        ht2.put("test4Key", "test4Value");
        ht2.put("test5Key", "test5Value");
        ht2.put("test6Key", "test6Value");
        queries.add(ht2);

        return queries;
    }

    private Hashtable initTestHashTable(){
        Hashtable table = new Hashtable();

        Vector v1 = new Vector();
        v1.add("file1");
        v1.add("file2");
        v1.add("file3");

        table.put("schema1", v1);

        Vector v2 = new Vector();
        v1.add("file4");
        table.put("schema2", v2);

        return table;
    }

    private HashMap initTestHashMap(){
        HashMap map = new HashMap();
        map.put("test1Key", "test1Value");
        map.put("test2Key", "test2Value");
        map.put("test3Key", "test3Value");

        return map;
    }
}
