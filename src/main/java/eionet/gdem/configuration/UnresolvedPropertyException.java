package eionet.gdem.configuration;

public class UnresolvedPropertyException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String propertyName;
    
    public UnresolvedPropertyException(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getMessage() {
        return String.format("Unable to resolve property: %s", this.getPropertyName());
    }
    
}
