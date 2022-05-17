package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.PropertiesEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.PropertiesRepository;
import eionet.gdem.jpa.utils.PropertiesEntryType;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class PropertiesServiceTest {

    @Mock
    private PropertiesRepository propertiesRepository;

    @Spy
    @InjectMocks
    private PropertiesServiceImpl propertiesService;

    private PropertiesEntry propertiesEntry;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        propertiesEntry = new PropertiesEntry().setId(1).setName("testProperty").setType(PropertiesEntryType.String).setValue("test");
    }

    @Test
    public void testFindByName() {
        when(propertiesRepository.findByName(anyString())).thenReturn(propertiesEntry);
        PropertiesEntry result = propertiesService.findByName("testProperty");
        assertThat(result.getValue(), is("test"));
    }

    @Test
    public void testGetValue() throws DatabaseException {
        when(propertiesRepository.findByName(anyString())).thenReturn(propertiesEntry);
        Object result = propertiesService.getValue("testProperty");
        assertThat(result, is("test"));
    }

    @Test
    public void testFindAll() {
        List<PropertiesEntry> propertiesEntries = new ArrayList<>();
        propertiesEntries.add(propertiesEntry);
        when(propertiesRepository.findAll()).thenReturn(propertiesEntries);
        List<PropertiesEntry> result = propertiesService.findAll();
        assertThat(result.get(0).getValue(), is("test"));
    }

    @Test
    public void testSave() {
        when(propertiesRepository.save(any(PropertiesEntry.class))).thenReturn(propertiesEntry);
        propertiesService.save(propertiesEntry);
        verify(propertiesService).save(propertiesEntry);
    }

    @Test
    public void testDelete() {
        doNothing().when(propertiesRepository).delete(anyInt());
        propertiesService.delete(1);
        verify(propertiesService).delete(1);
    }
}

























