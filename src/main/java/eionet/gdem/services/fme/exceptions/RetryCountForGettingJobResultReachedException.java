package eionet.gdem.services.fme.exceptions;

public class RetryCountForGettingJobResultReachedException extends Exception {

    public RetryCountForGettingJobResultReachedException() {
    }

    public RetryCountForGettingJobResultReachedException(String message) {
        super(message);
    }

    public RetryCountForGettingJobResultReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryCountForGettingJobResultReachedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
