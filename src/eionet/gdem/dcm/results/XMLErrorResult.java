/*
 * Created on 12.02.2008
 */
package eionet.gdem.dcm.results;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.xml.sax.helpers.AttributesImpl;

/**
 * Create XML result for remote method error
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS
 */

public class XMLErrorResult extends XMLResultStrategy {

	private static final String ERROR_TAG = "error-message";
	private static final String METHOD_TAG = "method";
	private static final String PARAMETERS_TAG = "parameters";
	private static final String PARAMETER_TAG = "parameter";

	private int status = HttpServletResponse.SC_BAD_REQUEST;
	private String error = null;
	private String method = null;
	private Map requestParamters = null;

	/**
	 * Set the request method name that was called
	 * @param m
	 */
	public void setMethod(String m) {
		method = m;
	}

	/**
	 * Set error message that is displayed to the client
	 * @param e
	 */
	public void setError(String e) {
		error = e;
	}

	/**
	 * Set HTTP Status code to 400
	 */
	public void setBadRequestStatus() {
		this.status = HttpServletResponse.SC_BAD_REQUEST;
	}

	/**
	 * Set HTTP Status code to 401
	 */
	public void setUnauthorizedStatus() {
		this.status = HttpServletResponse.SC_UNAUTHORIZED;
	}

	/**
	 * Get HTTP Status code
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Get error message
	 * @return
	 */
	public String getError() {
		if (error == null)
			return "Unknown error";
		else
			return error;
	}

	/**
	 * Set request parameters
	 * @param requestParamters
	 */
	public void setRequestParamters(Map requestParamters) {
		this.requestParamters = requestParamters;
	}

	/**
	 * write XML elements
	 */
	protected void writeElements() throws Exception {
		writeSimpleElement(ERROR_TAG, getError());
		writeSimpleElement(METHOD_TAG, (method == null) ? "" : method);
		writeRequestParamters();
	}

	/**
	 * Write request parameters as XML
	 * @throws Exception
	 */
	protected void writeRequestParamters() throws Exception {

		hd.startElement("", "", PARAMETERS_TAG, atts);
		if (requestParamters != null) {
			Iterator iterKeys = requestParamters.keySet().iterator();
			while (iterKeys.hasNext()) {
				String key = (String) iterKeys.next();
				String value = "";
				if ((requestParamters.get(key)) instanceof String[]) {
					value = (String) ((Object[]) requestParamters.get(key))[0];
				} else if ((requestParamters.get(key)) instanceof String) {
					value = (String) requestParamters.get(key);
				} else {
					value = requestParamters.get(key).toString();
				}

				AttributesImpl attrs = new AttributesImpl();
				attrs.addAttribute("", "", "name", "String", key);

				writeSimpleElement(PARAMETER_TAG, (value == null) ? "" : value,
						attrs);
			}
		}
		hd.endElement("", "", PARAMETERS_TAG);

	}
	/**
	 * Set HTTP Status code
	 * @param status2
	 */
	public void setStatus(int status2) {
		this.status=status2;
	}

}