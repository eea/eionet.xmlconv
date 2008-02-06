/*
 * Created on 31.01.2008
 */
package eionet.gdem.validation;

import junit.framework.TestCase;

/**
 * Tests ValidationService methods
 *
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ValidationServiceTest
 */

public class ValidationServiceTest extends TestCase {
	
	public static final String SEED_GW_VALID_XML = "seed-gw-valid.xml";
	
	public static final String SEED_GW_INVALID_XML = "seed-gw-invalid.xml";

	public static final String SEED_GW_SCHEMA = "seed-gw-schema.xsd";
	
	/**
	 * construct URI from seed file name
	 * @param seedName	eg. "seed.xml"
	 * @return	
	 */
	protected String getSeedURL(String seedName){
		
        String filename = getClass().getClassLoader().getResource(seedName).getFile();

        return "file://".concat(filename);
	}
	/**
	 * Test XML file validation method. Requires seed-gw-invalid.xml file. The file is not valid.
	 * 
	 * @throws Exception
	 */
	public void testValidateInvalidXML() throws Exception{
		ValidationService validService = new ValidationService();
		String s = validService.validateSchema(getSeedURL(SEED_GW_INVALID_XML),getSeedURL(SEED_GW_SCHEMA));
		
		//System.out.println(s);

		assertTrue(s.startsWith("<div"));
		assertTrue(s.indexOf("ERROR")>0);
	}
	/**
	 * Test XML file validation method. Requires seed-gw-valid.xml file. The file is valid.
	 * 
	 * @throws Exception
	 */
	public void testValidateValidXML() throws Exception{
		ValidationService validService = new ValidationService();
		String s = validService.validateSchema(getSeedURL(SEED_GW_VALID_XML),getSeedURL(SEED_GW_SCHEMA));
		
		//System.out.println(s);

		assertTrue(s.startsWith("<div"));
		assertTrue(s.indexOf("OK")>0);
	}
}
