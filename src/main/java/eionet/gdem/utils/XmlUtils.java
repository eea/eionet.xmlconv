package eionet.gdem.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class XmlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);

    private XmlUtils() {
        // do nothing
    }

    public static Long getNumberOfBytesBasedOnUrl(String urlString) {
        //open file from url as input stream
        try {
            URL url = new URL(urlString);
            CountInputStream in = new CountInputStream(url.openStream());

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            LOGGER.info("Bytes: %d", in.getCount());
            return in.getCount();
        } catch (Exception e) {
            LOGGER.error("Could not calculate size of url " + urlString + " Exception message is: " + e.getMessage());
            return null;
        }
    }
}
