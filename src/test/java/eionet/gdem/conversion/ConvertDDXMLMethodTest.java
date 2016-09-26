/*
 * Created on 09.05.2008
 */
package eionet.gdem.conversion;

import java.io.ByteArrayInputStream;
import java.util.List;

import eionet.gdem.XMLConvException;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.dto.ConvertedFileDto;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XPathQuery;
import eionet.gdem.utils.xml.dom.DomContext;
import eionet.gdem.utils.xml.sax.SaxContext;
import eionet.gdem.utils.xml.XmlException;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test DataDictionary MS Excel file to XML conversion.
 * seed-dates.xls should be in the resources folder.
 * The method tests, if date fields converted successfully.
 */
/**
 * @author Enriko Käsper, TietoEnator Estonia AS ConvertDDXMLMethodTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ConvertDDXMLMethodTest {

    /**
     * Set up test case properties
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpReleasedDataset();
    }
    @Test
    public void testConvertDD_XML() throws Exception {

        ConversionResultDto result = convertExcel();
        assertEquals("0", result.getStatusCode());
        assertNotNull(result.getConvertedFileByFileName("seed-dates.xml"));
    }
    @Test
    public void testConvertDD_XMLDates() throws Exception {
        ConversionResultDto result = convertExcel();
        IXmlCtx ctx = getXmlFromConversionResult(result);
        XPathQuery xQuery = ctx.getQueryManager();

        // TEST if result XML contains ND_EndDate values in 2008-02-01 format and not in numeric format: 39479
        List<String> dateValues = xQuery.getElementValues("dd487:ND_EndDate");
        assertTrue(dateValues.size() > 0);
        for (int i = 0; i < dateValues.size() - 1; i++) {
            String dateValue = dateValues.get(i);
            assertEquals(dateValue, "2008-02-01");
        }
    }
    @Test
    public void testConvertDD_XMLIntegers() throws Exception {
        ConversionResultDto result = convertExcel();
        IXmlCtx ctx = getXmlFromConversionResult(result);
        XPathQuery xQuery = ctx.getQueryManager();

        // TEST if result XML contains ND_NoOfSamples values in numeric format and they are not converted to dates
        List<String> numValues = xQuery.getElementValues("dd487:ND_NoOfSamples");
        assertTrue(numValues.size() > 0);
        for (int i = 0; i < numValues.size(); i++) {
            String numValue = numValues.get(i);
            if (numValue.length() > 0) {
                int intValue = Integer.parseInt(numValue);
                assertEquals(intValue, i + 1);
            }
        }
    }
    @Test
    public void testConvertDD_XMLDouble() throws Exception {
        ConversionResultDto result = convertExcel();
        IXmlCtx ctx = getXmlFromConversionResult(result);
        XPathQuery xQuery = ctx.getQueryManager();

        // TEST if result XML contains ND_MaxValue values in numeric format and they are not converted to dates
        List<String> numValues = xQuery.getElementValues("dd487:ND_MaxValue");
        assertTrue(numValues.size() > 3);
        for (int i = 0; i < numValues.size(); i++) {
            String numValue = numValues.get(i);
            if (numValue.length() > 0) {
                switch (i) {
                case 0:
                    assertEquals(numValue, "0.00001");
                    break;
                case 1:
                    assertEquals(numValue, "0.1");
                    break;
                case 2:
                    assertEquals(numValue, "131.12");
                    break;
                case 3:
                    assertEquals(numValue, "23");
                    break;
                }

            }
        }
    }
    @Test
    public void testConvertDD_XMLYearsInDateFields() throws Exception {
        ConversionResultDto result = convertExcel();
        IXmlCtx ctx = getXmlFromConversionResult(result);
        XPathQuery xQuery = ctx.getQueryManager();

        // TEST if result XML contains ND_EndDate values in 2008 in numeric format and not in date format
        List<String> dateValues = xQuery.getElementValues("dd487:ND_EndDate");
        assertTrue(dateValues.size() > 0);
        String dateValue = dateValues.get(dateValues.size() - 1);
        assertEquals(dateValue, "2008");
    }

    private ConversionResultDto convertExcel() throws XMLConvException {

        ConvertDDXMLMethod convMethod = new ConvertDDXMLMethod();
        convMethod.setCheckSchemaValidity(false);
        ConversionResultDto result = convMethod.convertDD_XML(TestUtils.getSeedURL(TestConstants.SEED_DATES_XLS, this));

        return result;

    }

    private IXmlCtx getXmlFromConversionResult(ConversionResultDto result) throws XmlException, XMLConvException {
        ConvertedFileDto xml = result.getConvertedFileByFileName("seed-dates.xml");
        IXmlCtx ctx = new DomContext();
        ctx.checkFromInputStream(new ByteArrayInputStream(xml.getFileContentAsByteArray()));
        return ctx;
    }
}
