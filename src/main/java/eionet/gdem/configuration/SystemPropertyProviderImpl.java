package eionet.gdem.configuration;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class SystemPropertyProviderImpl implements SystemPropertyProvider {

    @Override
    public String getPropertyValue(String propertyName) {
        return System.getProperty(propertyName);
    }
    
}
