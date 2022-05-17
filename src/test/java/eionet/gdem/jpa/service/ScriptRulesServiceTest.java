package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.ScriptRulesEntry;
import eionet.gdem.jpa.repositories.ScriptRulesRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ScriptRulesServiceTest {

    @Mock
    ScriptRulesRepository scriptRulesRepository;

    @Spy
    @InjectMocks
    ScriptRulesServiceImpl scriptRulesService;

    private ScriptRulesEntry scriptRulesEntry;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        scriptRulesEntry = new ScriptRulesEntry();
        scriptRulesEntry.setId(1).setQueryId(100).setField("collection path").setType("includes").setValue("test").setEnabled(true);
    }

    @Test
    public void testFindByQueryId() {
        List<ScriptRulesEntry> scriptRulesEntries = new ArrayList<>();
        scriptRulesEntries.add(scriptRulesEntry);
        when(scriptRulesRepository.findByQueryId(anyInt())).thenReturn(scriptRulesEntries);
        List<ScriptRulesEntry> result = scriptRulesService.findByQueryId(100);
        assertThat(result.get(0).getId(), is(1));
    }

    @Test
    public void testSave() {
        when(scriptRulesRepository.save(any(ScriptRulesEntry.class))).thenReturn(scriptRulesEntry);
        ScriptRulesEntry result = scriptRulesService.save(scriptRulesEntry);
        assertThat(result.getId(), is(1));
    }

    @Test
    public void testDelete() {
        doNothing().when(scriptRulesRepository).delete(anyInt());
        scriptRulesService.delete(1);
        verify(scriptRulesService).delete(1);
    }
}


















