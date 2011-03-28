/*
 * Created on 06.02.2008
 */
package eionet.gdem.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import eionet.gdem.Properties;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * This is a class for unit testing the <code>eionet.gdem.utils.Utils</code> class.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS
 * UtilsTest
 */

public class UtilsTest extends TestCase {

    /**
     * The methods test helper date formatting methods
     *
     * @throws Exception
     */
    public void testDateTime() throws Exception{

        String strDate = "06.02.2008";
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);
        Date date = dateFormatter.parse(strDate);

        assertEquals(date,Utils.parseDate(strDate, pattern));
        assertEquals(strDate,Utils.getFormat(date, pattern));
        assertEquals(strDate,Utils.getFormat(Utils.parseDate(strDate, pattern),pattern));
        //use default date and time formats from gdem.properties files
        assertEquals(Utils.getFormat(date, Properties.dateFormatPattern),Utils.getDate(date));
        assertEquals(Utils.getFormat(date, Properties.timeFormatPattern),Utils.getDateTime(date));

    }

    public void testResourceExists(){
        boolean b1 = Utils.resourceExists(TestUtils.getSeedURL(TestConstants.SEED_XLIFF_XML,this));
        assertTrue(b1);

        boolean b2 = Utils.resourceExists(TestUtils.getSeedURL(TestConstants.SEED_XLIFF_XML,this).concat(".tmp"));
        assertFalse(b2);

        boolean b3 = Utils.resourceExists("https://svn.eionet.europa.eu");
        assertTrue(b3);

        boolean b4 = Utils.resourceExists("https://svn.eionet.europa.eu/thereisnoschema");
        assertFalse(b4);
    }
}
