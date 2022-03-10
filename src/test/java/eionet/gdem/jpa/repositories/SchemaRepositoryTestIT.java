package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.SchemaEntry;
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

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class SchemaRepositoryTestIT {

    @Autowired
    private DataSource db;

    @Autowired
    SchemaRepository schemaRepository;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_SCHEMAS_XML);
    }

    @Test
    public void testFindBySchemaId() {
        SchemaEntry schemaEntry = schemaRepository.findBySchemaId(83);
        assertThat(schemaEntry.getXmlSchema(), is("http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd"));
        assertThat(schemaEntry.getMaxExecutionTime(), is(BigInteger.valueOf(360000)));
        SchemaEntry schemaSecondEntry = schemaRepository.findBySchemaId(1);
        assertThat(schemaSecondEntry.getXmlSchema(), is("http://dd.eionet.europa.eu/GetSchema?id=TBL4564"));
        assertThat(schemaSecondEntry.getMaxExecutionTime(), is(BigInteger.valueOf(355000)));
    }
}

















