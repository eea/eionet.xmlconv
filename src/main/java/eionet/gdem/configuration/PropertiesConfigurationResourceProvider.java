package eionet.gdem.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provider that reads a resource name and returns {@ link java.util.Properties} object.
 *
 */
public class PropertiesConfigurationResourceProvider implements ConfigurationResourceProvider<Properties> {

    private final String resourceName;

    /**
     * 
     * @param resourceName the Classpath resource
     */
    public PropertiesConfigurationResourceProvider(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * 
     * @return {@link java.util.Properties} 
     * @throws ConfigurationException file is not in classpath
     */
    @Override
    public Properties get() throws ConfigurationException {
        try {
            Properties properties = new Properties();
            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(resourceName);
            if (inStream == null) {
                throw new ConfigurationException("Error while trying to load file " + resourceName);
            }
            properties.load(inStream);
            inStream.close();
            return properties;
        } catch (IOException ioe) {
            throw new ConfigurationException("Error while trying to load file " + resourceName);
        }
    }
}
