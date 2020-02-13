package eionet.gdem.services.impl;


import eionet.acl.AccessController;
import eionet.acl.AclProperties;
import eionet.gdem.exceptions.AclAccessControllerInitializationException;
import eionet.gdem.exceptions.AclLibraryAccessControllerModifiedException;
import eionet.gdem.exceptions.AclPropertiesInitializationException;
import eionet.gdem.test.ApplicationTestContext;
import eionet.propertyplaceholderresolver.CircularReferenceException;
import eionet.propertyplaceholderresolver.ConfigurationPropertyResolver;
import eionet.propertyplaceholderresolver.UnresolvedPropertyException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.context.ContextConfiguration;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
@PrepareForTest(AccessController.class)

public class AclOperationsServiceTest {


    @Mock
    ConfigurationPropertyResolver configurationPropertyResolver;

    AclOperationsServiceImpl aclOperationsService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.aclOperationsService = new AclOperationsServiceImpl(configurationPropertyResolver);
    }


    @Test(expected = AclPropertiesInitializationException.class)
    public void testGetAclPropertiesThrowsAclPropertiesInitializationException() throws UnresolvedPropertyException, CircularReferenceException, AclPropertiesInitializationException {
        when(this.configurationPropertyResolver.resolveValue(any(String.class))).thenThrow(UnresolvedPropertyException.class);
        this.aclOperationsService.getAclProperties();
    }

    @Test
    public void testSucessGettingAclProperties() throws AclPropertiesInitializationException {
        final AclProperties aclProperties = mock(AclProperties.class);
        AclOperationsServiceImpl aclOperationsServiceImpl = new AclOperationsServiceImpl(this.configurationPropertyResolver) {
            @Override
            protected AclProperties getAclProperties() throws AclPropertiesInitializationException {
                return aclProperties;
            }
        };
        assertThat(aclOperationsServiceImpl.getAclProperties(), equalTo(aclProperties));
    }


    @Test(expected = AclLibraryAccessControllerModifiedException.class)
    public void testReninitializeAclRightsThrowsAclLibraryAccessControllerModifiedException() throws AclLibraryAccessControllerModifiedException, AclPropertiesInitializationException {
        final AclProperties aclProperties = mock(AclProperties.class);
        AclOperationsServiceImpl aclOperationsServiceImpl = new AclOperationsServiceImpl(this.configurationPropertyResolver) {
            @Override
            protected AclProperties getAclProperties() throws AclPropertiesInitializationException {
                return aclProperties;
            }

            @Override
            protected AccessController getAclLibraryAccessControllerInstance(AclProperties aclProperties) throws AclAccessControllerInitializationException {
                throw new AclAccessControllerInitializationException();
            }
        };
        aclOperationsServiceImpl.reinitializeAclRights();
    }


    @Test(expected = AclPropertiesInitializationException.class)
    public void testRenitializeAclRightsThrowsAclPropertiesInitializationException() throws AclLibraryAccessControllerModifiedException, AclPropertiesInitializationException {
        AclOperationsServiceImpl aclOperationsServiceImpl = new AclOperationsServiceImpl(this.configurationPropertyResolver) {
            @Override
            protected AclProperties getAclProperties() throws AclPropertiesInitializationException {
                throw new AclPropertiesInitializationException();
            }

            @Override
            protected AccessController getAclLibraryAccessControllerInstance(AclProperties aclProperties) throws AclAccessControllerInitializationException {
                throw new AclAccessControllerInitializationException();
            }
        };
        aclOperationsServiceImpl.reinitializeAclRights();
    }

    @Test
    public void testSuccessfullyReninitializeAclRightsThroughCallingAccessControllerInvokeMethod() throws Exception {
        AclProperties aclProperties = new AclProperties();
        aclProperties.setOwnerPermission("t");
        aclProperties.setAnonymousAccess("anonymous");
        aclProperties.setFileAclfolder("aclProperties");
        aclProperties.setAuthenticatedAccess("authenticated");
        AccessController spyAccessController = spy(new AccessController(aclProperties));
        AclOperationsServiceImpl aclOperationsServiceImpl = new AclOperationsServiceImpl(this.configurationPropertyResolver) {

            @Override
            protected AclProperties getAclProperties() throws AclPropertiesInitializationException {
                return aclProperties;
            }

            @Override
            protected AccessController getAclLibraryAccessControllerInstance(AclProperties aclProperties) throws AclAccessControllerInitializationException {
                return spyAccessController;
            }
        };
        PowerMockito.mockStatic(AccessController.class);
        PowerMockito.doNothing().when(AccessController.class, "initAcls");
        aclOperationsServiceImpl.reinitializeAclRights();
        PowerMockito.verifyPrivate(AccessController.class).invoke("initAcls");

    }


    @After
    public void validate() {
        validateMockitoUsage();
    }

}

