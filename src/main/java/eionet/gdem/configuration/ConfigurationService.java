package eionet.gdem.configuration;

/**
 * Interface that let the client interact with the Configuration Service
 *
 */
public interface ConfigurationService {
    
    /**
     * Returns the proper resolved value to the client.If the configuration
     * service can't resolve the value a {@link eionet.gdem.configuration.UnResolvedPropertyException}
     * exception is thrown.
     * 
     * @param key The property key
     * @return The resolved value
     * @throws UnResolvedPropertyException Missing property value
     */
    String get(String key) throws UnResolvedPropertyException;
    void invalidate();
    void cacheAll();
}
