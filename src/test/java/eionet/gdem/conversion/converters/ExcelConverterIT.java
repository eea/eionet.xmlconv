package eionet.gdem.conversion.converters;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Simple test to see if an XML can be converted to Excel.
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ExcelConverterIT {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void convertExcel() throws Exception {
        InputStream xml = this.getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_QA_XML);
        InputStream xsl = this.getClass().getClassLoader().getResourceAsStream("xsl/dummy.xsl");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelConverter converter = new ExcelConverter();
        converter.convert(xml, xsl, out, ".xls");
        assertEquals("Expected size: ", 3584, out.size());
        xml.close();
        xsl.close();
        out.close();
    }

}