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

import eionet.gdem.services.GDEMServices;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * UPLSchemaDAOTest
 */

public class UPLSchemaDaoTest  extends DBTestCase{

	private  IUPLSchemaDao uplSchemaDao = GDEMServices.getDaoService().getUPLSchemaDao();

	/**
	 * Provide a connection to the database.
	 */
	public UPLSchemaDaoTest(String name)	{
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
		String fileName = "schema.xsd";
		String descr = "test General report schema";
		String url = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";
		
		//get all uploaded schemas
		List schemas = uplSchemaDao.getUplSchema();
		//count schemas stored in data file
		int countSchemas = schemas.size();
		
		//add schema int db and upoload schema file
		uplSchemaDao.addUplSchema(fileName, descr, url);
		
		//count schemas
		List schemas2 = uplSchemaDao.getUplSchema();
		int countSchemas2 = schemas2.size();
		
		//check if the nuber of schemas is increased
		assertEquals(countSchemas+1,countSchemas2);
		
		//the method should return the file name of locally stored schema by URL
		HashMap uploadedSchema = uplSchemaDao.getUplSchemaByURL(url);
		String schemaId = (String)uploadedSchema.get("schema_id");
		assertEquals((String)uploadedSchema.get("description"),descr);
		assertEquals((String)uploadedSchema.get("schema"),fileName);
		assertEquals((String)uploadedSchema.get("schema_url"),url);
		
		//Get schema by ID and test if all inserted fields are in DB
		Hashtable schema = uplSchemaDao.getUplSchemaById(schemaId);
		assertEquals((String)schema.get("description"),descr);
		assertEquals((String)schema.get("schema"),fileName);
		assertEquals((String)schema.get("schema_url"),url);
		
		//check b9oolean methods
		assertTrue(uplSchemaDao.checkUplSchemaFile(fileName));
		assertTrue(uplSchemaDao.checkUplSchemaURL(url));
		assertFalse(uplSchemaDao.checkUplSchemaFile(fileName + "222"));
		assertFalse(uplSchemaDao.checkUplSchemaURL(url + "222"));

		//upadate schema fileds
		uplSchemaDao.updateUplSchema(schemaId, fileName + "UPD", descr + "UPD", url +"UPD");
		
		//Get schema by ID and test if all upadted fields are in DB
		schema = uplSchemaDao.getUplSchemaById(schemaId);
		assertEquals((String)schema.get("schema_id"),schemaId);
		assertEquals((String)schema.get("description"),descr + "UPD");
		assertEquals((String)schema.get("schema"),fileName + "UPD");
		assertEquals((String)schema.get("schema_url"),url + "UPD");
		
		
		//delete inserted schema
		uplSchemaDao.removeUplSchema(schemaId);
		
		//count schemas
		List schemas3 = uplSchemaDao.getUplSchema();
		int countSchemas3 = schemas3.size();
		
		//check if the nuber of schemas is the same as in the beginning
		assertEquals(countSchemas,countSchemas3);
	}
}