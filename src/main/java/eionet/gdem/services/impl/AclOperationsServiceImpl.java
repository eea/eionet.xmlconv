package eionet.gdem.services.impl;

import eionet.acl.AccessController;
import eionet.acl.AclProperties;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.exceptions.AclAccessControllerInitializationException;
import eionet.gdem.exceptions.AclLibraryAccessControllerModifiedException;
import eionet.gdem.exceptions.AclPropertiesInitializationException;
import eionet.gdem.services.AclOperationsService;
import eionet.propertyplaceholderresolver.CircularReferenceException;
import eionet.propertyplaceholderresolver.ConfigurationPropertyResolver;
import eionet.propertyplaceholderresolver.UnresolvedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
public class AclOperationsServiceImpl implements AclOperationsService {


    ConfigurationPropertyResolver configurationPropertyResolver;
    private static final Logger LOGGER = LoggerFactory.getLogger(AclOperationsServiceImpl.class);

    @Autowired
    public AclOperationsServiceImpl(ConfigurationPropertyResolver configurationPropertyResolver) {
        this.configurationPropertyResolver = configurationPropertyResolver;
    }

    @Override
    public void reinitializeAclRights() throws AclLibraryAccessControllerModifiedException, AclPropertiesInitializationException {
        try {
            AccessController accessController =this.getAclLibraryAccessControllerInstance(this.getAclProperties());
            Method initAclsMethod = null;
            initAclsMethod = AccessController.class.getDeclaredMethod("initAcls");
            initAclsMethod.setAccessible(true);
            initAclsMethod.invoke(accessController);
            initAclsMethod.setAccessible(false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | AclAccessControllerInitializationException e) {
            throw new AclLibraryAccessControllerModifiedException("eionet.acl library AccessController.class method:initAcls is modified. Acl Rights cannot be reinitialized",e.getCause());
        }

    }

    protected AclProperties getAclProperties() throws AclPropertiesInitializationException {
        AclProperties aclProperties = new AclProperties();
        try {
            aclProperties.setOwnerPermission(configurationPropertyResolver.resolveValue("owner.permission"));
            aclProperties.setAnonymousAccess(configurationPropertyResolver.resolveValue("anonymous.access"));
            aclProperties.setAuthenticatedAccess(configurationPropertyResolver.resolveValue("authenticated.access"));
            aclProperties.setDefaultdocPermissions(configurationPropertyResolver.resolveValue("defaultdoc.permissions"));
            aclProperties.setPersistenceProvider(configurationPropertyResolver.resolveValue("persistence.provider"));
            aclProperties.setInitialAdmin(configurationPropertyResolver.resolveValue("initial.admin"));
            aclProperties.setFileAclfolder(configurationPropertyResolver.resolveValue("file.aclfolder"));
            aclProperties.setFileLocalgroups(configurationPropertyResolver.resolveValue("file.localgroups"));
            aclProperties.setFileLocalusers(configurationPropertyResolver.resolveValue("file.localusers"));
            aclProperties.setFilePermissions(configurationPropertyResolver.resolveValue("file.permissions"));
            aclProperties.setDbDriver(configurationPropertyResolver.resolveValue("db.driver"));
            aclProperties.setDbUrl(configurationPropertyResolver.resolveValue("db.url"));
            aclProperties.setDbUser(configurationPropertyResolver.resolveValue("db.user"));
            aclProperties.setDbPwd(configurationPropertyResolver.resolveValue("db.pwd"));
            return aclProperties;
        } catch (UnresolvedPropertyException | CircularReferenceException e) {
            LOGGER.error(e.getMessage(),e.getCause());

            throw new AclPropertiesInitializationException(e.getMessage(), e.getCause());
        }
    }

    protected AccessController getAclLibraryAccessControllerInstance(AclProperties aclProperties) throws AclAccessControllerInitializationException {
        try {
            AccessController accessController = new AccessController(aclProperties);
            return accessController;
        } catch (Exception e) {
            throw new AclAccessControllerInitializationException("Could not initialize eionet.acl AccessController:", e.getCause());
        }
    }
}
