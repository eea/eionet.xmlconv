/*
 * Created on 21.04.2008
 */
package eionet.gdem.test.mocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import servletunit.HttpServletRequestSimulator;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * MockStrutsHttpRequestSimulator
 */

public class MockStrutsMultipartRequestSimulator extends HttpServletRequestSimulator implements HttpServletRequest{

	ServletInputStream inputStream = null;
	int contentLength = 0;
	
	
	private String contentType = "multipart/form-data; ";
	//private String contentType = "Content-Type=application/x-www-form-urlencoded";
	private static String boundary = "---------------------------7d226f700d0";
	private static final int BUFF_SIZE = 1024;
	private static final byte[] buffer = new byte[BUFF_SIZE];

	public MockStrutsMultipartRequestSimulator(ServletContext context) {
		super(context);
		super.setContentType(contentType.concat("boundary=").concat(boundary));
	}
	public MockStrutsMultipartRequestSimulator(ServletContext context, HttpServletRequestSimulator req) {
		super(context);
	}


	public String getContentType() {
		return contentType.concat("boundary=").concat(boundary);
	}

	public void writeFile(String fileItemParam, String file,
			String fileContentType) throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(("--" + boundary + "\r\n").getBytes());
		writeParams(out);
		writeFile(out, fileItemParam, file, fileContentType);
		ByteArrayInputStream ins = new ByteArrayInputStream(out.toByteArray());
		setServletInputStream(ins);
		setContentLength(out.size());
	}
	public ServletInputStream getInputStream() throws IOException {
		return inputStream;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setServletInputStream(ByteArrayInputStream ins){
		inputStream = new MockServletInputStream(ins);
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	private void writeParams(ByteArrayOutputStream out) throws Exception {

		Map requestParameters = getParameterMap();
		if (requestParameters != null) {
			Iterator iterKeys = requestParameters.keySet().iterator();
			while (iterKeys.hasNext()) {
				String key = (String) iterKeys.next();
				String value = "";
				if ((requestParameters.get(key)) instanceof String[]) {
					value = (String) ((Object[]) requestParameters.get(key))[0];
				} else if ((requestParameters.get(key)) instanceof String) {
					value = (String) requestParameters.get(key);
				} else {
					value = requestParameters.get(key).toString();
				}
				out.write((new StringBuilder(
						"Content-disposition: form-data; name=\"").append(key)
						.append("\"\r\n\r\n")).toString().getBytes());
				out.write(value.getBytes());
				out.write(("\r\n" + "--" + boundary + "\r\n").getBytes());
			}
		}
	}

	private void writeFile(ByteArrayOutputStream out, String fileItemParam,
			String name, String contentType) throws Exception {

		File file = new File(name);
		FileInputStream fis = new FileInputStream(file);
		try {
			out.write("Content-disposition: form-data; name=\"".concat(
					fileItemParam).concat("\"; filename=\"").concat(
					file.getName()).concat("\"\r\n").getBytes());
			out.write("Content-type: ".concat(contentType).concat("\r\n\r\n")
					.getBytes());

			int i = 0;
			while (true) {
				synchronized (buffer) {
					int amountRead = fis.read(buffer);
					if (amountRead == -1) {
						break;
					}
					out.write(buffer, 0, amountRead);
				}
			}
			out.write(("\r\n--" + boundary + "--\r\n").getBytes());
		} finally {
			fis.close();
		}
	}

}