package eionet.gdem.acl;

import eionet.acl.AclInitializerImpl;
import eionet.acl.AclProperties;
import eionet.propertyplaceholderresolver.CircularReferenceException;
import eionet.propertyplaceholderresolver.ConfigurationPropertyResolver;
import eionet.propertyplaceholderresolver.UnresolvedPropertyException;
import java.util.logging.Logger;
import org.springframework.beans.factory.FactoryBean;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
public class AclPropertiesBuilder implements FactoryBean<AclProperties> {

    private static final Logger LOGGER = Logger.getLogger(AclInitializerImpl.class.getName());

    
    private AclProperties aclProperties;
    private ConfigurationPropertyResolver properties;

    public void setProperties(ConfigurationPropertyResolver properties) {
        this.properties = properties;
    }
    
    private void initAclProperties() throws CircularReferenceException  {
        
        try {
            aclProperties.setOwnerPermission( properties.resolveValue("owner.permission") );
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setOwnerPermission( null );
        }
        try {
            aclProperties.setAnonymousAccess(properties.resolveValue("anonymous.access") );
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setAnonymousAccess( null );
        }
        try {
            aclProperties.setAuthenticatedAccess(properties.resolveValue("authenticated.access"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setAuthenticatedAccess( null );
        }
        try {
            aclProperties.setDefaultdocPermissions(properties.resolveValue("defaultdoc.permissions"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setDefaultdocPermissions( null );
        }
        try {
            aclProperties.setPersistenceProvider(properties.resolveValue("persistence.provider"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setPersistenceProvider( null );
        }
        try {
            aclProperties.setInitialAdmin(properties.resolveValue("initial.admin"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setInitialAdmin( null );
        }
        try {
            aclProperties.setFileAclfolder(properties.resolveValue("file.aclfolder"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setFileAclfolder( null );
        }
        try {
            aclProperties.setFileLocalgroups(properties.resolveValue("file.localgroups"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setFileLocalgroups( null );
        }
        try {
            aclProperties.setFileLocalusers(properties.resolveValue("file.localusers"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setFileLocalusers( null );
        }
        try {
            aclProperties.setFilePermissions(properties.resolveValue("file.permissions"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setFilePermissions( null );
        }
        try {
            aclProperties.setDbUrl(properties.resolveValue("config.acl.db.url"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setDbUrl( null );
        }
        try {
            aclProperties.setDbDriver(properties.resolveValue("config.acl.db.driver"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setDbDriver( null );
        }
        try {
            aclProperties.setDbUser(properties.resolveValue("config.acl.db.user"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setDbUser( null );
        }
        try {
            aclProperties.setDbPwd(properties.resolveValue("config.acl.db.pwd"));
        } catch (UnresolvedPropertyException ex) {
            aclProperties.setDbPwd( null );
        }
        
    }
   
    public AclProperties getObject() throws Exception{ 
        aclProperties = new AclProperties();
        initAclProperties  ();
        return aclProperties;
    }

    public Class<AclProperties> getObjectType() { return AclProperties.class ; } 

    public boolean isSingleton() { return false; }
  
}
