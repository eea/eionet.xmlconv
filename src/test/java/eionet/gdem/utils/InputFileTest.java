/*
 * Created on 31.01.2008
 */
package eionet.gdem.utils;

import eionet.gdem.Properties;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test test InputFile functionality. InputFile is responsible for parsing retreived URL - escapes the URL, extracts the file
 * name, host and folder.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS InputFileTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class InputFileTest {

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
    }

    /**
     * The method tests, if InputFile class extracts correct strings from URL.
     *
     * @throws Exception
     */
    @Test
    public void testPublicMethodsOnURLWithParams() throws Exception {
        InputFile inputFile =
                new InputFile("http://cdrtest.eionet.europa.eu/ee/eu/art17/envriytkg/general report.xml?param=11&param2=22");

        assertEquals("general report.xml", inputFile.getFileName());
        assertEquals("general report", inputFile.getFileNameNoExtension());
        assertEquals("/ee/eu/art17/envriytkg", inputFile.getFolderName());
        assertEquals("http://cdrtest.eionet.europa.eu", inputFile.getHostName());
        assertEquals("http://cdrtest.eionet.europa.eu/ee/eu/art17/envriytkg/general%20report.xml?param=11&param2=22", inputFile
                .getURL().toString());

        Map<String, String> cdrParams = new HashMap<String, String>();
        cdrParams.put("filename", "general report.xml");
        cdrParams.put("envelopeurl", "http://cdrtest.eionet.europa.eu/ee/eu/art17/envriytkg");
        cdrParams.put("envelopepath", "/ee/eu/art17/envriytkg");
        cdrParams.put("instance", "http://cdrtest.eionet.europa.eu/ee/eu/art17/envriytkg/general report.xml");

        assertEquals(cdrParams, inputFile.getCdrParams());
    }

    /**
     * Test the public methods with different URL.
     *
     * @throws Exception
     */
    @Test
    public void testPublicMethodsOnURLWithFragment() throws Exception {
        InputFile inputFile = new InputFile("http://localhost:8080/xmlconv/just a file.dddd#999");

        assertEquals("just a file.dddd", inputFile.getFileName());
        assertEquals("just a file", inputFile.getFileNameNoExtension());
        assertEquals("/xmlconv", inputFile.getFolderName());
        assertEquals("http://localhost:8080", inputFile.getHostName());
        assertEquals("http://localhost:8080/xmlconv/just%20a%20file.dddd#999", inputFile.getURL().toString());

        Map<String, String> cdrParams = new HashMap<String, String>();
        cdrParams.put("filename", "just a file.dddd");
        cdrParams.put("envelopeurl", "http://localhost:8080/xmlconv");
        cdrParams.put("envelopepath", "/xmlconv");
        cdrParams.put("instance", "http://localhost:8080/xmlconv/just a file.dddd");

        assertEquals(cdrParams, inputFile.getCdrParams());
    }

    @Test
    public void testFetchInputFileStoringLocally() throws Exception {
        InputFile inputFile = new InputFile(TestConstants.NETWORK_FILE_TO_TEST);
        inputFile.setStoreLocally(true);
        long sizeOfTmpDirectoryInitial = (new File(Properties.tmpFolder)).list().length;

        InputStream inputStream = inputFile.getSrcInputStream();
        long sizeOfTmpDirectoryAfterDownlaod = (new File(Properties.tmpFolder)).list().length;

        assertEquals(sizeOfTmpDirectoryInitial + 1, sizeOfTmpDirectoryAfterDownlaod);

        inputFile.close();
        long sizeOfTmpDirectoryFinal = (new File(Properties.tmpFolder)).list().length;

        assertEquals(sizeOfTmpDirectoryInitial, sizeOfTmpDirectoryFinal);
    }

    @Test
    public void testFetchInputFileNotStoringLocally() throws Exception {
        InputFile inputFile = new InputFile(TestConstants.NETWORK_FILE_TO_TEST);

        long sizeOfTmpDirectoryInitial = (new File(Properties.tmpFolder)).list().length;

        InputStream inputStream = inputFile.getSrcInputStream();
        long sizeOfTmpDirectoryAfterDownlaod = (new File(Properties.tmpFolder)).list().length;

        assertEquals(sizeOfTmpDirectoryInitial, sizeOfTmpDirectoryAfterDownlaod);

        inputFile.close();
        long sizeOfTmpDirectoryFinal = (new File(Properties.tmpFolder)).list().length;

        assertEquals(sizeOfTmpDirectoryInitial, sizeOfTmpDirectoryFinal);
    }}
