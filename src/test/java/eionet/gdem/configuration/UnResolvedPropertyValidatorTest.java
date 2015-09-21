/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eionet.gdem.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class UnResolvedPropertyValidatorTest {

    Map<String, String> gdem;
    private SystemPropertyProvider systemProvider;
    private static Map<String, String> local;

    public UnResolvedPropertyValidatorTest() {

    }

    @BeforeClass
    public static void beforeClass() {
        local = new HashMap<String, String>();
        local.put("app.home", "/tmp/xmlconv");
        local.put("config.app.home", "/tmp/xmlconv");
        local.put("config.app.host", "localhost;8080");
        local.put("config.gdem.url", "localhost:8080");
        local.put("config.dd.url", "http://dd.eionet.europa.eu");
        local.put("config.dd.rpc.url", "http://dd.eionet.europa.eu/rpcrouter");
        local.put("config.cr.sparql.endpoint", "http://cr.eionet.europa.eu/sparql");
        local.put("config.log.file", "log/xmlconv.log");
        local.put("config.db.user", "jane");
        local.put("config.db.jdbcurl", "jdbc:mysql://localhost:3306/xmlconv");
        local.put("config.db.password", "secret");
        local.put("config.db.driver", "com.mysql.jdbc.Driver");
        local.put("config.cdr.url", "http://cdr.eionet.europa.eu");

    }

    @Before
    public void before() throws ConfigurationException {
        PropertiesConfigurationResourceProvider p = new PropertiesConfigurationResourceProvider("gdem-config.properties");
        systemProvider = new SystemPropertyProvider() {

            @Override
            public String get(String key) {
                return local.get(key);
            }
        };
        List<Properties> properties = new ArrayList<Properties>();
        properties.add(p.get());
        gdem = (new MapConfigurationResourceProvider(properties)).get();
    }

    @Test
    public void testParsingGdemProperties() throws ConfigurationException {

        UnResolvedPropertyValidator validator = new UnResolvedPropertyValidator(gdem, systemProvider);
        validator.validate();
    }

    @Test
    public void testValidationIsOk() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${b}");
        properties.put("b", "${c}");
        properties.put("c", "test");
        SystemPropertyProvider provider = new SystemPropertyProvider() {

            @Override
            public String get(String key) {
                return null;

            }
        };
        UnResolvedPropertyValidator validator = new UnResolvedPropertyValidator(properties, provider);
        validator.validate();
    }

    @Test
    public void testValidationIsOkAndValueIsResolvedFromSystemProperty() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${b}");
        properties.put("b", "${c}");
        SystemPropertyProvider provider = new SystemPropertyProvider() {

            @Override
            public String get(String key) {
                if (key.equals("c")) {
                    return "test";
                }
                return null;

            }
        };
        UnResolvedPropertyValidator validator = new UnResolvedPropertyValidator(properties, provider);
        validator.validate();
    }

    @Test(expected = ConfigurationException.class)
    public void testValidationThrowsUnResolvedPropertyException() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${b}");
        properties.put("b", "${c}");
        SystemPropertyProvider fake = new SystemPropertyProvider() {

            @Override
            public String get(String key) {
                return null;

            }
        };
        UnResolvedPropertyValidator validator = new UnResolvedPropertyValidator(properties, fake);
        validator.validate();
    }

    public void testValidationIsOkWhenResolvedBothByPropertiesAndBySystemProperties() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${b}/${c}");
        properties.put("b", "${d}/${e}");
        properties.put("c", "test1");
        properties.put("e", "test2");
        SystemPropertyProvider fake = new SystemPropertyProvider() {

            @Override
            public String get(String key) {
                if (key.equals("d")) {
                    return "test3";
                }
                return null;

            }
        };
        UnResolvedPropertyValidator validator = new UnResolvedPropertyValidator(properties, fake);
        validator.validate();
    }

    @Test(expected = ConfigurationException.class)
    public void testValidationExceptionIsThrownWhenACircularReferenceOccurs() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${b}");
        properties.put("b", "${c}");
        properties.put("c", "${a}");
        SystemPropertyProvider fake = new SystemPropertyProvider() {

            @Override
            public String get(String key) {
                return null;

            }
        };
        UnResolvedPropertyValidator validator = new UnResolvedPropertyValidator(properties, fake);
        validator.validate();

    }

    public void testValidationIsOkWhenKeyIsSharedBothInPropertiesAndSystemProperties() throws ConfigurationException {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "${b}");
        properties.put("b", "${c}");
        properties.put("c", "test");
        SystemPropertyProvider fake = new SystemPropertyProvider() {

            @Override
            public String get(String key) {
                if (key.equals("c")) {
                    return "test2";
                }
                return null;

            }
        };
        UnResolvedPropertyValidator validator = new UnResolvedPropertyValidator(properties, fake);
        validator.validate();

    }

}
