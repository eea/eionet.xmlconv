package eionet.gdem.configuration;

import java.util.Map;

/**
 *
 * Generic configuration validator.Classes that implement this interface can 
 * add custom validation logic to the configurationFactory
 * 
 */
public interface ConfigurationValidator {
    void validate(Map<String, String> resources) throws ConfigurationException;
}
