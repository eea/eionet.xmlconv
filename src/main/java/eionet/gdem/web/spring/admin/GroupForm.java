package eionet.gdem.web.spring.admin;

import java.util.HashMap;
import java.util.List;

public class GroupForm {

    private HashMap<String, List<String>> groups;

    public HashMap<String, List<String>> getGroups() {
        return groups;
    }

    public void setGroups(HashMap<String, List<String>> groups) {
        this.groups = groups;
    }
}
