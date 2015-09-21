package eionet.gdem.configuration;

import java.util.HashMap;
import java.util.Properties;
import java.util.List;
import java.util.Map;

/**
 * 
 * Provider class that transforms a list of @{link java.util.Properties} objects to @{link java.util.Map}
 */
public class MapConfigurationResourceProvider implements ConfigurationResourceProvider<Map<String, String>> {

    private final List<Properties> resources;

    public MapConfigurationResourceProvider(List<Properties> resources) {
        this.resources = resources;
    }

    @Override
    public Map<String, String> get() throws ConfigurationException {
        Map<String, String> map = new HashMap<String, String>();
        for (Properties properties : resources) {
            for (Object o : properties.keySet()) {
                String key = (String) o;
                String value = properties.getProperty(key);
                map.put(key, value);
            }
        }
        return map;
    }

}
