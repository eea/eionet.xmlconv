package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.PropertiesEntry;
import eionet.gdem.jpa.utils.PropertiesEntryType;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class PropertiesRepositoryTestIT {

    @Autowired
    private DataSource db;

    @Autowired
    private PropertiesRepository propertiesRepository;

    @Before
    public void setUp() throws Exception {
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_PROPERTIES_XML);
    }

    @Test
    public void testFindByName() {
        PropertiesEntry propertiesEntry = propertiesRepository.findByName("test1");
        assertThat(propertiesEntry.getValue(), is("testValue"));
        assertThat(propertiesEntry.getType().getId(), is(3));
    }

    @Test
    public void testSave() {
        PropertiesEntry propertiesEntry = new PropertiesEntry().setId(3).setName("test3").setType(PropertiesEntryType.Long).setValue("100").setDescription("a long value");
        propertiesRepository.save(propertiesEntry);
        PropertiesEntry result = propertiesRepository.findByName("test3");
        assertNotNull(result);
    }

    @Test
    public void testDelete() {
        propertiesRepository.delete(4);
        PropertiesEntry result = propertiesRepository.findByName("test4");
        assertNull(result);
    }
}


















