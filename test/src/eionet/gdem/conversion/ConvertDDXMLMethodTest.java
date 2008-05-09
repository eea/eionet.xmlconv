/*
 * Created on 09.05.2008
 */
package eionet.gdem.conversion;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Vector;

import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;
import junit.framework.TestCase;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ConvertDDXMLMethodTest
 */

public class ConvertDDXMLMethodTest extends TestCase {
	/**
	 * Test DataDictionary MS Excel file to XML conversion.
	 * seed-dates.xls should be in the resources folder. 
	 * The method tests, if date fileds converted succesfully.
	 * 
	 * @throws Exception
	 */
	public void testConvertDD_XMLDates() throws Exception{

		ConvertDDXMLMethod convMethod = new ConvertDDXMLMethod();
		Vector v = convMethod.convertDD_XML(TestUtils.getSeedURL(TestConstants.SEED_DATES_XLS,this));
		
		assertEquals(3,v.size());
		assertEquals("0",(String)v.get(0));	
		assertEquals("seed-dates.xml",(String)v.get(2));

		byte[] xml = (byte[])v.get(1);
		String strXML = new String(xml, "UTF-8");

		//parse date values from result XML
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xml);
		IXmlCtx ctx=new XmlContext();
		ctx.checkFromInputStream(inputStream);
		
		IXQuery xQuery=ctx.getQueryManager();

		//TEST if result XML contains ND_EndDate values in 2008-02-01 format and not in numeric format: 39479 
		List dateValues = xQuery.getElementValues("dd487:ND_EndDate");
		assertTrue(dateValues.size()>0);
		for (int i = 0; i < dateValues.size(); i++) {
			String dateValue=(String) dateValues.get(i);
			assertEquals(dateValue,"2008-02-01");
		}

		//TEST if result XML contains ND_NoOfSamples values in numeric format and they are not converted to dates 
		List numValues = xQuery.getElementValues("dd487:ND_NoOfSamples");
		assertTrue(numValues.size()>0);
		for (int i = 0; i < numValues.size(); i++) {
			String numValue=(String) numValues.get(i);
			if(numValue.length()>0){
				int intValue = Integer.parseInt(numValue);
				assertEquals(intValue,i+1);
			}
		}
		
	}

}
