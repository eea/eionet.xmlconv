package eionet.gdem.configuration;

/**
 * Wrapper class in order to access System Properties.
 *
 */
public class SystemPropertyProviderImpl implements SystemPropertyProvider {


    @Override
    public String get(String value) {
        return System.getProperty(value);
    }

}
