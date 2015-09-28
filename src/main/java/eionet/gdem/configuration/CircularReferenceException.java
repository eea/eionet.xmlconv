package eionet.gdem.configuration;

public class CircularReferenceException extends ConfigurationException{

    public CircularReferenceException(String message) {
        super(message);
    }

    public CircularReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
