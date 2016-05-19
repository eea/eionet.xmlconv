/*
 * Created on 20.03.2008
 */
package eionet.gdem.utils;

import java.io.File;
import java.io.FileInputStream;

import eionet.gdem.Properties;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ZipUtilsTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ZipUtilTest {

    /**
     * Test ZipUtil unzip method. Exctract the seed...zip file into tmp directory and check that the unzipped file is well-formed
     * XML. The seed zip file should contain 1 well-formed xml file
     * 
     * @throws Exception
     */
    @Test
    public void testUnzip() throws Exception {
        String zipSeed = getClass().getClassLoader().getResource(TestConstants.SEED_GENERAL_REPORT_ZIP).getFile();
        String strOutDir = Properties.tmpFolder + File.separator + "unzip";
        ZipUtil.unzip(zipSeed, strOutDir);

        File outDir = new File(strOutDir);
        // test if the directory exists
        assertTrue(outDir.exists());
        assertTrue(outDir.isDirectory());
        // test if the directory has 1 subitem
        assertEquals(1, outDir.list().length);

        File xmlFile = (outDir.listFiles())[0];
        // test if the extracted xml file exists
        assertTrue(xmlFile.exists());
        assertTrue(xmlFile.isFile());
        // test if the directory has 1 subitem

        // check if the extracted file is well-formed XML
        IXmlCtx x = new XmlContext();
        x.setWellFormednessChecking();
        x.checkFromInputStream(new FileInputStream(xmlFile));
    }
}
