package eionet.gdem.services;

import eionet.gdem.exceptions.AclLibraryAccessControllerModifiedException;
import eionet.gdem.exceptions.AclPropertiesInitializationException;

public interface AclOperationsService {

    public void reinitializeAclRights() throws AclLibraryAccessControllerModifiedException, AclPropertiesInitializationException;
}
