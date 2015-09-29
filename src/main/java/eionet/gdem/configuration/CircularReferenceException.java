package eionet.gdem.configuration;

public class CircularReferenceException extends Exception {

    private final String propertyName;
    
    public CircularReferenceException(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getMessage() {
        return String.format("Circular dependency caused by property: %s", this.getPropertyName());
    }
    
}
