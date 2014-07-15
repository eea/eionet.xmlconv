/*
 * Created on 21.04.2008
 */
package eionet.gdem.services.db.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS UPLSchemaDAOTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class UPLSchemaDaoTest {

    @Autowired
    private IDatabaseTester databaseTester;

    @Autowired
    private IUPLSchemaDao uplSchemaDao;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDefaultDatabaseTester(databaseTester, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);
    }

    /**
     * The method adds UPL schema into DB, then it edits the properties and finally deletes the added schema. After each operation
     * it ccheks the properties values.
     *
     * @throws Exception
     */
    @Test
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
        assertEquals(uploadedSchema.get("schema_id"), schemaId);
        assertEquals(uploadedSchema.get("xml_schema"), url);
        assertEquals(uploadedSchema.get("upl_schema_file"), fileName);

        // Get schema by ID and test if all inserted fields are in DB
        Hashtable schema = uplSchemaDao.getUplSchemaById(uplSchemaId);
        assertEquals(schema.get("schema_id"), schemaId);
        assertEquals(schema.get("xml_schema"), url);
        assertEquals(schema.get("upl_schema_file"), fileName);

        // check boolean methods
        assertTrue(uplSchemaDao.checkUplSchemaFile(fileName));
        assertTrue(uplSchemaDao.checkUplSchemaFK(schemaId));
        assertFalse(uplSchemaDao.checkUplSchemaFile(fileName + "222"));
        assertFalse(uplSchemaDao.checkUplSchemaFK("222"));

        // upadate schema fileds
        uplSchemaDao.updateUplSchema(uplSchemaId, fileName + "UPD", null, schemaId);

        // Get schema by ID and test if all upadted fields are in DB
        schema = uplSchemaDao.getUplSchemaById(uplSchemaId);
        assertEquals(schema.get("upl_schema_id"), uplSchemaId);
        assertEquals(schema.get("upl_schema_file"), fileName + "UPD");

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
        assertEquals(schema1.get("schema"), "xliff.dtd");

        HashMap schema2 = uplSchemaDao.getUplSchemaByUrl(schemaUrl2);
        assertTrue(schema2 == null);
    }
}
