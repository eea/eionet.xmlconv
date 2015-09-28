package eionet.gdem.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads a property file from System Property
 * 
 */
public class PropertiesSystemVariableConfigurationResourceProvider implements ConfigurationResourceProvider<Properties> {

    private final String lookupKey;

    public PropertiesSystemVariableConfigurationResourceProvider(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    @Override
    public Properties get() throws ConfigurationException {
        SystemPropertyProvider systemProvider = new SystemPropertyProviderImpl();
        String path = systemProvider.get(lookupKey);
        if (path == null) {
            return new Properties();
        }
        try {
            InputStream in = new FileInputStream(path);
            Properties properties = new Properties();
            properties.load(in);
            in.close();
            return properties;
        } catch (IOException e) {
            return new Properties();
        }
    }

}
