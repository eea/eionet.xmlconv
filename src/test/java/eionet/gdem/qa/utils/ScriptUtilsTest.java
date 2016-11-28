package eionet.gdem.qa.utils;

import eionet.gdem.qa.XQScript;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * Script Utils
 */
public class ScriptUtilsTest {
    private final String extension = "xq";
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testXQueryValues() throws Exception {
        String result1 = ScriptUtils.getExtensionFromScriptType(XQScript.SCRIPT_LANG_XQUERY1);
        assertEquals("Wrong extension", "xquery", result1);
        String result2 = ScriptUtils.getExtensionFromScriptType(XQScript.SCRIPT_LANG_XQUERY3);
        assertEquals("Wrong extension", "xquery", result2);
        String result3 = ScriptUtils.getExtensionFromScriptType(extension);
        assertEquals("Wrong extension", extension, result3);
    }

    @Test
    public void testConstructor() throws Exception {
        exception.expect(InvocationTargetException.class);
        Constructor<ScriptUtils> defaultConstructor = ScriptUtils.class.getDeclaredConstructor();
        defaultConstructor.setAccessible(true);
        ScriptUtils utils = defaultConstructor.newInstance();
    }

}