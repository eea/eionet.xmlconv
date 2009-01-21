/*
 * Created on 19.03.2008
 */
package eionet.gdem.test.mocks;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * MockHttpMultipartServletRequest
 */

public class MockServletRequest  implements HttpServletRequest {

	ServletInputStream inputStream = null;
	String contentType = null;
	int contentLength = 0;
	HashMap headers = new HashMap();
	Map parameterMap = new HashMap();
	Locale locale = Locale.getDefault();
	int bufferSize = 0;
	String charEncoding = "UTF-8";
	
	String scheme = null;
	String serverName = null;
	int serverPort = 0;
	String contextPath= null;
	String requestURL = null;
	String queryString = null;
	
	HttpSession session;

	public String getAuthType() {
		throw new UnsupportedOperationException();
	}

	public String getContextPath() {
		return contextPath;
	}

	public Cookie[] getCookies() {
		throw new UnsupportedOperationException();
	}

	public long getDateHeader(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getHeader(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Enumeration getHeaderNames() {
		throw new UnsupportedOperationException();
	}

	public Enumeration getHeaders(String arg0) {
		throw new UnsupportedOperationException();
	}

	public int getIntHeader(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getMethod() {
		throw new UnsupportedOperationException();
	}

	public String getPathInfo() {
		throw new UnsupportedOperationException();
	}

	public String getPathTranslated() {
		throw new UnsupportedOperationException();
	}

	public String getQueryString() {
		return queryString;
	}

	public String getRemoteUser() {
		throw new UnsupportedOperationException();
	}

	public String getRequestURI() {
		throw new UnsupportedOperationException();
	}

	public StringBuffer getRequestURL() {
		return new StringBuffer(requestURL);
	}

	public String getRequestedSessionId() {
		throw new UnsupportedOperationException();
	}

	public String getServletPath() {
		throw new UnsupportedOperationException();
	}

	public HttpSession getSession() {
		return session;
	}

	public HttpSession getSession(boolean arg0) {
		return session;
	}

	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException();
	}

	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException();
	}

	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	public boolean isRequestedSessionIdValid() {
		return false;
	}

	public boolean isUserInRole(String arg0) {
		return false;
	}

	public Object getAttribute(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Enumeration getAttributeNames() {
		throw new UnsupportedOperationException();
	}

	public String getCharacterEncoding() {
		return charEncoding;
	}

	public int getContentLength() {
		return contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public ServletInputStream getInputStream() throws IOException {
		return inputStream;
	}

	public String getLocalAddr() {
		throw new UnsupportedOperationException();
	}

	public String getLocalName() {
		return locale.getDisplayName();
	}

	public int getLocalPort() {
		return 0;
	}

	public Locale getLocale() {
		return locale;
	}

	public Enumeration getLocales() {
		throw new UnsupportedOperationException();
	}

	public String getParameter(String arg0) {
		return (String)parameterMap.get(arg0);
	}

	public Map getParameterMap() {
		return parameterMap;
	}

	public Enumeration getParameterNames() {
		throw new UnsupportedOperationException();
	}

	public String[] getParameterValues(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getProtocol() {
		throw new UnsupportedOperationException();
	}

	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException();
	}

	public String getRealPath(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getRemoteAddr() {
		throw new UnsupportedOperationException();
	}

	public String getRemoteHost() {
		throw new UnsupportedOperationException();
	}

	public int getRemotePort() {
		throw new UnsupportedOperationException();
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getScheme() {
		return scheme;
	}

	public String getServerName() {
		return serverName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public boolean isSecure() {
		return false;
	}

	public void removeAttribute(String arg0) {
	}

	public void setAttribute(String arg0, Object arg1) {
	}

	public void setCharacterEncoding(String arg0){
		this.charEncoding=arg0;
	}
	//Methods needed for testing
	public void setParameterMap(Map paramsMap) {
		parameterMap=paramsMap;
	}
	public void setServletInputStream(ByteArrayInputStream ins){
		inputStream = new MockServletInputStream(ins);
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
}
