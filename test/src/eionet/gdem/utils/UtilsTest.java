/*
 * Created on 06.02.2008
 */
package eionet.gdem.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import eionet.gdem.Properties;

import junit.framework.TestCase;

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
}
