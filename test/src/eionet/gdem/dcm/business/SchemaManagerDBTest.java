/*
 * Created on 21.04.2008
 */
package eionet.gdem.dcm.business;

import java.util.Calendar;
import java.util.Date;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.UplSchema;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockFormFile;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.schema.UplSchemaHolder;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS SchemaManagerDBTest
 */

public class SchemaManagerDBTest extends DBTestCase {

    /**
     * Provide a connection to the database.
     */
    public SchemaManagerDBTest(String name) {
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
        String descr = "test General report schema";
        String schemaId = "83";
        String user = TestConstants.TEST_ADMIN_USER;

        SchemaManager sm = new SchemaManager();
        // get all schemas
        UplSchemaHolder schemas = sm.getUplSchemas(user);
        // count schemas stored in data file
        int countSchemas = schemas.getSchemas().size();

        MockFormFile file =
                new MockFormFile(getClass().getClassLoader().getResource(TestConstants.SEED_GENERALREPORT_SCHEMA).getFile());
        String fileName = sm.generateSchemaFilenameByID(Properties.schemaFolder, schemaId, "xsd");
        // add schema int db and upoload schema file
        sm.addUplSchema(user, file, fileName, schemaId);

        // count schemas
        UplSchemaHolder schemas2 = sm.getUplSchemas(user);
        int countSchemas2 = schemas2.getSchemas().size();

        // The number of schemas shouldn't be inreased, because the number of schemas is the same
        assertEquals(countSchemas, countSchemas2);

        // the method should return the file name of locally stored schema by URL
        UplSchema uplSchema = sm.getUplSchemasById(schemaId);
        String schemaFileName = uplSchema.getUplSchemaFile();
        assertEquals(schemaFileName, "schema-83.xsd");

        // Get schema by ID and test if all upadted fields are in DB
        sm.deleteUplSchema(user, schemaId, false);

        // count schemas
        UplSchemaHolder schemas3 = sm.getUplSchemas(user);
        int countSchemas3 = schemas3.getSchemas().size();

        // check if the number of schemas is the same
        assertEquals(countSchemas, countSchemas3);
    }

    /**
     * The method test if it gets the local file insted of remote URL
     * 
     * @throws Exception
     */
    public void testGetSchemaByURL() throws Exception {
        String schemaUrl1 = "http://www.oasis-open.org/committees/xliff/documents/xliff.dtd";
        String schemaUrl2 = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";

        SchemaManager sm = new SchemaManager();

        String url1 = sm.getUplSchemaURL(schemaUrl1);
        assertEquals(url1, "xliff.dtd");

        String url2 = sm.getUplSchemaURL(schemaUrl2);
        assertEquals(url2, schemaUrl2);
    }

    public void testDiffRemoteSchema() throws Exception {
        SchemaManager sm = new SchemaManager();

        String schemaFile = getClass().getClassLoader().getResource(TestConstants.SEED_GENERALREPORT_SCHEMA).getFile();
        byte[] bytes = Utils.fileToBytes(schemaFile);

        // files are iodentical
        String result = sm.diffRemoteSchema(bytes, TestConstants.SEED_GENERALREPORT_SCHEMA);
        assertEquals(result, BusinessConstants.WARNING_FILES_IDENTICAL);

        // filename is empty
        result = sm.diffRemoteSchema(bytes, "");
        assertEquals(result, "");

        // file does not exists
        result = sm.diffRemoteSchema(bytes, "nofile.xsd");
        assertEquals(result, BusinessConstants.WARNING_LOCALFILE_NOTAVAILABLE);

        // files are different
        result = sm.diffRemoteSchema(bytes, "seed-gw-schema.xsd");
        assertEquals(result, BusinessConstants.WARNING_FILES_NOTIDENTICAL);
    }

    /**
     * Test schema update DB method
     * 
     * @throws Exception
     */
    public void testUpdateSchema() throws Exception {
        SchemaManager sm = new SchemaManager();
        String description = "updated";
        String schemaId = "4";
        String user = TestConstants.TEST_ADMIN_USER;
        String schema = "www.schema.com";
        String schemaLang = "XSD";
        boolean doValidation = false;
        String dtdPublicId = "";
        Date expireDate = null;

        sm.update(user, schemaId, schema, description, schemaLang, doValidation, dtdPublicId, expireDate);
        Schema sch = sm.getSchema(schemaId);

        assertEquals(description, sch.getDescription());
        assertNull(sch.getExpireDate());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        expireDate = new Date(cal.getTimeInMillis());

        sm.update(user, schemaId, schema, description, schemaLang, doValidation, dtdPublicId, expireDate);
        sch = sm.getSchema(schemaId);
        assertEquals(expireDate, sch.getExpireDate());

    }
}
