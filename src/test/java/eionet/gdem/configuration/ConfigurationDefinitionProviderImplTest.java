/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eionet.gdem.configuration;

import eionet.gdem.configuration.util.ConfigurationLoadException;
import eionet.gdem.configuration.util.PropertyResourceLoader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class ConfigurationDefinitionProviderImplTest {

    public ConfigurationDefinitionProviderImplTest() {

    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getConfigurationDefinition method, of class
     * ConfigurationDefinitionProviderImpl.
     *
     * @throws eionet.gdem.configuration.util.ConfigurationLoadException
     */
    @Test
    public void testGetConfigurationDefinition() throws ConfigurationLoadException {
        final Properties properties = new Properties();
        properties.put("app.home", "/home/user");
        properties.put("xsl.folder", "${app.home}/xsl");
        PropertyResourceLoader propertyResourceLoader = new PropertyResourceLoader() {

            @Override
            public Properties loadFromResource(String resourceName) throws IOException {
                return properties;
            }
        };
        ConfigurationDefinitionProvider configurationDefinitionProvider = new ConfigurationDefinitionProviderImpl(propertyResourceLoader, new String[]{"dummy.properties"});
        Map<String, String> expected = configurationDefinitionProvider.getConfigurationDefinition();
        assertEquals("/home/user", expected.get("app.home"));
        assertEquals("${app.home}/xsl", expected.get("xsl.folder"));
    }

}
