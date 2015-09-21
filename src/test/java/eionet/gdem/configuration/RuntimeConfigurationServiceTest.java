/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eionet.gdem.configuration;

import java.util.HashMap;
import java.util.Map;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class RuntimeConfigurationServiceTest {

    @Test
    public void testResolve() throws UnResolvedPropertyException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("a", "${b}");
        map.put("b", "${c}/${d}");
        map.put("c", "test1");
        map.put("d", "test2");
        RuntimeConfigurationService p = new RuntimeConfigurationService(map);
        assertEquals("test1/test2", p.get("a"));
        assertTrue(p.getCachedResources().size() == 1);
        assertEquals("test1/test2", p.get("b"));
        assertTrue(p.getCachedResources().size() == 2);
        assertEquals("test1", p.get("c"));
        assertTrue(p.getCachedResources().size() == 3);
        assertEquals("test2", p.get("d"));
        assertTrue(p.getCachedResources().size() == 4);

    }

    @Test
    public void testGetWhenValueIsResolvedFromSystemPropertiesWhenIsShared() throws UnResolvedPropertyException {
        Map<String, String> map = new HashMap<String, String>();
        SystemPropertyProvider fake = new SystemPropertyProvider() {

            @Override
            public String get(String key) {
                if (key.equals("d")) {
                    return "test";
                }
                return null;
            }
        };
        map.put("a", "${b}/${b}");
        map.put("b", "${c}");
        map.put("c", "${d}");
        map.put("d", "test.local");
        RuntimeConfigurationService p = new RuntimeConfigurationService(map);
        p.setSystemPropertiesProvider(fake);
        assertEquals("test/test", p.get("a"));
        assertTrue(p.getCachedResources().size() == 1);
        assertEquals("test", p.get("b"));
        assertTrue(p.getCachedResources().size() == 2);
        assertEquals("test", p.get("c"));
        assertTrue(p.getCachedResources().size() == 3);
        assertEquals("test", p.get("d"));
        assertTrue(p.getCachedResources().size() == 4);

    }

    @Test
    public void testGetSystemPropertyValueOverridesLocalValue() throws UnResolvedPropertyException {
        Map<String, String> map = new HashMap<String, String>();
        SystemPropertyProvider fake = new SystemPropertyProvider() {

            @Override
            public String get(String key) {
                if (key.equals("d")) {
                    return "test";
                }
                return null;
            }
        };
        map.put("a", "${b}/${b}");
        map.put("b", "${c}");
        map.put("c", "${d}");
        RuntimeConfigurationService p = new RuntimeConfigurationService(map);
        p.setSystemPropertiesProvider(fake);
        assertEquals("test/test", p.get("a"));
        assertTrue(p.getCachedResources().size() == 1);
        assertEquals("test", p.get("b"));
        assertTrue(p.getCachedResources().size() == 2);
        assertEquals("test", p.get("c"));
        assertTrue(p.getCachedResources().size() == 3);
        assertEquals("test", p.get("d"));
        assertTrue(p.getCachedResources().size() == 4);

    }

    @Test(expected = UnResolvedPropertyException.class)
    public void testGetExceptionIsThrowWhenValueCannotBeResolved() throws UnResolvedPropertyException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("a", "${b}/${b}");
        map.put("b", "${c}");
        map.put("c", "${d}");
        RuntimeConfigurationService p = new RuntimeConfigurationService(map);
        assertEquals("test/test", p.get("a"));
        assertTrue(p.getCachedResources().size() == 1);
        assertEquals("test", p.get("b"));
        assertTrue(p.getCachedResources().size() == 2);
        assertEquals("test", p.get("c"));
        assertTrue(p.getCachedResources().size() == 3);
        assertEquals("test", p.get("d"));
        assertTrue(p.getCachedResources().size() == 4);

    }

    public void testInvalidate() throws UnResolvedPropertyException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("a", "${b}");
        map.put("b", "${c}/${d}");
        map.put("c", "test1");
        map.put("d", "test2");
        RuntimeConfigurationService p = new RuntimeConfigurationService(map);
        p.get("a");
        assertEquals(4, p.getCachedResources().size());
        p.invalidate();
        assertTrue(p.getCachedResources().isEmpty());
    }

}
