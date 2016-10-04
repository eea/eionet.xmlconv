package eionet.gdem.dcm.remote;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 *
 * @author George Sofianos
 */
public class ListConversionsResultTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void checkForNamespacesTest() throws Exception {
        ListConversionsResult result = new ListConversionsResult();
        StringWriter writer = new StringWriter();
        StreamResult stream = new StreamResult(writer);
        result.writeXML(stream);
    }

}