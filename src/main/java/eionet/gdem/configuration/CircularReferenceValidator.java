package eionet.gdem.configuration;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

/**
 * Object that detects circular references defined in
 * {@link java.util.Properties} object.Throws
 * {@link eionet.gdem.configuration.ConfigurationException} if it finds a
 * circular reference.
 *
 */
public class CircularReferenceValidator implements ConfigurationValidator {

    @Override
    public void validate(Map<String, String> resources) throws ConfigurationException {
        for (String key : resources.keySet()) {
            String value = resources.get(key);
            try {
                traverse(resources, new ArrayDeque<String>(), key);
            } catch (CircularReferenceException cre) {
                throw new ConfigurationException("Circular reference caused configuration error for placeholder ${" + key + "} : " + cre.getMessage());
            }
        }
    }

    /**
     * Simple DFS traversal in order to detect Circular references.
     *
     * @param visited The placeholder nodes we have visited so far
     * @param placeholder The placeholder we are currently checking for
     * dependencies
     * @throws CircularReferenceException
     */
    void traverse(Map<String, String> resources, Deque<String> visited, String placeholder) throws CircularReferenceException {
        // TODO(ezyk): Remove recursion, implement with iteration
        visited.push(placeholder);
        String value = resources.get(placeholder);
        if (value == null && !resources.containsKey(placeholder)) {
            return;
        }
        PlaceholderNameProvider placeholderProvider = new PlaceholderNameProviderImpl();
        Set<String> placeholders = placeholderProvider.extract(value);
        for (String item : placeholders) {
            if (visited.contains(item)) {
                throw new CircularReferenceException("Circular reference for placeholder ${" + item + "}");
            }
            visited.push(item);
            traverse(resources, visited, item);
        }
    }

}
