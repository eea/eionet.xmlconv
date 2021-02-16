package eionet.gdem.api.qa.service;

import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.service.impl.QaServiceImpl;
import eionet.gdem.dto.Schema;
import eionet.gdem.qa.QueryService;
import eionet.gdem.test.ApplicationTestContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class QaServiceTest {

    private QaServiceImpl qaService;

    @Mock
    private QueryService queryServiceMock;

    DocumentBuilder documentBuilder;

    @Autowired
    private DataSource db;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.qaService = new QaServiceImpl(queryServiceMock);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QA_XML);
    }

    @Test
    public void testExtractLinksAndSchemasFromEnvelopeUrl() throws URISyntaxException, ParserConfigurationException, SAXException, IOException, XMLConvException {
        URL url = this.getClass().getResource("/envelope-xml.xml");
        String envelopeUrl = "https://cdrtest.eionet.europa.eu/gr/colvjazdw/envvkyrww/AutomaticQA_70556";
        File XmlFile = new File(url.toURI());
        QaServiceImpl spy = spy(qaService);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(XmlFile);
        when(spy.getXMLFromEnvelopeURL(envelopeUrl)).thenReturn(doc);
        HashMap<String, String> fileLinksAndSchemas = new HashMap<String, String>();
        fileLinksAndSchemas.put("http://cdrtest.eionet.europa.eu/gr/sample.xml", "http://dd.eionet.europa.eu/GetSchema?id=1234");
        HashMap<String, String> realResults = spy.extractFileLinksAndSchemasFromEnvelopeUrl(envelopeUrl);
        Assert.assertEquals(fileLinksAndSchemas.get("http://cdrtest.eionet.europa.eu/gr/sample.xml"), realResults.get("http://cdrtest.eionet.europa.eu/gr/sample.xml"));
    }


    /* Test case: get schema by url successful */
    @Test
    public void testGetSchemaBySchemaUrlSuccessful() throws Exception {
        Schema result = qaService.getSchemaBySchemaUrl("http://localhost/not_existing.xsd");
        Assert.assertThat(result.getId(), is("58"));
        Assert.assertThat(result.getSchema(), is("http://localhost/not_existing.xsd"));
        Assert.assertThat(result.getDescription(), is("Expired dummy"));
        Assert.assertThat(result.getDtdPublicId(), is(""));
        Assert.assertThat(result.isDoValidation(), is(false));
        Assert.assertThat(result.getSchemaLang(), is("XSD"));
        Assert.assertThat(result.isBlocker(), is(false));
        Assert.assertThat(result.getMaxExecutionTime(), is(nullValue()));
    }

}
