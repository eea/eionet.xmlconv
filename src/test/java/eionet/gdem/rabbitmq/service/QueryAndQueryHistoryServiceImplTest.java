package eionet.gdem.rabbitmq.service;

import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.QueryHistoryService;
import eionet.gdem.jpa.service.QueryJpaService;
import eionet.gdem.rabbitMQ.service.QueryAndQueryHistoryServiceImpl;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryAndQueryHistoryServiceImplTest {

    @Mock
    private QueryJpaService queryJpaService;
    @Mock
    private QueryHistoryService queryHistoryService;
    @Spy
    @InjectMocks
    private QueryAndQueryHistoryServiceImpl queryAndQueryHistoryService;

    @Before
    public void setUp() throws DatabaseException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveQueryAndQueryHistoryEntries() {
        QueryEntry queryEntry = new QueryEntry().setQueryId(1);
        QueryHistoryEntry queryHistoryEntry = new QueryHistoryEntry().setShortName("testQueryHistoryEntry");
        when(queryJpaService.save(any(QueryEntry.class))).thenReturn(queryEntry);
        when(queryHistoryService.save(any(QueryHistoryEntry.class))).thenReturn(queryHistoryEntry);
        queryAndQueryHistoryService.saveQueryAndQueryHistoryEntries(queryEntry, queryHistoryEntry);
        verify(queryAndQueryHistoryService).saveQueryAndQueryHistoryEntries(queryEntry, queryHistoryEntry);
    }
}










