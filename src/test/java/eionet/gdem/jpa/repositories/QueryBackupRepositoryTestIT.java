package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.QueryBackupEntry;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryBackupRepositoryTestIT {

    @Autowired
    private QueryBackupRepository queryBackupRepository;

    @Test
    public void testSave() {
        QueryBackupEntry queryBackupEntry = new QueryBackupEntry.QueryBackupEntryBuilder().setBackupId(1)
                .setObjectId(10).setfTimestamp(new Timestamp(new Date().getTime())).setFileName("test.xquery").setUser("testUser").build();
        QueryBackupEntry result = queryBackupRepository.save(queryBackupEntry);
        assertThat(result.getBackupId(), is(1));
        assertThat(result.getObjectId(), is(10));
    }
}


















