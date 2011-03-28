/*
 * Created on 21.01.2009
 */
package eionet.gdem.test.mocks;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * MockSession
 */

public class MockHttpSession implements HttpSession {

    HashMap attributes;
    ServletContext servletContext;

    public MockHttpSession(){
        attributes = new HashMap();
    }
    public Object getAttribute(String s) {
        if (s!=null && attributes.containsKey(s))
            return attributes.get(s);

        return null;
    }

    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException();

    }

    public long getCreationTime() {
        throw new UnsupportedOperationException();
    }

    public String getId() {
        throw new UnsupportedOperationException();
    }

    public long getLastAccessedTime() {
        throw new UnsupportedOperationException();
    }

    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException();
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException();
    }

    public Object getValue(String s) {
        throw new UnsupportedOperationException();
    }

    public String[] getValueNames() {
        throw new UnsupportedOperationException();
    }

    public void invalidate() {
        throw new UnsupportedOperationException();
    }

    public boolean isNew() {
        throw new UnsupportedOperationException();
    }

    public void putValue(String s, Object obj) {
        throw new UnsupportedOperationException();
    }

    public void removeAttribute(String s) {
        if(s!=null && attributes.containsKey(s));
            attributes.remove(s);
    }

    public void removeValue(String s) {
        throw new UnsupportedOperationException();
    }

    public void setAttribute(String s, Object obj) {
        if(s!=null)
            attributes.put(s, obj);
    }

    public void setMaxInactiveInterval(int i) {
        throw new UnsupportedOperationException();
    }
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
