/*
 * Created on 06.02.2008
 */
package eionet.gdem.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.exceptions.AclPropertiesInitializationException;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This is a class for unit testing the <code>eionet.gdem.utils.Utils</code> class.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS UtilsTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class UtilsTest {

    /**
     * The methods test helper date formatting methods
     *
     * @throws Exception
     */
    @Test
    public void testDateTime() throws Exception {

        String strDate = "06.02.2008";
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);
        Date date = dateFormatter.parse(strDate);

        assertEquals(date, Utils.parseDate(strDate, pattern));
        assertEquals(strDate, Utils.getFormat(date, pattern));
        assertEquals(strDate, Utils.getFormat(Utils.parseDate(strDate, pattern), pattern));
        // use default date and time formats from gdem.properties files
        assertEquals(Utils.getFormat(date, Properties.dateFormatPattern), Utils.getDate(date));
        assertEquals(Utils.getFormat(date, Properties.timeFormatPattern), Utils.getDateTime(date));

    }
    @Test
    public void testResourceExists() {
        boolean b1 = Utils.resourceExists(TestUtils.getSeedURL(TestConstants.SEED_XLIFF_XML, this));
        assertTrue(b1);

        boolean b2 = Utils.resourceExists(TestUtils.getSeedURL(TestConstants.SEED_XLIFF_XML, this).concat(".tmp"));
        assertFalse(b2);

        boolean b3 = Utils.resourceExists("https://svn.eionet.europa.eu");
        assertTrue(b3);

        boolean b4 = Utils.resourceExists("https://svn.eionet.europa.eu/thereisnoschema");
        assertFalse(b4);
    }
    @Test
    public void testGetTmpUniqueFileName() {
        assertTrue(Utils.getUniqueTmpFileName(null).endsWith(".tmp"));
        assertTrue(Utils.getUniqueTmpFileName("filename.xml").endsWith("filename.xml"));
        assertTrue(Utils.getUniqueTmpFileName(null).startsWith(Properties.tmpFolder + File.separator + Constants.TMP_FILE_PREFIX));
    }
    @Test
    public void testEscapeXml() {
        assertEquals("&amp;ok", Utils.escapeXML("&ok"));
        assertEquals("&amp;ok", Utils.escapeXML("&amp;ok"));
        assertEquals("?", Utils.escapeXML("\u001A"));
        assertEquals("&#57344;", Utils.escapeXML("\uE000"));
        assertEquals("\u00F6", Utils.escapeXML("\u00F6"));
    }

    @Test(expected = Exception.class)
    public void createFormatForMsNull() {
        Utils.createFormatForMs(null);
    }

    @Test
    public void createFormatForMsSuccessful() {
        assertThat("0 hours, 00 minutes, 00 seconds", is(Utils.createFormatForMs(Long.valueOf(124))));
        assertThat("0 hours, 00 minutes, 07 seconds", is(Utils.createFormatForMs(Long.valueOf(7443))));
        assertThat("0 hours, 01 minutes, 14 seconds", is(Utils.createFormatForMs(Long.valueOf(74543))));
        assertThat("3 hours, 26 minutes, 47 seconds", is(Utils.createFormatForMs(Long.valueOf(12407443))));
    }
}
