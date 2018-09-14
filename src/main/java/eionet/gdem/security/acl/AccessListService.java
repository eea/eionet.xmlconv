package eionet.gdem.security.acl;

import eionet.acl.PersistenceFile;
import eionet.acl.SignOnException;
import eionet.gdem.web.spring.admin.users.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.sql.SQLException;
import java.util.*;

@Component
public class AccessListService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessListService.class);


    public HashMap<String, List<String>> getGroups() throws SignOnException, SQLException {

        HashMap<String, java.security.acl.Group> groups = new HashMap<>();
        HashMap<String, Principal> users = new HashMap<>();

        PersistenceFile persistenceFile = new PersistenceFile();
        persistenceFile.readGroups(groups, users);

        HashMap<String, List<String>> groupz = new HashMap<>();
        for (Map.Entry<String, java.security.acl.Group> group : groups.entrySet()) {
            List<String> userz = new ArrayList<>();
            for (Enumeration<?> e =  group.getValue().members(); e.hasMoreElements();) {
                userz.add(e.nextElement().toString());
            }

            groupz.put(group.getKey(), userz);
        }
        return groupz;
    }

    public void writeGroups(List<Group> formGroups) throws SignOnException {
        HashMap<String, java.security.acl.Group> groups;
        Hashtable groupsTable = new Hashtable();


        for (Group formGroup : formGroups) {
            groupsTable.put(formGroup.getName(), new Vector<>(formGroup.getUsers()));
        }
        PersistenceFile persistenceFile = new PersistenceFile();
        persistenceFile.writeGroups(groupsTable);
    }
}
