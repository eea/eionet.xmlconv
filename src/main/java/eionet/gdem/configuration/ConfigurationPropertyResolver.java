package eionet.gdem.configuration;

import eionet.gdem.configuration.util.ConfigurationLoadException;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public interface ConfigurationPropertyResolver {

    String resolveValue(String propertyName) throws UnresolvedPropertyException, CircularReferenceException, ConfigurationLoadException;
}
