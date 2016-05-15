package eionet.gdem.dcm.remote;

import java.io.File;

import eionet.gdem.Properties;

/**
 * Remote service method class.
 */
public abstract class RemoteServiceMethod {
    public static final String DEFAULT_CONTENT_TYPE = "text/plain";

    public static final String DEFAULT_QA_CONTENT_TYPE = "text/html;charset=UTF-8";

    public static final String DEFAULT_FILE_EXT = "txt";

    public static final String DEFAULT_FILE_NAME = "converted";

    /* Filesystem path where XSL files are stored */
    private String xslFolder;

    /* Filesystem path for temporary files */
    private String tmpFolder;

    /* Filesystem path where XQueries files are stored */
    private String queriesFolder;

    private String ticket = null;

    private boolean trustedMode = false; // false for web clients

    private boolean isHttpRequest = false;

    private HttpMethodResponseWrapper httpResponse = null;

    /**
     * Default constructor
     */
    public RemoteServiceMethod() {
        xslFolder = Properties.xslFolder + File.separatorChar; // props.getString("xsl.folder");
        tmpFolder = Properties.tmpFolder + File.separatorChar; // props.getString("tmp.folder");
        queriesFolder = Properties.queriesFolder + File.separatorChar; // props.getString("queries.folder");
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public boolean isTrustedMode() {
        return trustedMode;
    }

    public void setTrustedMode(boolean trustedMode) {
        this.trustedMode = trustedMode;
    }

    public boolean isHttpRequest() {
        return isHttpRequest;
    }

    public HttpMethodResponseWrapper getHttpResponse() {
        return httpResponse;
    }

    /**
     * Sets HTTP response.
     * @param httpResult HTTP result
     */
    public void setHttpResponse(HttpMethodResponseWrapper httpResult) {
        if (httpResult != null)
            isHttpRequest = true;
        this.httpResponse = httpResult;
    }

    public String getXslFolder() {
        return xslFolder;
    }

    public String getTmpFolder() {
        return tmpFolder;
    }

    public String getQueriesFolder() {
        return queriesFolder;
    }

    public void setQueriesFolder(String queriesFolder) {
        this.queriesFolder = queriesFolder;
    }

}
