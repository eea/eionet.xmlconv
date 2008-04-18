/*
 * Created on 18.04.2008
 */
package eionet.gdem.validation;

import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import junit.framework.TestCase;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * InputAnalyserTest
 */

public class InputAnalyserTest extends TestCase {

	
	/**
	 * Test if the method is able to extract XML schema from the XML file
	 * @throws Exception
	 */
	public void testSchemaFinder() throws Exception{
		InputAnalyser analyser = new InputAnalyser();
		analyser.parseXML(TestUtils.getSeedURL(TestConstants.SEED_GW_VALID_XML,this));
		
		assertEquals(analyser.hasNamespace(),true);
		assertEquals(analyser.isDTD(),false);
		assertEquals(analyser.getNamespace(),"http://dd.eionet.europa.eu/namespace.jsp?ns_id=8");
		assertEquals(analyser.getRootElement(),"GeneralCharacterisation");
		assertEquals(analyser.getSchemaNamespace(),"http://dd.eionet.europa.eu/namespace.jsp?ns_id=8");
		assertEquals(analyser.getSchemaOrDTD(),"http://dd.eionet.europa.eu/GetSchema?id=TBL4564");	
	}

	/**
	 * Test if the method is able to extract DTD information from the XML file
	 * @throws Exception
	 */
	public void testDTDFinder() throws Exception{
		InputAnalyser analyser = new InputAnalyser();
		analyser.parseXML(TestUtils.getSeedURL(TestConstants.SEED_XLIFF_XML,this));
		
		assertEquals(analyser.hasNamespace(),false);
		assertEquals(analyser.isDTD(),true);
		assertEquals(analyser.getRootElement(),"xliff");
		assertEquals(analyser.getSchemaOrDTD(),"http://www.oasis-open.org/committees/xliff/documents/xliff.dtd");	
	}
}
