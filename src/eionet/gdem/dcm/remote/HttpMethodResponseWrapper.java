/*
 * Created on 13.02.2008
 */
package eionet.gdem.dcm.remote;

import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Custom implementation of HttpServletResponseWrapper. Adds writeXML
 * functionality.
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS
 */

public class HttpMethodResponseWrapper extends HttpServletResponseWrapper {

	private static final String DEFAULT_ENCODING = "UTF-8";

	public HttpMethodResponseWrapper(HttpServletResponse response) {
		super(response);
		setCharacterEncoding(DEFAULT_ENCODING);
	}

	/**
	 * Writes Content-Disposition row into response header
	 * 
	 * @param contentDisposition
	 */
	public void setContentDisposition(String contentDisposition) {
		setHeader("Content-Disposition", "inline;filename=\""
				+ contentDisposition + "\"");
	}

	/**
	 * Configures the response header and writes the XML result into servlet
	 * output stream.
	 * 
	 * @param xmlResult
	 * @throws Exception
	 */
	public void flushXML(XMLResultStrategy xmlResult) throws Exception {
		setStatus(xmlResult.getStatus());
		setContentType(xmlResult.getContentType());
		if (xmlResult.getContentLength() > 0)
			setContentLength(xmlResult.getContentLength());
		xmlResult.writeXML(getOutputStream());
		flush();
	}

	/**
	 * Creates XMLErrorResult object with given parameters and writes the XML
	 * into servlet output
	 * 
	 * @param status
	 *            HTTP Status Code
	 * @param errMessage
	 *            Error message
	 * @param method
	 *            Method name that was called over HTTP
	 * @param params
	 *            Request parameters
	 * @throws Exception
	 */
	public void flushXMLError(int status, String errMessage, String method,
			Map params) throws Exception {
		XMLErrorResult errorResult = new XMLErrorResult();
		errorResult.setError(errMessage);
		errorResult.setMethod(method);
		errorResult.setStatus(status);
		errorResult.setRequestParamters(params);
		errorResult.writeXML();
		flushXML(errorResult);
	}

	/**
	 * Flush and close the servlet outputstream.
	 * 
	 * @throws Exception
	 */
	public void flush() throws Exception {
		OutputStream os = getOutputStream();
		os.flush();
		os.close();
	}

}
