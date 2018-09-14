package eionet.gdem.web.spring.admin.users;

import java.util.List;

public class UsersForm {

    private List<Group> groups;

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
