package eionet.gdem.configuration;

import java.util.Set;

/**
 *
 * Classes that implement this interface can extract placeholder names from
 * Strings.
 */
public interface PlaceholderNameProvider {
    
    Set<String> extract(String value);
}
