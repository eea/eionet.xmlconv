/*
 * Created on 31.01.2008
 */
package eionet.gdem.validation;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.Properties;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * Tests ValidationService methods
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS ValidationServiceTest
 */

public class ValidationServiceTest extends DBTestCase {

    /**
     * Provide a connection to the database.
     */
    public ValidationServiceTest(String name) {
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
     * Test XML file validation method. Requires seed-gw-invalid.xml file. The file is not valid.
     * 
     * @throws Exception
     */
    public void testValidateInvalidXML() throws Exception {
        ValidationService validService = new ValidationService(true);
        String s =
                validService.validateSchema(TestUtils.getSeedURL(TestConstants.SEED_GW_INVALID_XML, this),
                        TestUtils.getSeedURL(TestConstants.SEED_GW_SCHEMA, this));

        // System.out.println(s);

        assertTrue(s.startsWith("[ERROR"));
        // error objects are stored in errors list
        assertTrue(validService.getErrorList().size() > 0);
    }

    /**
     * Test XML file validation method. Requires seed-gw-valid.xml file. The file is valid.
     * 
     * @throws Exception
     */
    public void testValidateValidXML() throws Exception {
        ValidationService validService = new ValidationService(true);
        String s =
                validService.validateSchema(TestUtils.getSeedURL(TestConstants.SEED_GW_VALID_XML, this),
                        TestUtils.getSeedURL(TestConstants.SEED_GW_SCHEMA, this));

        // System.out.println(s);

        assertTrue(s.startsWith("<div"));
        assertTrue(s.indexOf("OK") > 0);
        // check if errors list is empty
        assertTrue(validService.getErrorList() == null || validService.getErrorList().size() == 0);
    }

    /**
     * Test XML file validation method. Validation Service should use locally stored XML Schema described in T_UPL_SCHEMA table.
     * Requires seed-gw-valid.xml file. The file is valid.
     * 
     * @throws Exception
     */
    public void testValidateValidXMLAgainstLocalSchema() throws Exception {
        ValidationService validService = new ValidationService(true);
        String s = validService.validate(TestUtils.getSeedURL(TestConstants.SEED_GW_VALID_XML, this));

        // System.out.println(s);

        assertTrue(s.startsWith("<div"));
        assertTrue(s.indexOf("OK") > 0);
        assertEquals(validService.getValidatedSchemaURL(), Properties.gdemURL.concat("/").concat(TestConstants.SEED_GW_SCHEMA));
        assertEquals(validService.getOriginalSchema(), "http://dd.eionet.europa.eu/GetSchema?id=TBL4564");
        // check if errors list is empty
        assertTrue(validService.getErrorList() == null || validService.getErrorList().size() == 0);
    }

    /**
     * The method tests, id xmlconv finds the schema stored in T_UPL_TABLE
     * 
     * @throws Exception
     */
    public void testSetLocalSchema() throws Exception {
        ValidationService validService = new ValidationService(true);
        validService.setLocalSchemaUrl("http://dd.eionet.europa.eu/GetSchema?id=TBL4564");

        assertTrue(validService.getValidatedSchema().endsWith(TestConstants.SEED_GW_SCHEMA));
        assertEquals(validService.getValidatedSchemaURL(), Properties.gdemURL.concat("/").concat(TestConstants.SEED_GW_SCHEMA));

        validService.setLocalSchemaUrl("http://dd.eionet.europa.eu/GetSchema");

        assertEquals(validService.getValidatedSchema(), "http://dd.eionet.europa.eu/GetSchema");
        assertEquals(validService.getValidatedSchemaURL(), "http://dd.eionet.europa.eu/GetSchema");
    }

    /**
     * Test XML file validation method. Validation Service should use locally stored XML Schema described in T_UPL_SCHEMA table.
     * Requires seed-gw-valid.xml file. The file is valid.
     * 
     * @throws Exception
     */
    public void testValidateValidXMLAgainstLocalDTD() throws Exception {
        ValidationService validService = new ValidationService(true);
        String s = validService.validate(TestUtils.getSeedURL(TestConstants.SEED_XLIFF_XML, this));

        // System.out.println(s);

        assertTrue(s.startsWith("<div"));
        assertTrue(s.indexOf("OK") > 0);
        assertEquals(validService.getValidatedSchemaURL(), Properties.gdemURL.concat("/").concat(TestConstants.SEED_XLIFF_DTD));
        assertEquals(validService.getOriginalSchema(), "http://www.oasis-open.org/committees/xliff/documents/xliff.dtd");
        // check if errors list is empty
        assertTrue(validService.getErrorList() == null || validService.getErrorList().size() == 0);
    }

    /**
     * Try to validate XML against XML schema that is not available
     * 
     * @throws Exception
     */
    public void testXMLAgainstUnavailableSchema() throws Exception {
        ValidationService validService = new ValidationService(true);
        String s =
                validService.validateSchema(TestUtils.getSeedURL(TestConstants.SEED_XLIFF_XML, this),
                        "https://svn.eionet.europa.eu/thereisnoschema");

        // System.out.println(s);

        assertTrue(s.startsWith("<div"));
        assertTrue(s.indexOf("ERROR") > 0);
    }
}
