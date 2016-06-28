/*
 * Created on 31.01.2008
 */
package eionet.gdem.conversion;

import eionet.gdem.test.ApplicationTestContext;
import java.util.Hashtable;
import java.util.Vector;





import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests ConversionService methods
 *
 * @author Enriko Käsper, TietoEnator Estonia AS ConversionServiceTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ConversionServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionServiceTest.class);

    /**
     * Set up test case properties
     */
    @Before
    public void setUp() throws Exception {
        DbHelper.setUpConnectionProperties();
        TestUtils.setUpProperties(this);
        TestUtils.setUpReleasedDataset();
    }

    /**
     * Test DataDictionary MS Excel file to XML conversion. seed-rivers.xls should be in the root of test classes. MS Excel file
     * should contain text "Conversion succeeded!" in one of the cells. Test parses the result Vector and checks, if XML file
     * contains string "Conversion succeeded!"
     *
     * @throws Exception
     */
    @Test
    public void testConvertDD_XML() throws Exception {

        ConversionServiceIF convService = new ConversionService();
        Hashtable<String, Object> result = convService.convertDD_XML(TestUtils.getSeedURL(TestConstants.SEED_RIVERS_XLS, this));

        assertEquals("0", result.get("resultCode"));

        Hashtable<String, byte[]> convertedFile = ((Vector<Hashtable<String, byte[]>>)result.get("convertedFiles")).get(0);
        String strXML = new String(convertedFile.get("content"), "UTF-8");
        assertTrue(strXML.indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Test DataDictionary MS Excel file to XML conversion ConvertDD_XML_split method. seed-rivers.xls should be in the root of test
     * classes. MS Excel file should contain text "Conversion succeeded!" in one of the cells. Test parses the result Vector and
     * checks, if XML file contains string "Conversion succeeded!"
     *
     * @throws Exception
     */
    @Test
    public void testConvertDD_XML_split() throws Exception {
        // System.out.println(filename);

        ConversionServiceIF convService = new ConversionService();
        Hashtable<String, Object> result = convService.convertDD_XML_split(TestUtils.getSeedURL(TestConstants.SEED_RIVERS_XLS, this), "BasicQuality");

        // sheet name + .xml
        Hashtable<String, byte[]> convertedFile = ((Vector<Hashtable<String, byte[]>>)result.get("convertedFiles")).get(0);
        assertEquals("BasicQuality.xml", convertedFile.get("fileName"));
        assertEquals(1, ((Vector<Hashtable<String, byte[]>>)result.get("convertedFiles")).size());

        String strXML = new String(convertedFile.get("content"), "UTF-8");
        assertTrue(strXML.indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Test DataDictionary MS Excel file to XML conversion ConvertDD_XML_split method. This test will check if sheet names that exceed maximum length will be
     * correctly parsed. File seed-wise.xlsx should be in the root of test classes. MS Excel file should contain text "Conversion works!" in one of the cells.
     * Test parses the result Vector and checks, if XML file contains string "Conversion works!"
     *
     * @throws Exception
     */
    @Test
    public void testConvertDD_LongSheetName_XML_split() throws Exception {
        // System.out.println(filename);

        ConversionServiceIF convService = new ConversionService();
        Hashtable<String, Object> result = convService.convertDD_XML_split(TestUtils.getSeedURL("seed-wise.xlsx", this), "BiologyEQRClassificationProcedure");

        // sheet name + .xml
        Hashtable<String, byte[]> convertedFile = ((Vector<Hashtable<String, byte[]>>)result.get("convertedFiles")).get(0);
        assertEquals("BiologyEQRClassificationProcedu.xml", convertedFile.get("fileName"));
        assertEquals(1, ((Vector<Hashtable<String, byte[]>>)result.get("convertedFiles")).size());

        String strXML = new String(convertedFile.get("content"), "UTF-8");
        assertTrue(strXML.indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Test DataDictionary MS Excel file to XML conversion ConvertDD_XML_split method. Parse the result, if the Excel does not
     * contain specified sheet
     *
     * @throws Exception
     */
    @Test
    public void testConvertDD_XML_split_nosheet() throws Exception {
        // System.out.println(filename);

        ConversionServiceIF convService = new ConversionService();
        Hashtable<String, Object> result = convService.convertDD_XML_split(TestUtils.getSeedURL(TestConstants.SEED_RIVERS_XLS, this), "NOSHEET");

        assertEquals("3", result.get("resultCode"));
        assertEquals(0, ((Vector<Hashtable<String, byte[]>>)result.get("convertedFiles")).size());
    }

}
