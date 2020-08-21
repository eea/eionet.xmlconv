package eionet.gdem.notifications;

import eionet.gdem.Properties;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class UNSEventSenderTest{

    @Test
    public void testLongRunningJobsNotificationsNullIds() throws Exception {

        UNSEventSenderMock unsEventSender = new UNSEventSenderMock();
        unsEventSender.longRunningJobsNotifications(null, null);
   //     postCallAssertions(expectedPredicates, null, unsEventSender);
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
