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
		String s = validService.validate(getSeedURL("seed-gw-invalid.xml"));
		
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
		String s = validService.validate(getSeedURL("seed-gw-valid.xml"));
		
		//System.out.println(s);

		assertTrue(s.startsWith("<div"));
		assertTrue(s.indexOf("OK")>0);
	}
}
