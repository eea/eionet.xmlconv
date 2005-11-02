package eionet.gdem.dcm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.InputSource;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.cache.MemoryCache;
import eionet.gdem.utils.xml.XSLTransformer;

public class XslGenerator {

	public static XSLTransformer transform = new XSLTransformer();
	public static MemoryCache MemCache = new MemoryCache(10000, 10);


	public static ByteArrayInputStream convertXML(String xmlURL, String conversionURL) throws GDEMException, Exception {
		String cacheId = xmlURL + "_" + conversionURL;
		byte[] result = (byte[]) MemCache.getContent(cacheId);
		if (result == null) {
			result = makeDynamicXSL(xmlURL, conversionURL);
			MemCache.put(cacheId, result, Integer.MAX_VALUE);
		}
		return new ByteArrayInputStream(result);
	}


	private static byte[] makeDynamicXSL(String sourceURL, String xslFile) throws GDEMException {
		InputFile src = null;
		byte[] result = null;
		try {
			src = new InputFile(sourceURL);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Map parameters = new HashMap();
			parameters.put("dd_domain", Properties.ddURL);
			transform.transform(xslFile, new InputSource(src.getSrcInputStream()), os, parameters);
			result = os.toByteArray();
		} catch (MalformedURLException mfe) {
			throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
		} catch (IOException ioe) {
			throw new GDEMException("Error opening URL " + ioe.toString(), ioe);
		} catch (Exception e) {
			throw new GDEMException("Error converting: " + e.toString(), e);
		} finally {
			try {
				if (src != null) src.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

}
