package eionet.gdem.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ConfigurationFactory {

    private final Set<String> resourceNames;
    private final List<Properties> propertiesList;
    private final Map<String, String> resources;
    private final CircularReferenceValidator circularReferenceValidator;
    private final UnResolvedPropertyValidator unResolvedPropertyValidator;
    public Map<String, String> getResources() {
        return resources;
    }

    public ConfigurationFactory(Set<String> resourceNames) throws ConfigurationException {
        this.resourceNames = resourceNames;
        propertiesList = new ArrayList<Properties>();
        for (String resourceName : resourceNames) {
            PropertiesConfigurationResourceProvider p = new PropertiesConfigurationResourceProvider(resourceName);
            Properties properties = p.get();
            propertiesList.add(properties);
        }
        resources = (new MapConfigurationResourceProvider(propertiesList).get());
        this.circularReferenceValidator = new CircularReferenceValidator(resources);
        circularReferenceValidator.validate();
        unResolvedPropertyValidator = new UnResolvedPropertyValidator(resources, new SystemPropertyProviderImpl());
        unResolvedPropertyValidator.validate();
        

    }

}
