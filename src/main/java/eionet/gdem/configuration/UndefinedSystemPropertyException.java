package eionet.gdem.configuration;

public class UndefinedSystemPropertyException extends RuntimeException {

    public UndefinedSystemPropertyException(String errorMessage) {
        super(errorMessage);
    }
}
