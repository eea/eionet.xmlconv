package eionet.gdem.utils;

import java.io.InputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestUtils;

/**
 * Attempt to download a file from CDRTEST. This is a temporary test to explore issue 21608.
 *
 * @author Søren Roug
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class DownloadFileTest {

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
    }

    @Test
    public void downloadAFile() throws Exception {
        String testFileName = "REP_D-LV_LEGMC_20141030_D-001.xml";
        String testUrl = "http://cdrtest.eionet.europa.eu/lv/eu/aqd/d/envvfm0pa/" + testFileName;

        InputFile inputFile = new InputFile(testUrl);
        assertEquals(testFileName, inputFile.getFileName());

        String outputPath; 
        File fp;

        outputPath = inputFile.saveSrcFile("unittest");
        //System.out.println(outputPath);
        fp = new File(outputPath);
        assertEquals(935726, fp.length());
        fp.delete();

        // Download again.
        outputPath = inputFile.saveSrcFile("unittest");
        fp = new File(outputPath);
        assertEquals(935726, fp.length());
        fp.delete();
    }
}
