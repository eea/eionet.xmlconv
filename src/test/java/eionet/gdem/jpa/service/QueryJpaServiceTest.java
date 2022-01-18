package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryEntry;
import eionet.gdem.jpa.repositories.QueryRepository;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryJpaServiceTest {

    @Mock
    private QueryRepository queryRepository;

    @Spy
    @InjectMocks
    private QueryJpaServiceImpl queryJpaService;

    private QueryEntry queryEntry;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        queryEntry = new QueryEntry().setQueryId(1).setVersion(5).setShortName("testQuery").setDescription("a test query");
    }

    @Test
    public void testFindMaxVersion() {
        when(queryRepository.findMaxVersion(anyInt())).thenReturn(5);
        Integer result = queryJpaService.findMaxVersion(1);
        assertThat(result, is(5));
    }

    @Test
    public void testFindByQueryId() {
        when(queryRepository.findByQueryId(anyInt())).thenReturn(queryEntry);
        QueryEntry result = queryJpaService.findByQueryId(1);
        assertThat(result.getShortName(), is("testQuery"));
        assertThat(result.getVersion(), is(5));
    }

    @Test
    public void testUpdateVersion() {
        doNothing().when(queryRepository).updateVersion(anyInt(), anyInt());
        queryJpaService.updateVersion(6, 1);
        verify(queryJpaService).updateVersion(6, 1);
    }
}


















