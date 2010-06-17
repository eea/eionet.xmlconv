/*
 * Created on 31.01.2008
 */
package eionet.gdem.utils;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * JUnit test test InputFile functionality.
 * InputFile is responsible for parsing retreived URL - escapes the URL, extracts the file name, host and folder.
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS
 * InputFileTest
 */

public class InputFileTest  extends TestCase{

	/**
	 * The method tests, if InputFile class extracts correct strings from URL
	 * 
	 * @throws Exception
	 */

    public void testPublicMethods() throws Exception{
    	InputFile inputFile = new InputFile("http://cdrtest.eionet.europa.eu/ee/eu/art17/envriytkg/general report.xml?param=11&param2=22");
    	
    	assertEquals("general report.xml", inputFile.getFileName());
    	assertEquals("general report", inputFile.getFileNameNoExtension());
    	assertEquals("/ee/eu/art17/envriytkg", inputFile.getFolderName());
    	assertEquals("http://cdrtest.eionet.europa.eu", inputFile.getHostName());
    	assertEquals("http://cdrtest.eionet.europa.eu/ee/eu/art17/envriytkg/general%20report.xml?param=11&param2=22", inputFile.getURL().toString());
    	
    	Map<String, String> cdrParams = new HashMap<String, String>();
    	cdrParams.put("filename", "general report.xml");
    	cdrParams.put("envelopeurl", "http://cdrtest.eionet.europa.eu/ee/eu/art17/envriytkg");
    	cdrParams.put("envelopepath", "/ee/eu/art17/envriytkg");
    	cdrParams.put("instance", "http://cdrtest.eionet.europa.eu/ee/eu/art17/envriytkg/general report.xml");
    
    	assertEquals(cdrParams,inputFile.getCdrParams());
    }
    
    /**
     * Test the public methods with different URL
     * 
     * @throws Exception
     */
    public static void testPublicMethods2() throws Exception{
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
    
    	assertEquals(cdrParams,inputFile.getCdrParams());
    }

}
