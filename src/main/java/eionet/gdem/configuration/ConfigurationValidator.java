package eionet.gdem.configuration;

/**
 *
 * Generic configuration validator.Classes that implement this interface can 
 * add custom validation logic to the configurationFactory
 * 
 */
public interface ConfigurationValidator {
    void validate() throws ConfigurationException;
}
