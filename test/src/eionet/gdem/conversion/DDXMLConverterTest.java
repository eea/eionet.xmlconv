/*
 * Created on 08.05.2008
 */
package eionet.gdem.conversion;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.TestCase;
import eionet.gdem.Properties;
import eionet.gdem.conversion.datadict.DD_XMLInstance;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS DDXMLConverterTest
 */

public class DDXMLConverterTest extends TestCase {

    /**
     * Test DD schema verification method
     */
    public void testGetInvalidSchemaMessage() throws Exception {
        MockDDXMLConverter ddConverter = new MockDDXMLConverter(new Excel2XML());

        Map<String, String> dataset = new HashMap<String, String>();
        dataset.put("id", "1111");
        dataset.put("status", "Released");
        dataset.put("isLatestReleased", "true");
        dataset.put("dateOfLatestReleased", "1257138000000");
        dataset.put("idOfLatestReleased", "2222");

        ddConverter.setDataset(dataset);

        // schema is OK
        String message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=TBL3739");
        assertNull(message);
        message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=DST1111");
        assertNull(message);

        // schema is INVALID DD schema
        MockDDXMLConverter ddExcelConverter = new MockDDXMLConverter(new Excel2XML());
        message = ddExcelConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=DST1111289389");
        assertEquals(
                Properties.getMessage(BusinessConstants.ERROR_CONVERSION_INVALID_TEMPLATE,
                        new String[] {ddConverter.getSourceFormatName()}), message);
        // schema is unknown
        message = ddExcelConverter.getInvalidSchemaMessage("http://unknown.com?SchemaId=1111");
        assertNull(message);

        // schema is OBSOLETE
        dataset.put("status", "RELEASED");
        dataset.put("isLatestReleased", "false");
        dataset.put("dateOfLatestReleased", "1257138000000"); // 2 Nov 2009
        dataset.put("idOfLatestReleased", "2222");
        ddConverter.setDataset(dataset);

        message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=TBL3739");
        assertEquals(
                Properties.getMessage(BusinessConstants.ERROR_CONVERSION_OBSOLETE_TEMPLATE,
                        new String[] {ddConverter.getSourceFormatName(), "02 Nov 2009", "2222"}), message);

        // schema is not RELEASED, but OK
        message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=TBL3739");
        dataset.put("status", "Incomplete");
        dataset.put("isLatestReleased", "false");
        ddConverter.setDataset(dataset);

        message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=TBL3739");
        assertNull(message);

    }

    public void testConvertDDExcelToXml_MultipleValues() throws Exception {

        MockDDXMLConverter converter = new MockDDXMLConverter(new Excel2XML());
        Map<String, String> dataset = new HashMap<String, String>();
        dataset.put("id", "6585");
        dataset.put("status", "Released");
        dataset.put("isLatestReleased", "true");
        converter.setDataset(dataset);

        Vector conversionResult =
                converter.convertDD_XML_split(this.getClass().getClassLoader().getResource(TestConstants.SEED_MULTIVALUES_XLS)
                        .getFile(), null);
        assertTestConvertDD_MultipleValuesresults((Vector) conversionResult.get(0));

    }

    public void testConvertDDExcel2007ToXml_MultipleValues() throws Exception {

        MockDDXMLConverter converter = new MockDDXMLConverter(new Excel20072XML());
        Map<String, String> dataset = new HashMap<String, String>();
        dataset.put("id", "6585");
        dataset.put("status", "Released");
        dataset.put("isLatestReleased", "true");
        converter.setDataset(dataset);

        Vector conversionResult =
                converter.convertDD_XML_split(this.getClass().getClassLoader().getResource(TestConstants.SEED_MULTIVALUES_XLSX)
                        .getFile(), null);
        assertTestConvertDD_MultipleValuesresults((Vector) conversionResult.get(0));

    }

    public void testConvertDDOdsToXml_MultipleValues() throws Exception {

        MockDDXMLConverter converter = new MockDDXMLConverter(new Ods2Xml());
        Map<String, String> dataset = new HashMap<String, String>();
        dataset.put("id", "6585");
        dataset.put("status", "Released");
        dataset.put("isLatestReleased", "true");
        converter.setDataset(dataset);

        Vector conversionResult =
                converter.convertDD_XML_split(this.getClass().getClassLoader().getResource(TestConstants.SEED_MULTIVALUES_ODS)
                        .getFile(), null);
        assertTestConvertDD_MultipleValuesresults((Vector) conversionResult.get(0));

    }

    private void assertTestConvertDD_MultipleValuesresults(Vector conversionResult) throws Exception {

        assertEquals(3, conversionResult.size());
        assertEquals("0", (String) conversionResult.get(0));
        assertEquals("GW-Body_Characterisation.xml", (String) conversionResult.get(1));

        byte[] xml = (byte[]) conversionResult.get(2);

        // parse date values from result XML
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml);
        IXmlCtx ctx = new XmlContext();
        ctx.checkFromInputStream(inputStream);
        IXQuery xQuery = ctx.getQueryManager();

        List<String> multipleValues = xQuery.getElementValues("dd37:Stratigraphy");
        assertTrue(multipleValues.size() > 0);
        assertEquals("Cambrian", multipleValues.get(0));
        assertEquals("Carboniferous", multipleValues.get(1));
        assertEquals("Devonian", multipleValues.get(2));
        assertEquals("Jurassic,Cambrian", multipleValues.get(3));
        assertEquals("1", multipleValues.get(4));
        assertEquals("2", multipleValues.get(5));
        assertEquals("3", multipleValues.get(6));
    }

    class MockDDXMLConverter extends DDXMLConverter {

        Map<String, String> datasetResult = null;
        private DDXMLConverter spreadsheetReader;

        public MockDDXMLConverter(DDXMLConverter spreadsheetReader) {
            super();
            this.spreadsheetReader = spreadsheetReader;
        }

        protected Map getDataset(String xmlSchema) {
            return datasetResult;
        }

        public void setDataset(Map<String, String> dataset) {
            this.datasetResult = dataset;
        }

        protected void importSheetSchemas(SourceReaderIF spreadsheet, DD_XMLInstance instance, String xml_schema) {
            String localSchemaUrl = TestUtils.getSeedURL(TestConstants.SEED_GW_SCHEMA, this);
            super.importSheetSchemas(spreadsheet, instance, localSchemaUrl);
        }

        @Override
        public String getSourceFormatName() {
            return spreadsheetReader.getSourceFormatName();
        }

        @Override
        public SourceReaderIF getSourceReader() {
            return spreadsheetReader.getSourceReader();
        }
    }
}
