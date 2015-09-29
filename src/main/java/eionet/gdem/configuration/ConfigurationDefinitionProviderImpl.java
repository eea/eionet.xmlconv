package eionet.gdem.configuration;

import eionet.gdem.configuration.util.ConfigurationLoadException;
import eionet.gdem.configuration.util.PropertyResourceLoader;
import eionet.gdem.configuration.util.PropertyResourceLoaderImpl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class ConfigurationDefinitionProviderImpl implements ConfigurationDefinitionProvider {

    private String[] resourceNames;
    private PropertyResourceLoader propertyResourceLoader;
    
    public ConfigurationDefinitionProviderImpl(String... resourceNames) {
        this(new PropertyResourceLoaderImpl(), resourceNames);
    }
    
    public ConfigurationDefinitionProviderImpl(PropertyResourceLoader propertyResourceLoader, String... resourceNames) {
        this.propertyResourceLoader = propertyResourceLoader;
        this.resourceNames = resourceNames;
    }

    @Override
    public Map<String, String> getConfigurationDefinition() throws ConfigurationLoadException {
        Map<String, String> configuration = new HashMap<String, String>();
        
        for (String resourceName : this.resourceNames) {
            Properties props;
            
            try {
                props = this.propertyResourceLoader.loadFromResource(resourceName);
            }
            catch (IOException ex) {
                throw new ConfigurationLoadException(ex);
            }
            
            for (Entry<Object, Object> entry : props.entrySet()) {
                configuration.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        
        return configuration;
    }

}
