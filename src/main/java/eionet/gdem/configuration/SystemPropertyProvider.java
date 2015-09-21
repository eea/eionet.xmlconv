package eionet.gdem.configuration;

/**
 *
 * Interface that provides System Properties to the client.
 */
public interface SystemPropertyProvider {
    String get(String key);
}
