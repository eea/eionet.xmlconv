package eionet.gdem.web.struts.schema;

import servletunit.struts.MockStrutsTestCase;

/**
 *
 * @author Nikolaos Nakas
 */
public final class SchemaUrlValidatorTest extends MockStrutsTestCase {
    
    public void testUrlValidity() {
        String[] validUrls = new String[] {
            "http://test.someurl.eu/path",
            "https://ttt.someurl.com/",
            "https://domain.someurl.org",
            "https://domain.someurl.sub.org/path/file.xml",
            "https://domain.someurl.org   "
        };
        String[] invalidUrls = new String[] {
            "not a url",
            "qhdj https://swjfd.test.com",
            "http://test.ccc"
        };
        
        SchemaUrlValidator validator = new SchemaUrlValidator();
        
        for (String validUrl1 : validUrls) {
            assertEquals(true, validator.isValidUrl(validUrl1));
            
            for (String validUrl2 : validUrls) {
                String urlSet = validUrl1 + " " + validUrl2;
                assertEquals(true, validator.isValidUrlSet(urlSet));
            }
            
            for (String invalidUrl : invalidUrls) {
                String urlSet = validUrl1 + " " + invalidUrl;
                assertEquals(false, validator.isValidUrlSet(urlSet));
            }
        }
        
        for (String invalidUrl : invalidUrls) {
            assertEquals(false, validator.isValidUrl(invalidUrl));
        }
    }
}
