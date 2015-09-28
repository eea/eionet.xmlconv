package eionet.gdem.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConfigurationFactory {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationFactory.class.getName());

    private final Set<String> resourceNames;
    private final List<Properties> propertiesList;
    private final Map<String, String> resources;
    private final ConfigurationService configurationService;
    private final List<ConfigurationValidator> validators;
    private final List<ConfigurationCallback> callbacks;

    public ConfigurationFactory(Set<String> resourceNames) throws ConfigurationException {
        this(resourceNames, null, null, null);
    }

    public ConfigurationFactory(Set<String> resourceNames, String configSystemKey) throws ConfigurationException {
        this(resourceNames, configSystemKey, null, null);
    }

    public ConfigurationFactory(Set<String> resourceNames, String configSystemKey, List<ConfigurationValidator> validators) throws ConfigurationException {
        this(resourceNames, configSystemKey, validators, null);
    }

    /**
     *
     * @param resourceNames
     * @param configSystemKey
     * @param validators
     * @param callbacks
     * @throws ConfigurationException
     */
    public ConfigurationFactory(Set<String> resourceNames, String configSystemKey, List<ConfigurationValidator> validators, List<ConfigurationCallback> callbacks) throws ConfigurationException {

        this.resourceNames = resourceNames;
        this.validators = validators;
        this.callbacks = callbacks;

        propertiesList = new ArrayList<Properties>();
        if (configSystemKey != null) {
            loadConfigurationPropertiesFromSystemVariable(configSystemKey);
        }
        for (String resourceName : this.resourceNames) {
            PropertiesConfigurationResourceProvider p = new PropertiesConfigurationResourceProvider(resourceName);
            Properties properties = p.get();
            propertiesList.add(properties);
        }
        resources = (new MapConfigurationResourceProvider(propertiesList).get());
        configurationService = new RuntimeConfigurationService(resources, new SystemPropertyProviderImpl());
        if (this.validators != null) {
            executeConfigurationValidators();
        }
        if (this.callbacks != null) {
            executeConfigurationCallbacks();
        }

    }

    public void executeConfigurationCallbacks() {
        for (ConfigurationCallback callback : callbacks) {
            callback.execute();
        }
    }

    public void executeConfigurationValidators() throws ConfigurationException {
        for (ConfigurationValidator validator : validators) {
            validator.validate(resources);
        }
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public Map<String, String> getResources() {
        return resources;
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
