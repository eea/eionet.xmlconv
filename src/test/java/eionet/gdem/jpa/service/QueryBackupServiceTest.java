package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.QueryBackupEntry;
import eionet.gdem.jpa.repositories.QueryBackupRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryBackupServiceTest {

    @Mock
    QueryBackupRepository queryBackupRepository;

    @Spy
    @InjectMocks
    QueryBackupServiceImpl queryBackupService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSave() {
        QueryBackupEntry queryBackupEntry = new QueryBackupEntry.QueryBackupEntryBuilder().setBackupId(1).build();
        when(queryBackupRepository.save(any(QueryBackupEntry.class))).thenReturn(queryBackupEntry);
        QueryBackupEntry result = queryBackupService.save(queryBackupEntry);
        assertThat(result.getBackupId(), is(1));
    }
}













