package eionet.gdem.configuration;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 *
 * Object that validates if circular references occur in properties files.
 *
 */
public class CircularReferenceValidatorTest {

    public CircularReferenceValidatorTest() {
    }

    @Test
    public void testEmptyPropertiesDontCauseErrors() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        CircularReferenceValidator v = new CircularReferenceValidator();
        v.validate(properties);
    }

    public void testNoErrorOccursWhenAllValuesArePlacehoders() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${b}");
        properties.put("b", "${c}");
        properties.put("c", "${d}");
        CircularReferenceValidator v = new CircularReferenceValidator();
        v.validate(properties);
    }

    @Test(expected = ConfigurationException.class)
    public void testErrorOccursWhenPlaceholderReferesToItself() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${a}");
        CircularReferenceValidator v = new CircularReferenceValidator();
        v.validate(properties);
    }

    @Test(expected = ConfigurationException.class)
    public void testErrorOccursWhenWeHaveCircularReference() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${b}");
        properties.put("b", "${a}");
        CircularReferenceValidator v = new CircularReferenceValidator();
        v.validate(properties);
    }

    @Test(expected = ConfigurationException.class)
    public void testErrorOccursWhenWeHaveCircularReferenceWith3Placeholders() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${b}");
        properties.put("b", "${c}");
        properties.put("c", "${a}");
        CircularReferenceValidator v = new CircularReferenceValidator();
        v.validate(properties);
    }

    @Test(expected = ConfigurationException.class)
    public void testErrorOccursWhenCircularReferenceOccursWithMultiplPlaceholdersOnValueSide() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${b}/${c}");
        properties.put("b", "${c}");
        properties.put("c", "${a}");
        CircularReferenceValidator v = new CircularReferenceValidator();
        v.validate(properties);
    }

}
