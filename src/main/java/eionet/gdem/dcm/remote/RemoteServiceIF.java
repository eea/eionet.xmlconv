package eionet.gdem.dcm.remote;

/**
 * Remote service interface.
 */
public interface RemoteServiceIF {
    /**
     * Sets ticket
     * @param _ticket Ticket
     */
    public void setTicket(String _ticket);

    /**
     * Sets trusted mode true or false
     * @param mode Mode to set
     */
    public void setTrustedMode(boolean mode);

    /**
     * Gets ticket
     * @return Ticket
     */
    public String getTicket();

    /**
     * Returns if it is trusted mode or not
     * @return True if trusted mode.
     */
    public boolean isTrustedMode();

    /**
     * Returns if it is http request or not
     * @return True if it is HTTP request
     */
    public boolean isHTTPRequest();

    /**
     * Assignes the HttpResponseWrapper into the method.
     * The response is used to fulfill the outputstream by converion service.
     * @param httpResponse HTTP response
     */
    public void setHttpResponse(HttpMethodResponseWrapper httpResponse);

}
