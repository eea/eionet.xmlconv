package eionet.gdem.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public interface IXmlSerializer {
	
	public ByteArrayOutputStream serializeToOutStream() throws XmlException;
	public void serializeToFs(String fullFileName) throws XmlException;
	public ByteArrayInputStream serializeToInStream() throws XmlException;

}
