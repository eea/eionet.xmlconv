package eionet.gdem.notifications;

import eionet.gdem.Properties;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
@Ignore
public class UNSEventSenderTest{

    UNSEventSenderMock unsEventSender;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestUtils.setUpProperties(this);
        unsEventSender = new UNSEventSenderMock();
    }

    @Test
    public void testLongRunningJobsNotificationsNullIds() throws Exception {
        List<String> jobIds = new ArrayList<>();
        jobIds.add("1");
        jobIds.add("2");
        jobIds.add("3");
        unsEventSender.longRunningJobsNotifications(jobIds, Properties.LONG_RUNNING_JOBS_EVENT);

        HashSet<String> expectedPredicates = new HashSet<String>();
        expectedPredicates.add("http://localhost:/jobs#event_type");
        expectedPredicates.add("http://localhost:/jobs#long_running_jobs");

        HashSet<String> unexpectedPredicates = new HashSet<String>();
        unexpectedPredicates.add("unexpected");

        postCallAssertions(expectedPredicates, unexpectedPredicates, unsEventSender);
    }

    /**
     *
     * @param expectedPredicates
     * @param unsEventSender
     * @throws InterruptedException
     */
    private void postCallAssertions(HashSet<String> expectedPredicates, HashSet<String> unexpectedPredicates,
                                    UNSEventSenderMock unsEventSender)
            throws InterruptedException {

        // Assert that there was an XmlRpcClient created.
        XmlRpcClientMock client = unsEventSender.getRpcClientMock();
        assertNotNull("XmlRpcClient null-check", client);

        // Assert various properties of the created XmlRpcClient
        String serverUrl = client.getURL() == null ? null : client.getURL().toString();
        assertEquals("XmlRpcClient server URL", Properties.PROP_UNS_XMLRPC_SERVER_URL, serverUrl);

        assertEquals("XmlRpcClient user-name", Properties.PROP_UNS_USERNAME, client.getUser());
        assertEquals("XmlRpcClient password", Properties.PROP_UNS_PASSWORD, client.getPassword());

        // Wait just in case, because client call will be issued by another thread.
        Thread.sleep(2000);

        // Assert that the XmlRpcClient was executed, with the expected method.
        assertTrue("XmlRpcClient.execute called", client.isExecuteCalled());
        assertEquals("XmlRpcClient method", Properties.PROP_UNS_SEND_NOTIFICATION_FUNC,
                client.getLastCalledMethod());

        // Assert that an expected set of parameters were sent by the XmlRpcClient.
        Vector params = client.getLastCalledParams();
        assertNotNull("RPC call params null-check", params);
        assertEquals("RPC call params size-check", 2, params.size());

        assertEquals("Notification channel:", Properties.PROP_UNS_CHANNEL_NAME, params.get(0));
        assertTrue("Notification triples type-check", params.get(1) instanceof Vector);

        assertTriplesPredicates(expectedPredicates, unexpectedPredicates, (Vector) params.get(1));
    }

    /**
     *
     * @param expectedPredicates
     */
    private void assertTriplesPredicates(Collection<String> expectedPredicates, HashSet<String> unexpectedPredicates,
                                         Vector notificationTriples) {

        HashSet<String> actualPredicates = new HashSet<String>();
        for (Object notificationTriple : notificationTriples) {
            assertTrue("Notification triple type-check", notificationTriple instanceof Vector);
            Vector tripleVector = (Vector) notificationTriple;
            assertEquals("Notification triple size", 3, tripleVector.size());
            String actualPredicate = tripleVector.get(1).toString();
            assertTrue("Notification triple blank-check", StringUtils.isNotBlank(actualPredicate));
            actualPredicates.add(actualPredicate);
        }

        for (String expectedPredicate : expectedPredicates) {
            assertTrue("existence of " + expectedPredicate, actualPredicates.contains(expectedPredicate));
        }

        if (unexpectedPredicates != null) {
            for (String unexpectedPredicate : unexpectedPredicates) {
                assertTrue("non-existence of " + unexpectedPredicate, !actualPredicates.contains(unexpectedPredicate));
            }
        }
    }
}
