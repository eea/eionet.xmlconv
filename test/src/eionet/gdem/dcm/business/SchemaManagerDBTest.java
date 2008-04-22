/*
 * Created on 21.04.2008
 */
package eionet.gdem.dcm.business;

import java.util.List;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.Properties;
import eionet.gdem.dto.UplSchema;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockFormFile;
import eionet.gdem.web.struts.schema.UplSchemaHolder;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * SchemaManagerDBTest
 */

public class SchemaManagerDBTest  extends DBTestCase{

	
	/**
	 * Provide a connection to the database.
	 */
	public SchemaManagerDBTest(String name)	{
		super( name );
		DbHelper.setUpConnectionProperties();
	}
	/**
	 * Set up test case properties
	 */
    protected void setUp()throws Exception{
    	super.setUp();
    	TestUtils.setUpProperties(this);
    }
	/**
	 * Load the data which will be inserted for the test
	 */
	protected IDataSet getDataSet() throws Exception {
		IDataSet loadedDataSet = new FlatXmlDataSet(
				getClass().getClassLoader().getResourceAsStream(
						TestConstants.SEED_DATASET_UPL_SCHEMAS_XML));
		return loadedDataSet;
	}
	
	/**
	 * The method adds UPL schema into DB, then it edits the properties and finally deletes the added schema.
	 * After each operation it scheks the properties values.
	 * 
	 * @throws Exception
	 */
	public void testUPLSchemaMethods() throws Exception{
		String descr = "test General report schema";
		String url = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";
		String user = TestConstants.TEST_ADMIN_USER;
		
		SchemaManager sm = new SchemaManager();
		//get all uploaded schemas
		UplSchemaHolder schemas = sm.getUplSchemas(user);
		//count schemas stored in data file
		int countSchemas = schemas.getSchemas().size();
		
		MockFormFile file = new MockFormFile(getClass().getClassLoader().getResource(TestConstants.SEED_GENERALREPORT_SCHEMA)
				.getFile());
		//add schema int db and upoload schema file
		sm.addUplSchema(user, file, descr, url);
		
		//count schemas
		UplSchemaHolder schemas2 = sm.getUplSchemas(user);
		int countSchemas2 = schemas2.getSchemas().size();
		
		//check if the nuber of schemas is increased
		assertEquals(countSchemas+1,countSchemas2);
		
		//the method should return the file name of locally stored schema by URL
		String schemaFileName = sm.getUplSchemaURL(url);
		assertEquals(schemaFileName,TestConstants.SEED_GENERALREPORT_SCHEMA);
		
		String schemaId = null;
		List schemasList = schemas2.getSchemas();
		for (int i=0;i<schemasList.size();i++){
			UplSchema _schema = (UplSchema)schemasList.get(i);
			if(_schema.getSchema().equals(Properties.gdemURL + "/schema/" +TestConstants.SEED_GENERALREPORT_SCHEMA)){
				schemaId=_schema.getId();
				break;
			}
		}
		
		//Get schema by ID and test if all inserted fields are in DB
		UplSchema schema = sm.getUplSchemasById(schemaId);
		assertEquals(schema.getDescription(),descr);
		assertEquals(schema.getSchemaUrl(),url);
		assertEquals(schema.getSchema(),TestConstants.SEED_GENERALREPORT_SCHEMA);
		
		//upadate schema fileds
		MockFormFile file2 = new MockFormFile(getClass().getClassLoader().getResource(TestConstants.SEED_GENERALREPORT_SCHEMA_UPD)
				.getFile());
		sm.updateUplSchema(user, schemaId, TestConstants.SEED_GENERALREPORT_SCHEMA, file2, descr + "UPD", url +"UPD");
		
		//Get schema by ID and test if all upadted fields are in DB
		schema = sm.getUplSchemasById(schemaId);
		assertEquals(schema.getDescription(),descr + "UPD");
		assertEquals(schema.getSchemaUrl(),url + "UPD");
		assertEquals(schema.getSchema(),TestConstants.SEED_GENERALREPORT_SCHEMA_UPD);
		
		sm.deleteUplSchema(user, schemaId);
		
		//count schemas
		UplSchemaHolder schemas3 = sm.getUplSchemas(user);
		int countSchemas3 = schemas3.getSchemas().size();
		
		//check if the nuber of schemas is the same as in the beginning
		assertEquals(countSchemas,countSchemas3);
	}
}
