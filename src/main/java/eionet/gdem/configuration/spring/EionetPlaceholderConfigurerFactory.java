package eionet.gdem.configuration.spring;

import eionet.gdem.configuration.ConfigurationDefinitionProvider;
import eionet.gdem.configuration.ConfigurationPropertyResolverImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class EionetPlaceholderConfigurerFactory {

    private final ConfigurationDefinitionProvider configDefinitionProvider;
    
    @Autowired
    public EionetPlaceholderConfigurerFactory(
            @Qualifier(value = "appConfigDefinitionProvider") ConfigurationDefinitionProvider configDefinitionProvider) {
        this.configDefinitionProvider = configDefinitionProvider;
    }
    
    public EionetPlaceholderConfigurer createPlaceholderConfigurer() {
        return new EionetPlaceholderConfigurer(new ConfigurationPropertyResolverImpl(configDefinitionProvider));
    }
    
}
