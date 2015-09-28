package eionet.gdem.configuration;

public class UnresolvedPropertyException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnresolvedPropertyException(String errorMessage) {
        super(errorMessage);
    }
}
