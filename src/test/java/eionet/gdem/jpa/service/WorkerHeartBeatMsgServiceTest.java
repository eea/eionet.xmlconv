package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.WorkerHeartBeatMsgEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.WorkerHeartBeatMsgRepository;
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
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkerHeartBeatMsgServiceTest {

    @Mock
    private WorkerHeartBeatMsgRepository workerHeartBeatMsgRepository;

    @Spy
    @InjectMocks
    private WorkerHeartBeatMsgServiceImpl workerHeartBeatMsgService;

    private WorkerHeartBeatMsgEntry workerHeartBeatMsgEntry;
    private List<WorkerHeartBeatMsgEntry> workerHeartBeatMsgEntryList;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        workerHeartBeatMsgEntry = new WorkerHeartBeatMsgEntry();
        workerHeartBeatMsgEntryList = new ArrayList<>();
        workerHeartBeatMsgEntry.setId(1);
        workerHeartBeatMsgEntryList.add(workerHeartBeatMsgEntry);
    }

    @Test
    public void testSave() {
        when(workerHeartBeatMsgRepository.save(any(WorkerHeartBeatMsgEntry.class))).thenReturn(workerHeartBeatMsgEntry);
        WorkerHeartBeatMsgEntry result = workerHeartBeatMsgService.save(workerHeartBeatMsgEntry);
        assertThat(result.getId(), is(1));
    }

    @Test
    public void testFindUnAnsweredHeartBeatMessages() throws DatabaseException {
        when(workerHeartBeatMsgRepository.findUnAnsweredHeartBeatMessages(anyInt())).thenReturn(workerHeartBeatMsgEntryList);
        List<WorkerHeartBeatMsgEntry> result = workerHeartBeatMsgService.findUnAnsweredHeartBeatMessages(55);
        assertThat(result.get(0).getId(), is(1));
    }

    @Test
    public void testDelete() {
        doNothing().when(workerHeartBeatMsgRepository).delete(anyInt());
        workerHeartBeatMsgService.delete(1);
        verify(workerHeartBeatMsgService).delete(1);
    }
}











