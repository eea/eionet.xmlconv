package eionet.gdem.security.acl;

import eionet.acl.PersistenceFile;
import eionet.acl.SignOnException;
import eionet.acl.XmlFileReaderWriter;
import eionet.gdem.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.security.acl.Group;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

@Component
public class AccessListService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessListService.class);


    public HashMap<String, Group> getGroups() throws SignOnException, SQLException {

        HashMap<String, Group> groups = new HashMap<>();
        HashMap<String, Principal> users = new HashMap<>();

        PersistenceFile persistenceFile = new PersistenceFile();
        persistenceFile.readGroups(groups, users);
        return groups;
    }

    public void writeGroups(HashMap<String, Group> groups) throws SignOnException {
        PersistenceFile persistenceFile = new PersistenceFile();
        Hashtable groupsTable = new Hashtable(groups);
        persistenceFile.writeGroups(groupsTable);
    }
}
