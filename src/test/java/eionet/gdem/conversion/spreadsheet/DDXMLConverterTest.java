/*
 * Created on 08.05.2008
 */
package eionet.gdem.conversion.spreadsheet;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.conversion.datadict.DD_XMLInstance;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.dto.ConvertedFileDto;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS DDXMLConverterTest
 */

public class DDXMLConverterTest extends TestCase {

    /**
     * Test DD schema verification method
     */
    public void testGetInvalidSchemaMessage() throws Exception {
        MockDDXMLConverter ddConverter = new MockDDXMLConverter(new Excel2XML(), null);

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
        MockDDXMLConverter ddExcelConverter = new MockDDXMLConverter(new Excel2XML(), null);
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
                (Properties.getMessage(BusinessConstants.ERROR_CONVERSION_OBSOLETE_TEMPLATE,
                        new String[] {ddConverter.getSourceFormatName(), "02 Nov 2009", "2222"})).toLowerCase(), message.toLowerCase());

        // schema is not RELEASED, but OK
        message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=TBL3739");
        dataset.put("status", "Incomplete");
        dataset.put("isLatestReleased", "false");
        ddConverter.setDataset(dataset);

        message = ddConverter.getInvalidSchemaMessage("http://dd.eionet.europa.eu/GetSchema?id=TBL3739");
        assertNull(message);

    }

    public void testConvertDDExcelToXml_MultipleValues() throws Exception {

        MockDDXMLConverter converter = new MockDDXMLConverter(new Excel2XML(), new File(this.getClass().getClassLoader().getResource(TestConstants.SEED_MULTIVALUES_XLS)
                .getFile()));
        Map<String, String> dataset = new HashMap<String, String>();
        dataset.put("id", "6585");
        dataset.put("status", "Released");
        dataset.put("isLatestReleased", "true");
        converter.setDataset(dataset);

        ConversionResultDto conversionResult =
            converter.convertDD_XML_split(null, null);
        assertTestConvertDD_MultipleValuesresults(conversionResult);

    }

    public void testConvertDDExcel2007ToXml_MultipleValues() throws Exception {

        MockDDXMLConverter converter = new MockDDXMLConverter(new Excel20072XML(), new File(this.getClass().getClassLoader().getResource(TestConstants.SEED_MULTIVALUES_XLSX)
                .getFile()));
        Map<String, String> dataset = new HashMap<String, String>();
        dataset.put("id", "6585");
        dataset.put("status", "Released");
        dataset.put("isLatestReleased", "true");
        converter.setDataset(dataset);

        ConversionResultDto conversionResult =
            converter.convertDD_XML_split(null, null);
        assertTestConvertDD_MultipleValuesresults(conversionResult);

    }

    public void testConvertDDOdsToXml_MultipleValues() throws Exception {
        File inFile = new File(this.getClass().getClassLoader().getResource(TestConstants.SEED_MULTIVALUES_ODS)
                .getFile());
        MockDDXMLConverter converter = new MockDDXMLConverter(new Ods2Xml(), inFile);
        Map<String, String> dataset = new HashMap<String, String>();
        dataset.put("id", "6585");
        dataset.put("status", "Released");
        dataset.put("isLatestReleased", "true");
        converter.setDataset(dataset);

        ConversionResultDto conversionResult =
            converter.convertDD_XML_split(null, null);
        assertTestConvertDD_MultipleValuesresults(conversionResult);
    }

    private void assertTestConvertDD_MultipleValuesresults(ConversionResultDto conversionResult) throws Exception {

        assertEquals(ConversionResultDto.STATUS_OK, conversionResult.getStatusCode());
        assertNotNull(conversionResult.getConvertedFileByFileName("GW-Body_Characterisation.xml"));

        ConvertedFileDto xml = conversionResult.getConvertedFileByFileName("GW-Body_Characterisation.xml");

        IXmlCtx ctx = new XmlContext();
        ctx.checkFromInputStream(new ByteArrayInputStream(xml.getFileContentAsByteArray()));
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

    public void testConvertDDExcelToXml_Warning() throws Exception {

        MockDDXMLConverter converter = new MockDDXMLConverter(new Excel2XML(), new File(this.getClass().getClassLoader().getResource(TestConstants.SEED_VALIDATION_WARNINGS_XLS)
                .getFile()));
        Map<String, String> dataset = new HashMap<String, String>();
        dataset.put("id", "6585");
        dataset.put("status", "Released");
        dataset.put("isLatestReleased", "true");
        converter.setDataset(dataset);

        ConversionResultDto conversionResult =
            converter.convertDD_XML_split(null, null);
        assertEquals(ConversionResultDto.STATUS_ERR_VALIDATION, conversionResult.getStatusCode());
        assertNotNull(conversionResult.getConvertedFileByFileName("GW-Body_Characterisation.xml"));
        assertTrue(conversionResult.getConversionLogAsHtml().contains("WARNING"));
        assertTrue(conversionResult.getConversionLogAsHtml().contains("GWNameNew"));
    }

    class MockDDXMLConverter extends DDXMLConverter {

        Map<String, String> datasetResult = null;
        private DDXMLConverter spreadsheetReader;

        public MockDDXMLConverter(DDXMLConverter spreadsheetReader, File inFile) throws GDEMException {
            super();
            this.spreadsheetReader = spreadsheetReader;
            try{
                initConverter(inFile);
                startConverter(new ConversionResultDto(), null);
            }
            catch(GDEMException e){
                if (inFile == null){
                    //do nothing
                }
                else{
                    throw e;
                }
            }
        }

        @Override
        protected Map<String, String> getDataset(String xmlSchema) {
            return datasetResult;
        }

        public void setDataset(Map<String, String> dataset) {
            this.datasetResult = dataset;
        }

        @Override
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
