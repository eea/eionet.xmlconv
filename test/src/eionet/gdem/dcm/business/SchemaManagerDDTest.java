/*
 * Created on 05.02.2008
 */
package eionet.gdem.dcm.business;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;
import eionet.gdem.Properties;
import eionet.gdem.dto.Schema;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.stylesheet.StylesheetListHolder;

/**
 * This is a class for unit testing the <code>eionet.gdem.dcm.business.SchemaManager</code> class.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS SchemaManagerTest
 */

public class SchemaManagerDDTest extends TestCase {

    /**
     * Test getDDSchemas method. The result should be ordered list of Schema objects. The schemas are ordered by table names,
     * dataset names and dateRelased descending.
     *
     * @throws Exception
     */
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
    public void testGetSchemasGenerated() throws Exception {
        MockSchemaManager mockSchemaManager = new MockSchemaManager();

        StylesheetListHolder stylesheetList = mockSchemaManager.getSchemas("generated");
        List schemas = stylesheetList.getDdStylesheets();

        // verify the results
        verifyDDSchemasResults(schemas);
    }

    /**
     * Verifies that the list of schemas contains the same data as defined in getDDTables() method
     *
     * @param schemas
     * @throws Exception
     */
    private void verifyDDSchemasResults(List schemas) throws Exception {
        // the list should contain 3 Schema objects
        assertEquals(3, schemas.size());

        Schema schema1 = (Schema) schemas.get(0);
        assertEquals("WISE-SOE: Ground", schema1.getDataset());
        assertEquals(Utils.parseDate("11.10.2007", "dd.MM.yyyy"), schema1.getDatasetReleased());
        assertEquals("AggregatedData_NH4", schema1.getTable());
        assertEquals(Properties.ddURL + "/GetSchema?id=TBL4558", schema1.getSchema());

        Schema schema2 = (Schema) schemas.get(1);
        assertEquals("Eionet-Water: Ground", schema2.getDataset());
        assertEquals(Utils.parseDate("02.06.2006", "dd.MM.yyyy"), schema2.getDatasetReleased());
        assertEquals("AggregatedData_NO3", schema2.getTable());
        assertEquals(Properties.ddURL + "/GetSchema?id=TBL3351", schema2.getSchema());

        Schema schema3 = (Schema) schemas.get(2);
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
        public List getDDTables() {

            Hashtable hash1 = new Hashtable();
            hash1.put("shortName", "AggregatedData_NO3");
            hash1.put("identifier", "AggregatedData_NO3");
            hash1.put("tblId", "3279");
            hash1.put("dataSet", "Eionet-Water: Ground");
            hash1.put("dateReleased", "190805");

            Hashtable hash2 = new Hashtable();
            hash2.put("shortName", "AggregatedData_NO3");
            hash2.put("identifier", "AggregatedData_NO3");
            hash2.put("tblId", "3351");
            hash2.put("dataSet", "Eionet-Water: Ground");
            hash2.put("dateReleased", "020606");

            Hashtable hash3 = new Hashtable();
            hash3.put("shortName", "AggregatedData_NH4");
            hash3.put("identifier", "AggregatedData_NH4");
            hash3.put("tblId", "4558");
            hash3.put("dataSet", "WISE-SOE: Ground");
            hash3.put("dateReleased", "111007");

            ArrayList list = new ArrayList();
            list.add(hash1);
            list.add(hash2);
            list.add(hash3);
            return list;
        }
    }

}
