package eionet.gdem.configuration.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class PropertyResourceLoaderImpl implements PropertyResourceLoader {

    @Override
    public Properties loadFromResource(String resourceName) throws IOException {
        URL resourceUrl = this.getClass().getClassLoader().getResource(resourceName);
        
        if (resourceUrl == null) {
            String msg = String.format("Resource not found: %s", resourceName);
            throw new FileNotFoundException(msg);
        }
        
        InputStream in = null;
        
        try {
            in = resourceUrl.openStream();
            Properties properties = new Properties();
            properties.load(in);
            
            return properties;
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }

}
