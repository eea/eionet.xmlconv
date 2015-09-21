package eionet.gdem.configuration;

import java.util.Properties;
import org.junit.Test;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class PropertiesConfigurationResourceProviderTest {

    @Test
    public void testPropertiesAreLoadedGivenAValidResourceNameFromClasspath() throws ConfigurationException {
        PropertiesConfigurationResourceProvider provider = new PropertiesConfigurationResourceProvider("gdem.properties");
        Properties properties = provider.get();
        assertNotNull(provider);
        assertTrue(properties.keySet().size() > 0);
        assertTrue(properties.containsKey("root.folder"));
    }

    @Test(expected = ConfigurationException.class)
    public void testErrorOccursWhenMissingClasspathFileIsProvided() throws ConfigurationException {
        PropertiesConfigurationResourceProvider provider = new PropertiesConfigurationResourceProvider("resourcemissing.properties");
        Properties properties = provider.get();
    }
}
