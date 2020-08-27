package eionet.gdem.notifications;

import org.apache.xmlrpc.XmlRpcClient;

import java.net.MalformedURLException;
import java.util.Vector;

public class XmlRpcClientMock extends XmlRpcClient {

    /** "OK" string. */
    private static final String OK = "OK";

    /** User for basic authentication. */
    private String user;

    /** Password for basic authentication. */
    private String password;

    /** The name of the last remote method called via this client mock. */
    private String lastCalledMethod;

    /** The parameters passed to the last remote method called via this client mock. */
    private Vector lastCalledParams;

    /** Will be set to true if {@link #execute(String, Vector)} gets called at least once. */
    private boolean executeCalled;

    /**
     * Calls {@link XmlRpcClient#XmlRpcClient(String)} with the given URL string.
     * @param url The given URL string.
     * @throws MalformedURLException O fthe URL is malformed.
     */
    public XmlRpcClientMock(String url) throws MalformedURLException {
        super(url);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.xmlrpc.XmlRpcClient#setBasicAuthentication(java.lang.String, java.lang.String)
     */
    @Override
    public void setBasicAuthentication(String user, String password) {
        this.user = user;
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.xmlrpc.XmlRpcClient#execute(java.lang.String, java.util.Vector)
     */
    @Override
    public Object execute(String method, Vector params) {
        executeCalled = true;
        lastCalledMethod = method;
        lastCalledParams = params;
        return OK;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the lastCalledMethod
     */
    public String getLastCalledMethod() {
        return lastCalledMethod;
    }

    /**
     * @return the lastCalledParams
     */
    public Vector getLastCalledParams() {
        return lastCalledParams;
    }

    /**
     * @return the executeCalled
     */
    public boolean isExecuteCalled() {
        return executeCalled;
    }
}
