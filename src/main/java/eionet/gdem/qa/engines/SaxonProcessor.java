package eionet.gdem.qa.engines;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;

import javax.xml.transform.stream.StreamSource;

/**
 * Saxon Processor singleton.
 * @author George Sofianos
 */
public final class SaxonProcessor {
    private static Processor processor;

    /**
     * Creating singleton with exceptions
     * {@link http://stackoverflow.com/questions/2284502/singleton-and-exception}
     */
    static {
        try {
            processor = new Processor(new StreamSource(SaxonProcessor.class.getResourceAsStream("/saxon-config.xml")));
        } catch (SaxonApiException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    /**
     * Default constructor
     */
    private SaxonProcessor() {
        // do nothing
    }

    public static Processor getProcessor() {
        return processor;
    }

    public static void setProcessor(Processor processor) {
        SaxonProcessor.processor = processor;
    }
}
