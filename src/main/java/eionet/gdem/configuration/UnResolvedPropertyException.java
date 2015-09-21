package eionet.gdem.configuration;

public class UnResolvedPropertyException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnResolvedPropertyException(String errorMessage) {
        super(errorMessage);
    }
}
