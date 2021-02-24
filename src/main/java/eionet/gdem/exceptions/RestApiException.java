package eionet.gdem.exceptions;

public class RestApiException extends Exception {

    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Constructor
     *
     * @param errorCode Error code
     * @param message   Exception message
     */
    public RestApiException(String errorCode, String message) {
        super("Error Message:" + message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor
     *
     * @param errorCode Error code
     */
    public RestApiException(String errorCode) {
        this.errorCode = errorCode;
    }
}