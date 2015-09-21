package eionet.gdem.configuration;

import com.sun.javafx.binding.StringFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

/**
 * Object that detects unresolved placeholders.Throws
 * {@link eionet.gdem.configuration.ConfigurationException} if it finds a
 * placeholder that can not be resolved.
 *
 */
public class UnResolvedPropertyValidator implements ConfigurationValidator {

    private final Map<String, String> resources;
    private final SystemPropertyProvider systemPropertyProvider;

    public UnResolvedPropertyValidator(Map<String, String> resources, SystemPropertyProvider systemPropertyProvider) {
        this.resources = resources;
        this.systemPropertyProvider = systemPropertyProvider;
    }

    /**
     * Validation method that traverse all {@link java.util.Properties} keys in
     * order to check that they can be resolved.If a unresolved placeholder is
     * found, a {@link eionet.gdem.configuration.ConfigurationException} is
     * thrown in order to mark the application configuration as invalid.
     *
     * @throws ConfigurationException
     */
    @Override
    public void validate() throws ConfigurationException {
        for (String key : resources.keySet()) {
            try {
                traverse(new ArrayDeque<String>(), key);
            } catch (UnResolvedPropertyException ure) {
                throw new ConfigurationException("Configuration error for placeholder ${" + key + "}: " + ure.getMessage());
            }
        }
    }

    /**
     * Simple DFS traversal in order to detect Unresolved placeholders.
     *
     * @param visited The placeholder nodes we have visited so far
     * @param placeholder The placeholder we are currently checking to resolve
     * @throws UnResolvedPropertyException
     */
    void traverse(Deque<String> visited, String placeholder) throws UnResolvedPropertyException {
        // TODO(ezyk): Remove recursion, implement with iteration
        visited.push(placeholder);
        String value = resources.get(placeholder);
        PlaceholderNameProvider placeholderProvider = new PlaceholderNameProviderImpl();
        Set<String> placeholders = placeholderProvider.extract(value);
        if (value == null && placeholders.isEmpty() && systemPropertyProvider != null) {
            value = systemPropertyProvider.get(placeholder);
            if (value == null) {
                throw new UnResolvedPropertyException("Configuration error for placeholder ${" + placeholder + "}: No value was found.");
            }
        }
        for (String item : placeholders) {
            if (visited.contains(item)) {
                throw new UnResolvedPropertyException("Configuration error for placeholder ${" + item + "}: Circular reference.");
            }
            visited.push(item);
            traverse(visited, item);
        }
    }

}
