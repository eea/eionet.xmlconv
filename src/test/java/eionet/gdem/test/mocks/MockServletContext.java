/*
 * Created on 21.01.2009
 */
package eionet.gdem.test.mocks;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;

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

    @Override
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
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

    @Override
    public ServletRegistration.Dynamic addServlet(String s, String s1) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
        return null;
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> aClass) throws ServletException {
        return null;
    }

    @Override
    public ServletRegistration getServletRegistration(String s) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, String s1) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
        return null;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> aClass) throws ServletException {
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String s) {
        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> set) {

    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    @Override
    public void addListener(String s) {

    }

    @Override
    public <T extends EventListener> void addListener(T t) {

    }

    @Override
    public void addListener(Class<? extends EventListener> aClass) {

    }

    @Override
    public <T extends EventListener> T createListener(Class<T> aClass) throws ServletException {
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void declareRoles(String... strings) {

    }

    @Override
    public String getVirtualServerName() {
        return null;
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
    @Override
    public boolean setInitParameter(String s, String s2) {
        if (s != null)
            initParameters.put(s, s2);
        return true;
    }

    @Override
    public String getContextPath() {
        return null;
    }

}
