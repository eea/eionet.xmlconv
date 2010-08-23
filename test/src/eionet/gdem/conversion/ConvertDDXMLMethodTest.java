/*
 * Created on 09.05.2008
 */
package eionet.gdem.conversion;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import eionet.gdem.GDEMException;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;
import eionet.gdem.utils.xml.XmlException;

/**
 * Test DataDictionary MS Excel file to XML conversion.
 * seed-dates.xls should be in the resources folder. 
 * The method tests, if date fields converted successfully.
 */
/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ConvertDDXMLMethodTest
 */

public class ConvertDDXMLMethodTest extends TestCase {

	/**
	 * Set up test case properties
	 */
    protected void setUp()throws Exception{
    	super.setUp();
    	TestUtils.setUpReleasedDataset();
    }

	public void testConvertDD_XML() throws Exception{

		Vector<Object> v = convertExcel();
		assertEquals(3,v.size());
		assertEquals("0",(String)v.get(0));	
		assertEquals("seed-dates.xml",(String)v.get(2));
	}
	
	public void testConvertDD_XMLDates() throws Exception{
		Vector<Object> v = convertExcel();
		IXmlCtx ctx = getXmlFromConversionResult(v);
		IXQuery xQuery=ctx.getQueryManager();

		//TEST if result XML contains ND_EndDate values in 2008-02-01 format and not in numeric format: 39479 
		List<String> dateValues = xQuery.getElementValues("dd487:ND_EndDate");
		assertTrue(dateValues.size()>0);
		for (int i = 0; i < dateValues.size()-1; i++) {
			String dateValue=(String) dateValues.get(i);
			assertEquals(dateValue,"2008-02-01");
		}
	}
	
	public void testConvertDD_XMLNumbers() throws Exception{
		Vector<Object> v = convertExcel();
		IXmlCtx ctx = getXmlFromConversionResult(v);
		IXQuery xQuery=ctx.getQueryManager();

		//TEST if result XML contains ND_NoOfSamples values in numeric format and they are not converted to dates 
		List<String> numValues = xQuery.getElementValues("dd487:ND_NoOfSamples");
		assertTrue(numValues.size()>0);
		for (int i = 0; i < numValues.size(); i++) {
			String numValue=(String) numValues.get(i);
			if(numValue.length()>0){
				int intValue = Integer.parseInt(numValue);
				assertEquals(intValue,i+1);
			}
		}		
	}
	public void testConvertDD_XMLYearsInDateFields() throws Exception{
		Vector<Object> v = convertExcel();
		IXmlCtx ctx = getXmlFromConversionResult(v);
		IXQuery xQuery=ctx.getQueryManager();

		//TEST if result XML contains ND_EndDate values in 2008 in numeric format and not in date format 
		List<String> dateValues = xQuery.getElementValues("dd487:ND_EndDate");
		assertTrue(dateValues.size()>0);
		String dateValue=(String) dateValues.get(dateValues.size()-1);
		assertEquals(dateValue,"2008");
	}
	private Vector<Object> convertExcel() throws GDEMException{
		
		ConvertDDXMLMethod convMethod = new ConvertDDXMLMethod();
		Vector<Object> v = convMethod.convertDD_XML(TestUtils.getSeedURL(TestConstants.SEED_DATES_XLS,this));
		
		return v;

	}
	private IXmlCtx getXmlFromConversionResult(Vector<Object> v) throws XmlException{
		
		byte[] xml = (byte[])v.get(1);

		//parse date values from result XML
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xml);
		IXmlCtx ctx=new XmlContext();
		ctx.checkFromInputStream(inputStream);
		
		return ctx;
	}
}
