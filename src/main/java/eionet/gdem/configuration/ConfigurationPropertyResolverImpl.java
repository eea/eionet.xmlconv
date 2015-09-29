package eionet.gdem.configuration;

import eionet.gdem.configuration.util.ConfigurationLoadException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class ConfigurationPropertyResolverImpl implements ConfigurationPropertyResolver {

    private ConfigurationDefinitionProvider configDefinitionProvider;
    private SystemPropertyProvider systemPropertyProvider;
    private PlaceholderExpressionEvaluator expressionEvaluator;
    
    public ConfigurationPropertyResolverImpl(ConfigurationDefinitionProvider configDefinitionProvider) {
        this(configDefinitionProvider, new SystemPropertyProviderImpl(), new PlaceholderExpressionEvaluator());
    }
    
    public ConfigurationPropertyResolverImpl(
            ConfigurationDefinitionProvider configDefinitionProvider,
            SystemPropertyProvider systemPropertyProvider, 
            PlaceholderExpressionEvaluator expressionEvaluator) {
        this.configDefinitionProvider = configDefinitionProvider;
        this.systemPropertyProvider = systemPropertyProvider;
        this.expressionEvaluator = expressionEvaluator;
    }
    
    @Override
    public String resolveValue(String propertyName) throws UnresolvedPropertyException, CircularReferenceException, ConfigurationLoadException {
        return this.resolveValue(propertyName, new HashSet<String>(), new HashMap<String, String>());
    }

    String resolveValue(String propertyName, final Set<String> pendingToResolve, final Map<String, String> cache) 
            throws UnresolvedPropertyException, CircularReferenceException, ConfigurationLoadException {
        if (pendingToResolve.contains(propertyName)) {
            throw new CircularReferenceException(propertyName);
        }
        
        pendingToResolve.add(propertyName);
        String value = this.systemPropertyProvider.getPropertyValue(propertyName);
        
        if (value != null) {
            return this.normalizeAndReturnValue(propertyName, value, pendingToResolve, cache);
        }
        
        Map<String, String> configuration = this.configDefinitionProvider.getConfigurationDefinition();
        
        if (!configuration.containsKey(propertyName)) {
            throw new UnresolvedPropertyException(propertyName);
        }
        
        String expression = configuration.get(propertyName);
        
        if (!StringUtils.isEmpty(expression)) {
            value = this.expressionEvaluator.evaluate(expression, this);
        }
        
        return this.normalizeAndReturnValue(propertyName, value, pendingToResolve, cache);
    }
    
    String normalizeAndReturnValue(String propertyName, String value, Set<String> pendingToResolve, Map<String, String> cache) {
        String normalizedValue = value == null ? "" : value;
        
        pendingToResolve.remove(propertyName);
        cache.put(propertyName, normalizedValue);
        
        return normalizedValue;
    }
    
}
