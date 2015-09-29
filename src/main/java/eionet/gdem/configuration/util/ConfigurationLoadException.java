package eionet.gdem.configuration.util;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class ConfigurationLoadException extends Exception {

    public ConfigurationLoadException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Failed to load property configuration.";
    }
    
}
