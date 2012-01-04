package eionet.gdem.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public interface IXmlSerializer {

    ByteArrayOutputStream serializeToOutStream() throws XmlException;

    void serializeToFs(String fullFileName) throws XmlException;

    ByteArrayInputStream serializeToInStream() throws XmlException;

    String serializeToString() throws XmlException;

}
