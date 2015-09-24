package eionet.gdem.configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class ConfigurationFactory {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationFactory.class.getName());

    private final Set<String> resourceNames;
    private final List<Properties> propertiesList;
    private final Map<String, String> resources;
    private final CircularReferenceValidator circularReferenceValidator;
    private final UnResolvedPropertyValidator unResolvedPropertyValidator;
    private final ConfigurationService configurationService;

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public Map<String, String> getResources() {
        return resources;
    }

    public ConfigurationFactory(Set<String> resourceNames) throws ConfigurationException {
        this(resourceNames, null);
    }

    public ConfigurationFactory(Set<String> resourceNames, String configSystemKey) throws ConfigurationException {
        this.resourceNames = resourceNames;
        propertiesList = new ArrayList<Properties>();
        if (configSystemKey != null) {
            loadConfigurationPropertiesFromSystemVariable(configSystemKey);
        }
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
        configurationService = new RuntimeConfigurationService(resources, new SystemPropertyProviderImpl());
        configurationService.cacheAll();

    }

    void loadConfigurationPropertiesFromSystemVariable(String key) {
        LOGGER.info("Configuration factory initialization from system property: " + key);
        String value = System.getProperty(key);
        if (value == null) {
            return;
        }
        LOGGER.info("Found file path for configuration : " + value);
        FileConfigurationSourceProvider f = new FileConfigurationSourceProvider(value);
        try {
            Properties configurationProperties = f.get();
            this.propertiesList.add(configurationProperties);
            LOGGER.info("Successfully loaded properties.");
        } catch (ConfigurationException ex) {
            Logger.getLogger(ConfigurationFactory.class.getName()).log(Level.INFO, null, ex);
        }
    }

}
