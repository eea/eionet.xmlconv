/*
 * Created on 31.01.2008
 */
package eionet.gdem.validation;

import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import junit.framework.TestCase;

/**
 * Tests ValidationService methods
 *
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ValidationServiceTest
 */

public class ValidationServiceTest extends TestCase {
	
	
	/**
	 * Test XML file validation method. Requires seed-gw-invalid.xml file. The file is not valid.
	 * 
	 * @throws Exception
	 */
	public void testValidateInvalidXML() throws Exception{
		ValidationService validService = new ValidationService();
		String s = validService.validateSchema(TestUtils.getSeedURL(TestConstants.SEED_GW_INVALID_XML,this),
				TestUtils.getSeedURL(TestConstants.SEED_GW_SCHEMA,this));
		
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
		String s = validService.validateSchema(TestUtils.getSeedURL(TestConstants.SEED_GW_VALID_XML,this),
				TestUtils.getSeedURL(TestConstants.SEED_GW_SCHEMA,this));
		
		//System.out.println(s);

		assertTrue(s.startsWith("<div"));
		assertTrue(s.indexOf("OK")>0);
	}
}
