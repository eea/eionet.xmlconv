package eionet.gdem.configuration;

import eionet.gdem.configuration.util.ConfigurationLoadException;
import java.util.Map;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public interface ConfigurationDefinitionProvider {

    Map<String, String> getConfigurationDefinition() throws ConfigurationLoadException;
    
}
