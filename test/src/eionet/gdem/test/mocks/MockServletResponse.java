/*
 * Created on 18.03.2008
 */
package eionet.gdem.test.mocks;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * The class mocks HttpServletResponse class to be able to test HTTP request and response.
 * @author Enriko Käsper, TietoEnator Estonia AS
 * MockServletResponse
 */

public class MockServletResponse implements HttpServletResponse {

	PrintWriter writer = null;
	ServletOutputStream outputStream = null;
	int statusCode = 200;
	String statusString = "OK";
	String contentType = "text/html";
	int contentLength = 0;
	HashMap headers = new HashMap();
	List cookies = new ArrayList();
	Locale locale = Locale.getDefault();
	int bufferSize = 0;
	String charEncoding = "UTF-8";

	public MockServletResponse() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		this.outputStream = new MockServletOutputStream(output);
		this.writer = new PrintWriter(outputStream);
	}

	public MockServletResponse(PrintWriter writer,
			ServletOutputStream outputStream) {
		this.writer = writer;
		this.outputStream = outputStream;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public ServletOutputStream getOutputStream() {
		return outputStream;
	}

	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	public void addDateHeader(String name, long value) {
		headers.put(name, String.valueOf(value));
	}

	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	public void addIntHeader(String name, int value) {
		headers.put(name, String.valueOf(value));
	}

	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}

	/**
	 * @deprecated
	 */
	public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}

	public String encodeRedirectURL(String url) {
		return url;
	}

	/**
	 * @deprecated
	 */
	public String encodeUrl(String url) {
		return encodeURL(url);
	}

	public String encodeURL(String url) {
		return url;
	}

	public void sendError(int status) {
		throw new UnsupportedOperationException();
	}

	public void sendError(int status, String message) {
		throw new UnsupportedOperationException();
	}

	public void sendRedirect(String location) {
		throw new UnsupportedOperationException();
	}

	public void setDateHeader(String name, long value) {
		headers.put(name, String.valueOf(value));
	}

	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	public void setIntHeader(String name, int value) {
		headers.put(name, String.valueOf(value));
	}

	public void setStatus(int status) {
		this.statusCode = status;
	}

	/**
	 * @deprecated
	 */
	public void setStatus(int status, String message) {
		this.statusCode = status;
		this.statusString = message;
	}

	public void flushBuffer() {
		throw new UnsupportedOperationException();
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public String getCharacterEncoding() {
		return charEncoding;
	}

	public Locale getLocale() {
		return locale;
	}

	public boolean isCommitted() {
		throw new UnsupportedOperationException();
	}

	public void reset() {
		throw new UnsupportedOperationException();
	}

	public void resetBuffer() {
		throw new UnsupportedOperationException();
	}

	public void setBufferSize(int size) {
		throw new UnsupportedOperationException();
	}

	public void setContentLength(int length) {
		this.contentLength = length;
	}

	public void setContentType(String type) {
		this.contentType = type;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getContentType() {
		return contentType;
	}

	public void setCharacterEncoding(String s) {
		this.charEncoding = s;
	}

	public String getHeader(String key) {
		if(headers.containsKey(key))
			return (String)headers.get(key);
		else
			return null;
	}
	public int getStatus() {
		return statusCode;
	}
}
