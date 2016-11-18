package eionet.gdem.qa;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public class ScriptStatus {

    private static final List<String> ACTIVE_STATUS = Collections.unmodifiableList(Arrays.asList("true", "false", "all"));

    public static List<String> getActiveStatusList() {
        return ACTIVE_STATUS;
    }

}
