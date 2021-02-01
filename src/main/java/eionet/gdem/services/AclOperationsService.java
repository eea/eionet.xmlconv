package eionet.gdem.services;

import eionet.gdem.exceptions.AclLibraryAccessControllerModifiedException;
import eionet.gdem.exceptions.AclPropertiesInitializationException;

import java.util.Hashtable;
import java.util.Vector;

public interface AclOperationsService {

    public void reinitializeAclRights() throws AclLibraryAccessControllerModifiedException, AclPropertiesInitializationException;
    Hashtable<String, Vector<String>> getRefreshedGroupsAndUsersHashTable(boolean init) throws AclLibraryAccessControllerModifiedException, AclPropertiesInitializationException;
}
