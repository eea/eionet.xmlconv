package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.AlertEntry;
import eionet.gdem.jpa.enums.AlertSeverity;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class AlertRepositoryTestIT {

    @Autowired
    private DataSource db;

    @Autowired
    private AlertRepository alertRepository;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_ALERTS_XML);
    }

    @Test
    public void testFindAll() {
        List<AlertEntry> alertEntries = alertRepository.findAll();
        assertThat(alertEntries.size(), is(2));
    }

    @Test
    public void testSave() {
        AlertEntry alertEntry = new AlertEntry().setId(3).setSeverity(AlertSeverity.MEDIUM).setDescription("alert3").setNotificationSentToUns(false)
                .setOccurrenceDate(new Timestamp(new Date().getTime()));
        AlertEntry result = alertRepository.save(alertEntry);
        assertThat(result.getDescription(), is(alertEntry.getDescription()));
        assertThat(result.getId(), is(alertEntry.getId()));
    }

    @Test
    public void testDelete() {
        AlertEntry alertEntry = new AlertEntry().setId(4).setSeverity(AlertSeverity.CRITICAL).setDescription("alert4").setNotificationSentToUns(false)
                .setOccurrenceDate(new Timestamp(new Date().getTime()));
        alertRepository.save(alertEntry);
        alertRepository.delete(alertEntry.getId());
        AlertEntry result = alertRepository.findOne(alertEntry.getId());
        assertNull(result);
    }

}



























