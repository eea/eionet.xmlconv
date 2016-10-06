package eionet.gdem.http;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author George Sofianos
 */
public class CustomURI {

    private URI uri;

    public CustomURI(String url) throws URISyntaxException {
        uri = new URI(url.replace(" ", "%20"));
    }

    public String getHost() {
        return uri.getScheme() + "://" + uri.getAuthority();
    }

    public URI getUri() {
        return uri;
    }

    public URL getURL() throws MalformedURLException {
        URL temp = null;
        try {
            temp = uri.toURL();
        } catch (IllegalArgumentException ae) {
            throw new MalformedURLException(ae.toString());
        }
        return temp;
    }
}
