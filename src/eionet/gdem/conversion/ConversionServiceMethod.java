/*
 * Created on 20.02.2008
 */
package eionet.gdem.conversion;

import java.io.File;

import eionet.gdem.Properties;
import eionet.gdem.dcm.results.HttpResultWrapper;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ConversionServiceMethod
 */

public abstract class ConversionServiceMethod {


	public static final String DEFAULT_CONTENT_TYPE = "text/plain";

	public static final String DEFAULT_FILE_EXT = "txt";

	public static final String DEFAULT_FILE_NAME = "converted";

	private String xslFolder;

	private String tmpFolder;

	private String ticket = null;

	private boolean trustedMode = true;// false for web clients
	
	private boolean isHttpRequest = false;
	
	private HttpResultWrapper httpResult = null;

	public ConversionServiceMethod() {
		xslFolder = Properties.xslFolder + File.separatorChar; // props.getString("xsl.folder");
		tmpFolder = Properties.tmpFolder + File.separatorChar; // props.getString("tmp.folder");
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public boolean isTrustedMode() {
		return trustedMode;
	}

	public void setTrustedMode(boolean trustedMode) {
		this.trustedMode = trustedMode;
	}

	public boolean isHttpRequest() {
		return isHttpRequest;
	}

	public HttpResultWrapper getHttpResult() {
		return httpResult;
	}

	public void setHttpResult(HttpResultWrapper httpResult) {
		isHttpRequest=true;
		this.httpResult = httpResult;
	}

	public String getXslFolder() {
		return xslFolder;
	}

	public String getTmpFolder() {
		return tmpFolder;
	}

}
