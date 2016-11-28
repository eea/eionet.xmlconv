package eionet.gdem.qa.utils;

import eionet.gdem.qa.XQScript;

/**
 * QA script utility class.
 * @author George Sofianos
 */

public final class ScriptUtils {

    /**
     * Default constructor
     */
    private ScriptUtils() {
        throw new AssertionError();
    }

    /**
     * Returns file extension from script type.
     * @param scriptType Script type
     * @return File extension
     */
    public static String getExtensionFromScriptType(String scriptType) {
        String extension;
        if (scriptType.equalsIgnoreCase(XQScript.SCRIPT_LANG_XQUERY1) ||  scriptType.equalsIgnoreCase(XQScript.SCRIPT_LANG_XQUERY3)) {
            extension = "xquery";
        } else {
            extension = scriptType;
        }
        return extension;
    }
}
