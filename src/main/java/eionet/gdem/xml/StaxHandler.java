package eionet.gdem.xml;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import org.xml.sax.InputSource;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.sax.SAXSource;
import java.io.OutputStream;

/**
 * SAX Handler for XML files.
 * @author George Sofianos
 */
public class StaxHandler implements XmlHandler {
    @Override
    public boolean parseString(String xml) {
        return false;
    }

    @Override
    public boolean parseString(String xml, OutputStream out) {
        try {
            SAXSource source = new SAXSource(new InputSource(xml));
            SMInputFactory inf = new SMInputFactory(new WstxInputFactory());
            SMOutputFactory outf = new SMOutputFactory(new WstxOutputFactory());
            SMOutputDocument doc = outf.createOutputDocument(out);
            SMOutputElement elem = doc.addElement("test");
            elem.addCharacters("test");
            doc.closeRoot();
        } catch (XMLStreamException e) {
            return false;
        }
        return false;
    }
}
