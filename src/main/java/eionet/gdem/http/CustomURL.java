package eionet.gdem.http;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author George Sofianos
 */
public class CustomURL {

    private URI uri;

    public CustomURL(String url) throws URISyntaxException {
        uri = new URI(url.replace(" ", "%20"));
    }

    public String getHost() {
        return uri.getScheme() + "://" + uri.getAuthority();
    }

    public String getUri() {
        return uri.toString();
    }

    public URL getURL() throws MalformedURLException {
        return uri.toURL();
    }
}
