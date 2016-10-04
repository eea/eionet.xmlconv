package eionet.gdem.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * XML Serializer Interface.
 */
public interface XmlSerializer {

    /**
     * Serializes XML to input stream.
     * @return OutputStream
     * @throws XmlException If an error occurs.
     */
    ByteArrayOutputStream serializeToOutStream() throws XmlException;

    /**
     * Serializes to file system
     * @param fullFileName File name
     * @throws XmlException If an error occurs.
     */
    void serializeToFs(String fullFileName) throws XmlException;

    /**
     * Serializes to input stream.
     * @return InputStream
     * @throws XmlException If an error occurs.
     */
    ByteArrayInputStream serializeToInStream() throws XmlException;

    /**
     * Serializes to String
     * @return String result
     * @throws XmlException If an error occurs.
     */
    String serializeToString() throws XmlException;

}
