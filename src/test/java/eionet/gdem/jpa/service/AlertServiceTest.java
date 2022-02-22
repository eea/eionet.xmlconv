package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.AlertEntry;
import eionet.gdem.jpa.enums.AlertSeverity;
import eionet.gdem.jpa.repositories.AlertRepository;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Spy
    @InjectMocks
    private AlertServiceImpl alertService;

    private AlertEntry alertEntry;
    private List<AlertEntry> alertEntryList;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        alertEntry = new AlertEntry().setId(1).setSeverity(AlertSeverity.LOW.getId()).setDescription("error").setNotificationSentToUns(false)
                .setOccurrenceDate(new Timestamp(new Date().getTime()));
        alertEntryList = new ArrayList<>();
        alertEntryList.add(alertEntry);
    }

    @Test
    public void testSave() {
        when(alertRepository.save(any(AlertEntry.class))).thenReturn(alertEntry);
        alertService.save(alertEntry);
        verify(alertService).save(alertEntry);
    }

    @Test
    public void testFindAll() {
        when(alertRepository.findAll()).thenReturn(alertEntryList);
        List<AlertEntry> result = alertService.findAll();
        assertThat(result.get(0).getId(), is(alertEntry.getId()));
    }

    @Test
    public void testDelete() {
        doNothing().when(alertRepository).delete(anyInt());
        alertService.delete(alertEntry.getId());
        verify(alertService).delete(alertEntry.getId());
    }

}






















