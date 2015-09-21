/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    private final Map<String, String> resources;

    public CircularReferenceValidator(Map<String, String> resources) {
        this.resources = resources;
    }

    /**
     * Validation method that traverse all {@link java.util.Properties} keys in
     * order to find a circular reference.If a Circular reference is found a
     * {@link eionet.gdem.configuration.ConfigurationException} is thrown in
     * order to mark the application configuration as invalid.
     *
     * @throws ConfigurationException
     */
    @Override
    public void validate() throws ConfigurationException {
        for (String key : resources.keySet()) {
            String value = resources.get(key);
            try {
                traverse(new ArrayDeque<String>(), key);
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
    void traverse(Deque<String> visited, String placeholder) throws CircularReferenceException {
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
                throw new CircularReferenceException("Circular reference for placeholder ${"+item+"}");
            }
            visited.push(item);
            traverse(visited, item);
        }
    }

}
