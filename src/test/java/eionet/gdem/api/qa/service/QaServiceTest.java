package eionet.gdem.api.qa.service;

import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.service.impl.QaServiceImpl;
import eionet.gdem.qa.XQueryService;
import eionet.gdem.test.ApplicationTestContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class QaServiceTest {

    private QaServiceImpl qaService;

    @Mock
    private XQueryService xqueryServiceMock;
    // @Spy
    DocumentBuilder documentBuilder;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.qaService = new QaServiceImpl(xqueryServiceMock);
    }

    @Test
    public void testExtractLinksAndSchemasFromEnvelopeUrl() throws URISyntaxException, ParserConfigurationException, SAXException, IOException, XMLConvException {
        URL url = this.getClass().getResource("/envelope-xml.xml");
        String envelopeUrl = "http://cdrtest.eionet.europa.eu/gr/colvjazdw/envvkyrww/AutomaticQA_70556";
        File XmlFile = new File(url.toURI());
        QaServiceImpl spy = spy(qaService);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(XmlFile);
        when(spy.getXMLFromEnvelopeURL(envelopeUrl)).thenReturn(doc);
        HashMap<String, String> fileLinksAndSchemas = new HashMap<String, String>();
        fileLinksAndSchemas.put("http://cdrtest.eionet.europa.eu/gr/sample.xml", "http://dd.eionet.europa.eu/GetSchema?id=1234");
        HashMap<String, String> realResults = spy.extractLinksAndSchemasFromEnvelopeUrl(envelopeUrl);
        Assert.assertEquals(fileLinksAndSchemas.get("http://cdrtest.eionet.europa.eu/gr/sample.xml"), realResults.get("http://cdrtest.eionet.europa.eu/gr/sample.xml"));
    }

    
    @Test
    public void testSuccessRunQaScript() throws XMLConvException{
      this.qaService = new QaServiceImpl(xqueryServiceMock);
      String sourceUrl ="source.url";
      String scriptId ="-1";
      when(xqueryServiceMock.runQAScript(sourceUrl, scriptId)).thenReturn(new Vector());
      qaService.runQaScript(sourceUrl, scriptId);
      verify(xqueryServiceMock,times(1)).runQAScript(sourceUrl, scriptId);
    }
    
    @Test
    public void testSuccessListQaScripts() throws XMLConvException {
        this.qaService = new QaServiceImpl(xqueryServiceMock);
        Hashtable<String, String> ht = new Hashtable<String, String>();
        ht.put("somekey", "somevalue");
        when(xqueryServiceMock.getResult(any(String.class))).thenReturn(ht);
        Hashtable<String, String> realResult = this.qaService.getJobResults("22");
        verify(xqueryServiceMock, times(1)).getResult("22");
        assertTrue(EqualsBuilder.reflectionEquals(realResult, ht));
    }
    
    @Test
    public void testSuccessGetJobResults() throws XMLConvException{
            this.qaService = new QaServiceImpl(xqueryServiceMock);
            String jobId = "22";
            Hashtable<String,String> results = new Hashtable<String,String>();
            results.put("jobstatus","success");
            when(xqueryServiceMock.getResult(jobId)).thenReturn(results);
            Hashtable<String,String> realResults = this.qaService.getJobResults(jobId);
            verify(xqueryServiceMock,times(1)).getResult(jobId);
            Assert.assertEquals(results,realResults);
    }
    
}
