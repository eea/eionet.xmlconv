package eionet.gdem.configuration;

/**
 * Class that presents problems with application Configuration.
 * 
 */
public class ConfigurationException extends Exception {
    
    public ConfigurationException(String errorMessage) {
        super(errorMessage);
    } 

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
