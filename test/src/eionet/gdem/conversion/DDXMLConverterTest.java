/*
 * Created on 08.05.2008
 */
package eionet.gdem.conversion;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * DDXMLConverterTest
 */

public class DDXMLConverterTest extends TestCase{


    /**
     * Test DD schema verification methdo
     */
    public void testGetInvalidSchemaMessage() throws Exception{
		MockDDXMLConverter ddConverter = new MockDDXMLConverter();

		Map dataset = new HashMap();
		dataset.put("id", "1111");
		dataset.put("status", "Released");
		dataset.put("isLatestReleased", "true");
		dataset.put("dateOfLatestReleased", "1257138000000");
		dataset.put("idOfLatestReleased", "2222");
		
		ddConverter.setDataset(dataset);
		
		//schema is OK
		String message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=TBL3739");
		assertNull(message);
		message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=DST1111");
		assertNull(message);
		
		//schema is INVALID
		message = ddConverter.getInvalidSchemaMessage("http://unknown.com?SchemaId=1111");
		assertEquals(Properties.getMessage(
        		BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE, new String[]{ddConverter.getSourceFormatName()}), 
        		message);

		//schema is OBSOLETE
		dataset.put("status", "RELEASED");
		dataset.put("isLatestReleased", "false");
		dataset.put("dateOfLatestReleased", "1257138000000"); //2 Nov 2009
		dataset.put("idOfLatestReleased", "2222");
		ddConverter.setDataset(dataset);
		
		message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=TBL3739");
		assertEquals(Properties.getMessage(
        		BusinessConstants.ERROR_CONVERSION_OBSOLETE_TEMPLATE, new String[]{ddConverter.getSourceFormatName(),"02 Nov 2009","2222"}), 
        		message);

		//schema is not RELEASED, but OK
		message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=TBL3739");
		dataset.put("status", "Incomplete");
		dataset.put("isLatestReleased", "false");
		ddConverter.setDataset(dataset);

		message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=TBL3739");
		assertNull(message);

    }
	class MockDDXMLConverter extends Excel2XML {

		/**
		 * Override getDataset and construct the result of xml-rpc method (DDServiceClient.getDataset())
		 * 
		 */

		Map datasetResult = null;
		public MockDDXMLConverter() {
			super();
		}
		protected Map getDataset(String type, String dsId){
			return datasetResult;
		}
		public void setDataset(Map dataset){
			this.datasetResult = dataset;
		}
	}
}

