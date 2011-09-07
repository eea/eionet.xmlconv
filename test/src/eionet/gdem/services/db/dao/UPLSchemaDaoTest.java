/*
 * Created on 21.04.2008
 */
package eionet.gdem.services.db.dao;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS UPLSchemaDAOTest
 */

public class UPLSchemaDaoTest extends DBTestCase {

    private IUPLSchemaDao uplSchemaDao = GDEMServices.getDaoService().getUPLSchemaDao();

    /**
     * Provide a connection to the database.
     */
    public UPLSchemaDaoTest(String name) {
        super(name);
        DbHelper.setUpConnectionProperties();
    }

    /**
     * Set up test case properties
     */
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.setUpProperties(this);
    }

    /**
     * Load the data which will be inserted for the test
     */
    protected IDataSet getDataSet() throws Exception {
        IDataSet loadedDataSet =
                new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_UPL_SCHEMAS_XML));
        return loadedDataSet;
    }

    /**
     * The method adds UPL schema into DB, then it edits the properties and finally deletes the added schema. After each operation
     * it scheks the properties values.
     * 
     * @throws Exception
     */
    public void testUPLSchemaMethods() throws Exception {
        String schemaId = "83";
        String fileName = "schema.xsd";
        String descr = "test General report schema";
        String url = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";

        // get all uploaded schemas
        List schemas = uplSchemaDao.getUplSchema();
        // count schemas stored in data file
        int countSchemas = schemas.size();

        // add schema int db and upoload schema file
        uplSchemaDao.addUplSchema(fileName, null, schemaId);

        // count schemas
        List schemas2 = uplSchemaDao.getUplSchema();
        int countSchemas2 = schemas2.size();

        // check if the nuber of schemas is increased
        assertEquals(countSchemas + 1, countSchemas2);

        // the method should return the file name of locally stored schema by FK_SCHEMA_ID
        HashMap uploadedSchema = uplSchemaDao.getUplSchemaByFkSchemaId(schemaId);
        String uplSchemaId = (String) uploadedSchema.get("upl_schema_id");
        assertEquals((String) uploadedSchema.get("schema_id"), schemaId);
        assertEquals((String) uploadedSchema.get("xml_schema"), url);
        assertEquals((String) uploadedSchema.get("upl_schema_file"), fileName);

        // Get schema by ID and test if all inserted fields are in DB
        Hashtable schema = uplSchemaDao.getUplSchemaById(uplSchemaId);
        assertEquals((String) schema.get("schema_id"), schemaId);
        assertEquals((String) schema.get("xml_schema"), url);
        assertEquals((String) schema.get("upl_schema_file"), fileName);

        // check boolean methods
        assertTrue(uplSchemaDao.checkUplSchemaFile(fileName));
        assertTrue(uplSchemaDao.checkUplSchemaFK(schemaId));
        assertFalse(uplSchemaDao.checkUplSchemaFile(fileName + "222"));
        assertFalse(uplSchemaDao.checkUplSchemaFK("222"));

        // upadate schema fileds
        uplSchemaDao.updateUplSchema(uplSchemaId, fileName + "UPD", null, schemaId);

        // Get schema by ID and test if all upadted fields are in DB
        schema = uplSchemaDao.getUplSchemaById(uplSchemaId);
        assertEquals((String) schema.get("upl_schema_id"), uplSchemaId);
        assertEquals((String) schema.get("upl_schema_file"), fileName + "UPD");

        // delete inserted schema
        uplSchemaDao.removeUplSchema(uplSchemaId);

        // count schemas
        List schemas3 = uplSchemaDao.getUplSchema();
        int countSchemas3 = schemas3.size();

        // check if the nuber of schemas is the same as in the beginning
        assertEquals(countSchemas, countSchemas3);
    }

    /**
     * The method test if it gets the local file name by URL
     * 
     * @throws Exception
     */
    public void testGetSchemaByURL() throws Exception {
        String schemaUrl1 = "http://www.oasis-open.org/committees/xliff/documents/xliff.dtd";
        String schemaUrl2 = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";

        SchemaManager sm = new SchemaManager();

        HashMap schema1 = uplSchemaDao.getUplSchemaByUrl(schemaUrl1);
        assertEquals((String) schema1.get("schema"), "xliff.dtd");

        HashMap schema2 = uplSchemaDao.getUplSchemaByUrl(schemaUrl2);
        assertTrue(schema2 == null);
    }
}
