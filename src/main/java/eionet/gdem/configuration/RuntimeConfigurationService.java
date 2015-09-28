package eionet.gdem.configuration;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * Implementation of {@link eionet.gdem.configuration.ConfigurationService}.This
 * is a centralized service, it is aware of all property values of the
 * application.If any object needs to interact with an application resource, it
 * should this class.
 */
public final class RuntimeConfigurationService implements ConfigurationService {

    private final Map<String, String> resources;
    private final Map<String, String> cachedResources;
    private SystemPropertyProvider systemPropertyProvider;

    public RuntimeConfigurationService(Map<String, String> resources) {
        this(resources, new SystemPropertyProviderImpl());
    }

    public RuntimeConfigurationService(Map<String, String> resources, SystemPropertyProvider systemPropertyProvider) {
        this.resources = resources;
        this.systemPropertyProvider = systemPropertyProvider;
        this.cachedResources = new HashMap<String, String>();
    }

    public void setSystemPropertiesProvider(SystemPropertyProvider systemPropertyProvider) {
        this.systemPropertyProvider = systemPropertyProvider;
    }

    /**
     * Returns a property value.
     *
     * @param key Property key
     * @return Property value
     * @throws UnresolvedPropertyException
     */
    @Override
    public String get(String key) throws UnresolvedPropertyException {
        String value = cachedResources.get(key);
        if (value == null) {
            value = addToCache(key);
        }
        return value;
    }

    /**
     * Resovles and adds the Property key to internal cache.
     *
     * @param key
     * @return The resolved value
     * @throws UnresolvedPropertyException
     */
    String addToCache(String key) throws UnresolvedPropertyException {
        String value = resolve(key);
        cachedResources.put(key, value);
        return value;
    }

    /**
     * Resolves the property value for a given property key.
     *
     * @param key
     * @return The resolved value
     * @throws UnresolvedPropertyException
     */
    String resolve(String key) throws UnresolvedPropertyException {
        String value = traverse(new ArrayDeque<String>(), key);
        return value;

    }

    /**
     * Resolves all the the placeholder names that it finds on the value side of
     * the property and returns the resolved value.
     *
     * @param visited
     * @param placeholder
     * @return
     * @throws UnresolvedPropertyException
     */
    String traverse(Deque<String> visited, String placeholder) throws UnresolvedPropertyException {
        // TODO(ezyk): Remove recursion, implement with iteration
        visited.push(placeholder);

        String value = resources.get(placeholder);
        PlaceholderNameProvider placeholderProvider = new PlaceholderNameProviderImpl();
        Set<String> placeholders = placeholderProvider.extract(value);
        if (placeholders.isEmpty()) {
            // system properties override local properties
            if (systemPropertyProvider != null && systemPropertyProvider.get(placeholder) != null) {
                value = systemPropertyProvider.get(placeholder);
            }
            if (value == null) {
                throw new UnresolvedPropertyException("Could not resolve placeholder " + placeholder);
            }
        }
        for (String item : placeholders) {
            if (visited.contains(item)) {
                throw new UnresolvedPropertyException("Invalid circular reference. Could not resolve placeholder " + item);
            }
            visited.push(item);
            String resolved = traverse(visited, item);
            value = StringUtils.replace(value, "${" + item + "}", resolved);

        }
        return value;
    }

    /**
     * Iterates all resources, resolves all placeholder names and stores their
     * value to the internal cache.
     */
    @Override
    public void cacheAll() {
        invalidate();
        for (String key : resources.keySet()) {
            try {
                get(key);
            } catch (UnresolvedPropertyException ex) {
                Logger.getLogger(RuntimeConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Clear the cache
     */
    @Override
    public void invalidate() {
        cachedResources.clear();
    }

    /**
     * Helper method to provide the all properties to
     * {@link eionet.gdem.configuration.ConfigurationFactory}
     *
     * @return
     */
    Map<String, String> getResources() {
        return resources;
    }

    /**
     * Helper method to provide the all properties to provide access to cache.
     *
     * @return
     */
    Map<String, String> getCachedResources() {
        return cachedResources;
    }

}
