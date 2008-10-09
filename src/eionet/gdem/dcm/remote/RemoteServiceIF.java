package eionet.gdem.dcm.remote;


public interface RemoteServiceIF {

	public void setTicket(String _ticket);

	public void setTrustedMode(boolean mode);

	public String getTicket();

	public boolean isTrustedMode();

	public boolean isHTTPRequest();

	/**
	 * Assignes the HttpResponseWrapper into the method. 
	 * The response is used to fulfill the outputstream by converion service.
	 */
	public void setHttpResponse(HttpMethodResponseWrapper httpResponse);

}