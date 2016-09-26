package eionet.gdem.conversion.converters;

import eionet.gdem.Properties;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class OdsConverterIT {

    @Test
    public void conversionTest() throws Exception {
        Properties.odsFolder = "src/main/webapp/opendoc/ods";
        OdsConverter converter = new OdsConverter();
        InputStream xml = this.getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_QA_XML);
        InputStream xsl = this.getClass().getClassLoader().getResourceAsStream("xsl/dummy.xsl");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        converter.convert(xml, xsl, out, ".ods");
        assertEquals("Expected size: ", 4886, out.size());
        xml.close();
        xsl.close();
        out.close();
    }

}