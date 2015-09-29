package eionet.gdem.configuration;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public interface SystemPropertyProvider {
    
    String getPropertyValue(String propertyName);
}
