package eionet.gdem.configuration;

/**
 * Class that presents problems with application Configuration.
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class ConfigurationException extends Exception {
    
    public ConfigurationException(String errorMessage) {
        super(errorMessage);
    } 

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
