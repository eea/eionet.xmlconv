package eionet.gdem.logging;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author George Sofianos
 */
public final class Markers {
    public static Marker fatal = MarkerFactory.getMarker("FATAL");

    /**
     * Private constructor
     */
    private Markers() {
        // do nothing
    }
}
