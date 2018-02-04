/*
 * Created on 21.04.2008
 */
package eionet.gdem.test.mocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import servletunit.HttpServletRequestSimulator;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS MockStrutsHttpRequestSimulator
 */

public class MockStrutsMultipartRequestSimulator extends HttpServletRequestSimulator implements HttpServletRequest {

    ServletInputStream inputStream = null;
    int contentLength = 0;

    private String contentType = "multipart/form-data; ";
    // private String contentType = "Content-Type=application/x-www-form-urlencoded";
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

    public void writeFile(String fileItemParam, String file, String fileContentType) throws Exception {

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

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    public int getContentLength() {
        return contentLength;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    public void setServletInputStream(ByteArrayInputStream ins) {
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
                out.write((new StringBuilder("Content-disposition: form-data; name=\"").append(key).append("\"\r\n\r\n"))
                        .toString().getBytes());
                out.write(value.getBytes());
                out.write(("\r\n" + "--" + boundary + "\r\n").getBytes());
            }
        }
    }

    private void writeFile(ByteArrayOutputStream out, String fileItemParam, String name, String contentType) throws Exception {

        File file = new File(name);
        FileInputStream fis = new FileInputStream(file);
        try {
            out.write("Content-disposition: form-data; name=\"".concat(fileItemParam).concat("\"; filename=\"")
                    .concat(file.getName()).concat("\"\r\n").getBytes());
            out.write("Content-type: ".concat(contentType).concat("\r\n\r\n").getBytes());

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

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }
}
