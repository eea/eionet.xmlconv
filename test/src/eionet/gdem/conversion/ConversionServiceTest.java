/*
 * Created on 31.01.2008
 */
package eionet.gdem.conversion;

import java.util.Vector;


import junit.framework.TestCase;

/**
 * Tests ConversionService methods
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ConversionServiceTest
 */

public class ConversionServiceTest extends TestCase{

	protected final String CONVERSION_SUCCEEDED= "Conversion succeeded!";
	
	protected String getSeedURL(){
		
        String filename = getClass().getClassLoader().getResource("seed-rivers.xls").getFile();

        return "file://".concat(filename);
	}
	/**
	 * Test DataDictionary MS Excel file to XML conversion.
	 * seed-rivers.xls should be in the root of test classes. 
	 * MS Excel file should contain text "Conversion succeeded!" in one of the cells.
	 * Test parses the result Vector and checks, if XML file contains string "Conversion succeeded!"
	 * 
	 * @throws Exception
	 */
	public void testConvertDD_XML() throws Exception{

		ConversionService convService = new ConversionService();
		Vector v = convService.convertDD_XML(getSeedURL());
		
		// test if the result Vector is in correct format:
		//["0", byte[], "XML filename"]
		
		assertEquals(3,v.size());
		assertEquals("0",(String)v.get(0));	

		byte[] xml = (byte[])v.get(1);
		String strXML = new String(xml, "UTF-8");
		assertTrue(strXML.indexOf(CONVERSION_SUCCEEDED)>0);

		assertEquals("seed-rivers.xml",(String)v.get(2));
	}
	/**
	 * Test DataDictionary MS Excel file to XML conversion ConvertDD_XML_split method.
	 * seed-rivers.xls should be in the root of test classes. 
	 * MS Excel file should contain text "Conversion succeeded!" in one of the cells.
	 * Test parses the result Vector and checks, if XML file contains string "Conversion succeeded!"
	 * 
	 * @throws Exception
	 */
	public void testConvertDD_XML_split() throws Exception{
		//System.out.println(filename);

		ConversionService convService = new ConversionService();
		Vector v = convService.convertDD_XML_split(getSeedURL(),"BasicQuality");
		
		// test if the result Vector is in correct format:
		//[["0", "XML filename", byte[]]]
		
		assertEquals(1,v.size());
		assertTrue(v.get(0) instanceof Vector);
		Vector v2 = (Vector)v.get(0);
		
		assertEquals(3,v2.size());
		//result code=0 
		assertEquals("0",(String)v2.get(0));	

		//sheet name + .xml
		assertEquals("BasicQuality.xml",(String)v2.get(1));

		//xml content as byte array
		byte[] xml = (byte[])v2.get(2);
		String strXML = new String(xml, "UTF-8");
		assertTrue(strXML.indexOf("Conversion succeeded!")>0);
	}
	/**
	 * Test DataDictionary MS Excel file to XML conversion ConvertDD_XML_split method.
	 * Parse the result, if the Excel does not contain specified sheet
	 * 
	 * @throws Exception
	 */
	public void testConvertDD_XML_split_nosheet() throws Exception{
		//System.out.println(filename);

		ConversionService convService = new ConversionService();
		Vector v = convService.convertDD_XML_split(getSeedURL(),"NOSHEET");
		
		// test if the result Vector is in correct format:
		//[["0", "XML filename", byte[]]]
		
		assertEquals(1,v.size());
		assertTrue(v.get(0) instanceof Vector);
		Vector v2 = (Vector)v.get(0);
		
		assertEquals(3,v2.size());
		//result code = 1
		assertEquals("1",(String)v2.get(0));	

		//sheet name
		assertEquals("NOSHEET",(String)v2.get(1));

		//error message
		assertTrue(((String)v2.get(2)).indexOf("NOSHEET")>0);
	}
}
