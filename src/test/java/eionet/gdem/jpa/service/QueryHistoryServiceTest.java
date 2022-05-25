package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.jpa.repositories.QueryHistoryRepository;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryHistoryServiceTest {

    @Mock
    private QueryHistoryRepository queryHistoryRepository;

    @Spy
    @InjectMocks
    private QueryHistoryServiceImpl queryHistoryService;

    private QueryHistoryEntry queryHistoryEntry1;
    private QueryHistoryEntry queryHistoryEntry2;
    private List<QueryHistoryEntry> queryHistoryEntries;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        QueryEntry queryEntry1 = new QueryEntry().setQueryId(1);
        QueryEntry queryEntry2 = new QueryEntry().setQueryId(2);
        queryHistoryEntry1 = new QueryHistoryEntry().setQueryEntry(queryEntry1).setDateModified(new Date());
        queryHistoryEntry2 = new QueryHistoryEntry().setQueryEntry(queryEntry2).setDateModified(new Date());
        queryHistoryEntries = new ArrayList<>();
        queryHistoryEntries.add(queryHistoryEntry2);
    }

    @Test
    public void testFindEntriesByQueryId() {
        when(queryHistoryRepository.findEntriesByQueryId(anyInt())).thenReturn(queryHistoryEntries);
        List<QueryHistoryEntry> result = queryHistoryService.findEntriesByQueryId(2);
        assertThat(result.size(), is(1));
    }

    @Test
    public void testSave() {
        when(queryHistoryRepository.save(any(QueryHistoryEntry.class))).thenReturn(queryHistoryEntry1);
        QueryHistoryEntry result = queryHistoryService.save(queryHistoryEntry1);
        assertThat(result.getQueryEntry().getQueryId(), is(1));
    }

    @Test
    public void testUpdateQueryId() {
        doNothing().when(queryHistoryRepository).updateQueryId(anyInt(), anyInt());
        queryHistoryService.updateQueryId(null, 1);
        verify(queryHistoryService).updateQueryId(null, 1);
    }
}












