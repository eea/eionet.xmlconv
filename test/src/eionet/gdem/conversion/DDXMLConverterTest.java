/*
 * Created on 08.05.2008
 */
package eionet.gdem.conversion;

import java.util.Map;

import junit.framework.TestCase;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * DDXMLConverterTest
 */

public class DDXMLConverterTest extends TestCase{

	/**
	 * Tests convert method - validate the result file and metadata( content type and file name) 
	 */
	public void testGetContainerSchemaUrl() throws Exception {
		String url = DDXMLConverter.getContainerSchemaUrl("http://dd.eionet.europa.eu/GetSchema?id=TBL4948");
		assertEquals("http://dd.eionet.europa.eu/GetContainerSchema?id=TBL4948",url);
	}

	/**
	 * Tests convert method - validate the result file and metadata( content type and file name) 
	 */
	public void testGetElementsDefs() throws Exception {
		String schemaUrl =TestUtils.getSeedURL(TestConstants.SEED_GW_CONTAINER_SCHEMA,this);
		DDXMLConverter converter = new Excel2XML();
		Map elemDefs = converter.getSchemaElemDefs(schemaUrl);
		assertEquals(elemDefs.size(),43);
		
		String type = (String)elemDefs.get("GWEWN-Code");
		assertEquals("xs:string",type);
		
		String type2 = (String)elemDefs.get("GWArea");
		assertEquals("xs:decimal",type2);
		

	}
}
