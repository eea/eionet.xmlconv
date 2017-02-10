package eionet.gdem.services;

import eionet.gdem.data.obligations.Obligation;
import eionet.gdem.http.HttpDefaultClientFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 *
 *
 */
public class ObligationService {

    private RDFParser rdfParser;

    @Autowired
    public ObligationService(RDFParser rdfParser) {
        this.rdfParser = rdfParser;
    }

    public List<Obligation> getObligations() throws IOException {
/*        CloseableHttpClient client = HttpDefaultClientFactory.getInstance();
        HttpGet httpGet = new HttpGet("http://rod.eionet.europa.eu/obligations/rdf");
        HttpCacheContext context = HttpCacheContext.create();
        CloseableHttpResponse response = null;
        response = client.execute(httpGet, context);
        Model model = Rio.parse(response.getEntity().getContent(), "", RDFFormat.RDFXML);

        client.*/
    }

}
