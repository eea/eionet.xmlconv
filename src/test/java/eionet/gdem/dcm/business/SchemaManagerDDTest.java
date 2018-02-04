/*
 * Created on 05.02.2008
 */
package eionet.gdem.dcm.business;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.stylesheet.StylesheetListHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.Properties;
import eionet.gdem.dto.DDDatasetTable;
import eionet.gdem.dto.Schema;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestUtils;
import eionet.gdem.utils.Utils;

/**
 * This is a class for unit testing the <code>eionet.gdem.dcm.business.SchemaManager</code> class.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS SchemaManagerTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class SchemaManagerDDTest {

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
    }

    /**
     * Test getDDSchemas method. The result should be ordered list of Schema objects. The schemas are ordered by table names,
     * dataset names and dateRelased descending.
     *
     * @throws Exception
     */
    @Test
    public void testGetDDSchemas() throws Exception {
        MockSchemaManager mockSchemaManager = new MockSchemaManager();

        List schemas = mockSchemaManager.getDDSchemas();

        // verify the results
        verifyDDSchemasResults(schemas);
    }

    /**
     * Test getSchemas(String user_name, String type) method. The result should be ordered list of Schema objects. The schemas are
     * ordered by table names, dataset names and dateRelased descending.
     *
     * @throws Exception
     */
    @Test
    public void testGetSchemasGenerated() throws Exception {
        MockSchemaManager mockSchemaManager = new MockSchemaManager();

        StylesheetListHolder stylesheetList = mockSchemaManager.getSchemas("generated");
        List<Schema> schemas = stylesheetList.getDdStylesheets();

        // verify the results
        verifyDDSchemasResults(schemas);
    }

    /**
     * Verifies that the list of schemas contains the same data as defined in getDDTables() method
     *
     * @param schemas
     * @throws Exception
     */
    private void verifyDDSchemasResults(List<Schema> schemas) throws Exception {
        // the list should contain 3 Schema objects
        assertEquals(3, schemas.size());

        Schema schema1 = schemas.get(0);
        assertEquals("WISE-SOE: Ground", schema1.getDataset());
        assertEquals(Utils.parseDate("11.10.2007", "dd.MM.yyyy"), schema1.getDatasetReleased());
        assertEquals("AggregatedData_NH4", schema1.getTable());
        assertEquals(Properties.ddURL + "/GetSchema?id=TBL4558", schema1.getSchema());

        Schema schema2 = schemas.get(1);
        assertEquals("Eionet-Water: Ground", schema2.getDataset());
        assertEquals(Utils.parseDate("02.06.2006", "dd.MM.yyyy"), schema2.getDatasetReleased());
        assertEquals("AggregatedData_NO3", schema2.getTable());
        assertEquals(Properties.ddURL + "/GetSchema?id=TBL3351", schema2.getSchema());

        Schema schema3 = schemas.get(2);
        assertEquals("Eionet-Water: Ground", schema3.getDataset());
        assertEquals(Utils.parseDate("19.08.2005", "dd.MM.yyyy"), schema3.getDatasetReleased());
        assertEquals("AggregatedData_NO3", schema3.getTable());
        assertEquals(Properties.ddURL + "/GetSchema?id=TBL3279", schema3.getSchema());
    }

    class MockSchemaManager extends SchemaManager {
        /**
         * Override getDDTables and construct the result of xml-rpc method (DDServiceClient.getDDTables())
         */
        @Override
        public List<DDDatasetTable> getDDTables() {

            DDDatasetTable ddTable1 = new DDDatasetTable("3279");
            ddTable1.setShortName("AggregatedData_NO3");
            ddTable1.setDataSet("Eionet-Water: Ground");
            ddTable1.setDateReleased("190805");

            DDDatasetTable hash2 = new DDDatasetTable("3351");
            hash2.setShortName("AggregatedData_NO3");
            hash2.setDataSet("Eionet-Water: Ground");
            hash2.setDateReleased("020606");

            DDDatasetTable hash3 = new DDDatasetTable("4558");
            hash3.setShortName("AggregatedData_NH4");
            hash3.setDataSet("WISE-SOE: Ground");
            hash3.setDateReleased("111007");

            List<DDDatasetTable> list = new ArrayList<DDDatasetTable>();
            list.add(ddTable1);
            list.add(hash2);
            list.add(hash3);
            return list;
        }
    }

}
