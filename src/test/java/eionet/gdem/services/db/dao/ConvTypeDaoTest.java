package eionet.gdem.services.db.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.EqualPredicate;
import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.dto.ConversionDto;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ConvTypeDaoTest {

    @Autowired
    private DataSource db;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_CONVERSIONS_XML);
    }

    @Autowired
    private IConvTypeDao convTypeDao;

    @Test
    public void testListConversions() throws Exception {
        Vector<ConversionDto> conversions = convTypeDao.listConversions("1-the-first-schema.xsd");
        assertEquals(2, conversions.size());

        for (ConversionDto conversion : conversions) {
            if (conversion.getConvId().equals("180")) {
                assertEquals("1-the-first-schema.xsd", conversion.getXmlSchema());
                assertEquals("stylesheet", conversion.getDescription());
                assertEquals("HTML", conversion.getResultType());
                assertEquals(conversion.getStylesheet(), "file.xsl");
            } else if (conversion.getConvId().equals("181")) {
                assertEquals("1-the-first-schema.xsd", conversion.getXmlSchema());
                assertEquals("stylesheet", conversion.getDescription());
                assertEquals("HTML", conversion.getResultType());
                assertEquals(conversion.getStylesheet(), "file.xsl2");
            }
        }
    }

    @Test
    public void testListConversionsAllSchemas() throws Exception {
        Vector<ConversionDto> conversions = convTypeDao.listConversions(null);
        assertTrue(conversions.size() > 20);

        CollectionUtils.filter(conversions, new BeanPredicate("xmlSchema", new EqualPredicate("1-the-first-schema.xsd")));

        for (ConversionDto conversion : conversions) {
            if (conversion.getConvId().equals("180")) {
                assertEquals("1-the-first-schema.xsd", conversion.getXmlSchema());
                assertEquals("stylesheet", conversion.getDescription());
                assertEquals("HTML", conversion.getResultType());
                assertEquals(conversion.getStylesheet(), "file.xsl");
            } else if (conversion.getConvId().equals("181")) {
                assertEquals("1-the-first-schema.xsd", conversion.getXmlSchema());
                assertEquals("stylesheet", conversion.getDescription());
                assertEquals("HTML", conversion.getResultType());
                assertEquals(conversion.getStylesheet(), "file.xsl2");
            }
        }
    }

    @Test
    public void testGetConvType() throws Exception {
        Hashtable convType = convTypeDao.getConvType("RDF");
        assertEquals("application/rdf+xml", convType.get("content_type"));
        assertEquals("rdf", convType.get("file_ext"));
        assertEquals("Semantic Web resources", convType.get("description"));
    }

    @Test
    public void testGetUnknownConvType() throws Exception {
        Hashtable convType = convTypeDao.getConvType("UNKNOWN");
        assertNull(convType);
    }

    @Test
    public void testGetConvTypes() throws Exception {
        Vector convTypes = convTypeDao.getConvTypes();
        Hashtable convType = (Hashtable) convTypes.get(0);
        assertEquals("EXCEL", convType.get("conv_type"));
        assertEquals("application/vnd.ms-excel", convType.get("content_type"));
        assertEquals("xls", convType.get("file_ext"));
        assertEquals("Excel", convType.get("description"));

    }
}
