package eionet.gdem.dcm.remote;


/**
 * Remote Service Method Facade.
 * The service is able to execute different remote methods
 *  that are called through XML/RPC and HTTP POST and GET.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */
public abstract class RemoteService implements RemoteServiceIF {

    //The service provides methods both for HTTP and XMLRPC clients.
    //isHttpResponse=true and HttpMethodResponseWrapper object is initialised, if the service is called through HTTP.

    private boolean isHttpRequest = false;

    private HttpMethodResponseWrapper httpResponse = null;

    private String ticket = null;

    private boolean trustedMode = false;


    /* (non-Javadoc)
     * @see eionet.gdem.services.RemoteServiceIF#setTicket(java.lang.String)
     */
    public void setTicket(String _ticket) {
        this.ticket = _ticket;
    }

    /* (non-Javadoc)
     * @see eionet.gdem.services.RemoteServiceIF#setTrustedMode(boolean)
     */
    public void setTrustedMode(boolean mode) {
        this.trustedMode = mode;
    }


    /* (non-Javadoc)
     * @see eionet.gdem.services.RemoteServiceIF#getTicket()
     */
    public String getTicket() {
        return ticket;
    }

    /* (non-Javadoc)
     * @see eionet.gdem.services.RemoteServiceIF#isTrustedMode()
     */
    public boolean isTrustedMode() {
        return trustedMode;
    }

    /* (non-Javadoc)
     * @see eionet.gdem.services.RemoteServiceIF#isHTTPRequest()
     */
    public boolean isHTTPRequest() {
        return isHttpRequest;
    }

    /** (non-Javadoc)
     * Sets http response
     * @param httpResponse HTTP response
     */
    public void setHttpResponse(HttpMethodResponseWrapper httpResponse) {
        if (httpResponse != null) isHttpRequest = true;
        this.httpResponse = httpResponse;
    }
    /**
     * Assign ticket and HTTPResponse to the executed method.
     * @param method Remote service method
     */
    protected void setGlobalParameters(RemoteServiceMethod method){
        //if it's a xml-rpc request, then use trusted account for getting remote URLs
        if (!isHttpRequest)
            setTrustedMode(true);

        method.setTicket(getTicket());
        method.setTrustedMode(isTrustedMode());
        method.setHttpResponse(httpResponse);

    }
}
