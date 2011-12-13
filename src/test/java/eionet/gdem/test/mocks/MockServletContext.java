/*
 * Created on 21.01.2009
 */
package eionet.gdem.test.mocks;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS MockSessionContext
 */

public class MockServletContext implements ServletContext {

    HashMap attributes;
    HashMap initParameters;

    public MockServletContext() {
        attributes = new HashMap();
        initParameters = new HashMap();
    }

    public Object getAttribute(String s) {
        if (s != null && attributes.containsKey(s))
            return attributes.get(s);

        return null;
    }

    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    public ServletContext getContext(String s) {
        throw new UnsupportedOperationException();
    }

    public String getInitParameter(String s) {
        if (s != null && initParameters.containsKey(s))
            return (String) initParameters.get(s);

        return null;
    }

    public Enumeration getInitParameterNames() {
        throw new UnsupportedOperationException();
    }

    public int getMajorVersion() {
        throw new UnsupportedOperationException();
    }

    public String getMimeType(String s) {
        throw new UnsupportedOperationException();
    }

    public int getMinorVersion() {
        throw new UnsupportedOperationException();
    }

    public RequestDispatcher getNamedDispatcher(String s) {
        throw new UnsupportedOperationException();
    }

    public String getRealPath(String s) {
        throw new UnsupportedOperationException();
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        throw new UnsupportedOperationException();
    }

    public URL getResource(String s) throws MalformedURLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getResourceAsStream(String s) {
        throw new UnsupportedOperationException();
    }

    public Set getResourcePaths(String s) {
        throw new UnsupportedOperationException();
    }

    public String getServerInfo() {
        throw new UnsupportedOperationException();
    }

    public Servlet getServlet(String s) throws ServletException {
        throw new UnsupportedOperationException();
    }

    public String getServletContextName() {
        throw new UnsupportedOperationException();
    }

    public Enumeration getServletNames() {
        throw new UnsupportedOperationException();
    }

    public Enumeration getServlets() {
        throw new UnsupportedOperationException();
    }

    public void log(String s) {
        throw new UnsupportedOperationException();
    }

    public void log(Exception exception, String s) {
        throw new UnsupportedOperationException();
    }

    public void log(String s, Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    public void removeAttribute(String s) {
        if (s != null && attributes.containsKey(s))
            ;
        attributes.remove(s);
    }

    public void setAttribute(String s, Object obj) {
        if (s != null)
            attributes.put(s, obj);
    }

    public void setInitParameter(String s, String s2) {
        if (s != null)
            initParameters.put(s, s2);
    }

    @Override
    public String getContextPath() {
        return null;
    }

}
