package eionet.gdem.notifications;

import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.xmlrpc.XmlRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.*;

public class UNSEventSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(UNSEventSender.class);

    /* Variables for eionet.gdem.Properties*/
    private String eventTypePredicateProperty = null;
    private String longRunningJobsPredicateProperty = null;
    private String unsDisabledProperty = null;
    private String xmlrpcServerUrlProperty = null;
    private String channelNameProperty = null;
    private String notificationFunctionNameProperty = null;
    private String userNameProperty = null;
    private String passwordProperty = null;
    private String eventsNamespaceProperty = null;
    private String unsUrl = null;
    private String sendNotificationRest = null;
    private String restUserNameProperty = null;
    private String restPasswordProperty = null;
    private String rancherCircuitBreakerEventTypePredicateProperty = null;
    private String rancherCircuitBreakerPredicateProperty = null;
    private String rancherCircuitBreakerChannelName = null;

    public UNSEventSender() {
    }

    /**
     *
     * @param jobIds
     * @param eventType
     */
    public void longRunningJobsNotifications (List<String> jobIds, String eventType) throws GeneralSecurityException {

        if (jobIds == null || eventType == null) {
            return;
        }

        setupProperties();

        Hashtable predicateObjects = new Hashtable();
        Vector objects = new Vector();
        objects.add(eventType);
        predicateObjects.put(getEventTypePredicateProperty(), objects);

        objects = new Vector();
        objects.add(jobIds);
        predicateObjects.put(getLongRunningJobsPredicateProperty(), objects);

        sendEvent(predicateObjects, UnsEventTypes.LONG_RUNNING_JOBS.getId());
    }

    public void rancherCircuitBreakerOpenNotification(String message, String eventType) throws GeneralSecurityException {
        if (message==null || eventType==null) {
            return;
        }

        setupProperties();

        Hashtable predicateObjects = new Hashtable();
        Vector objects = new Vector();
        objects.add(eventType);
        predicateObjects.put(getRancherCircuitBreakerEventTypePredicateProperty(), objects);

        objects = new Vector();
        objects.add(message);
        predicateObjects.put(getRancherCircuitBreakerPredicateProperty(), objects);

        sendEvent(predicateObjects, UnsEventTypes.RANCHER_CIRCUIT_BREAKER.getId());
    }

    /**
     *
     * @return
     */
    protected boolean isSendingDisabled() {

        String isDisabledStr = getUnsDisabledProperty();
        boolean isWindows = File.separatorChar == '\\';
        if (isWindows) {
            boolean isEnabled = StringUtils.isNotBlank(isDisabledStr) && isDisabledStr.trim().equals("false");
            return !isEnabled;
        } else {
            boolean result = BooleanUtils.toBoolean(isDisabledStr);
            return result;
        }
    }

    /**
     *
     * @param serverURL
     * @return
     * @throws MalformedURLException
     */
    protected XmlRpcClient newXmlRpcClient(String serverURL) throws MalformedURLException {
        return new XmlRpcClient(serverURL);
    }

    /**
     *
     * @param predicateObjects
     */
    protected void sendEvent(Hashtable predicateObjects, Integer findChannel) throws GeneralSecurityException {

        if (predicateObjects == null || predicateObjects.size() == 0) {
            return;
        }

        Vector notificationTriples = prepareTriples(predicateObjects, findChannel);
        logTriples(notificationTriples);
        //makeCall(notificationTriples);
        makeRestCall(notificationTriples, findChannel);
    }

    /**
     *
     * @param rdfTriples
     */
    protected void makeCall(Object rdfTriples) {

        if (isSendingDisabled()) {
            return;
        }

        // get server URL, channel name and function-name from configuration
        String serverURL = getXmlrpcServerUrlProperty();
        String channelName = getChannelNameProperty();
        String functionName = getNotificationFunctionNameProperty();
        String userName = getUserNameProperty();
        String password = getPasswordProperty();

        try {
            // instantiate XML-RPC client object, set username/password from configuration
            XmlRpcClient client = newXmlRpcClient(serverURL);
            client.setBasicAuthentication(userName, password);

            // prepare call parameters
            Vector params = new Vector();
            params.add(channelName);
            params.add(rdfTriples);

            // perform the call
            XmlRpcCallThread.execute(client, functionName, params);
        } catch (IOException e) {
            LOGGER.error("Sending UNS notification failed: " + e.toString(), e);
        }
    }

    protected void makeRestCall(Object rdfTriples, Integer findChannel) {
        try {
            if (isSendingDisabled()) {
                return;
            }

            String channelName = null;
            if (findChannel==UnsEventTypes.LONG_RUNNING_JOBS.getId()) {
                channelName = getChannelNameProperty();
            } else if (findChannel==UnsEventTypes.RANCHER_CIRCUIT_BREAKER.getId()) {
                channelName = getRancherCircuitBreakerChannelName();
            }
            String userName = getRestUserNameProperty();
            String password = getRestPasswordProperty();

            String url = getUnsUrl() + getSendNotificationRest() + channelName;
            HttpGet request = new HttpGet(url);

            // serialize triples
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ObjectOutputStream(out).writeObject(rdfTriples);

            // your string
            String rdfTriplesStr = new String(Hex.encodeHex(out.toByteArray()));

            URI uri = new URIBuilder(request.getURI())
                    .addParameter("triples", rdfTriplesStr)
                    .build();

            ((HttpRequestBase) request).setURI(uri);

            String usernamePassword = userName + ":" + password;
            byte[] encoding = Base64.getEncoder().encode(usernamePassword.getBytes());
            request.addHeader("Authorization", "Basic " + new String(encoding));

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {

                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    String errorMsg = "Received status code " + response.getStatusLine().getStatusCode();
                    throw new Exception(errorMsg);
                }
            } catch (Exception e) {
                throw (e);
            }

        } catch (Exception e) {
            LOGGER.error("Could not send notification to uns: " + e.getMessage());
        }
    }

    /**
     *
     * @param predicateObjects
     * @return
     */
    protected Vector prepareTriples (Hashtable predicateObjects, Integer findChannel) throws GeneralSecurityException {

        Vector notificationTriples = new Vector();
        NotificationTriple triple = new NotificationTriple();
        String eventID = String.valueOf(System.currentTimeMillis());

        try {
            String digest = Utils.digestHexDec(eventID, "MD5");
            if (digest != null && digest.length() > 0) {
                eventID = digest;
            }
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException("Error generating an MD5 hash", e);
        }

        eventID = getEventsNamespaceProperty() + eventID;

        triple.setSubject(eventID);
        if (findChannel==UnsEventTypes.LONG_RUNNING_JOBS.getId()) {
            triple.setProperty(getLongRunningJobsPredicateProperty());
        } else if (findChannel==UnsEventTypes.RANCHER_CIRCUIT_BREAKER.getId()) {
            triple.setProperty(getRancherCircuitBreakerPredicateProperty());
        }
        triple.setValue("Converters event");
        notificationTriples.add(triple.toVector());

        Enumeration predicates = predicateObjects.keys();
        while (predicates.hasMoreElements()) {
            String predicate = (String) predicates.nextElement();
            Vector objects = (Vector) predicateObjects.get(predicate);
            for (int i = 0; objects != null && i < objects.size(); i++) {
                String object = objects.get(i).toString();

                triple = new NotificationTriple();
                triple.setSubject(eventID);
                triple.setProperty(predicate);
                triple.setValue(object);
                notificationTriples.add(triple.toVector());
            }
        }
        return notificationTriples;
    }

    /**
     *
     * @param triples
     */
    private void logTriples (Vector triples){

        if (triples != null) {

            int noOfTriples = triples.size();
            for (int i = 0; i < noOfTriples; i++) {

                Vector triple = (Vector) triples.get(i);
                if (triple != null) {

                    int tripleSize = triple.size();
                    if (tripleSize > 0) {

                        StringBuilder sb = new StringBuilder();
                        for (int j = 0; j < tripleSize; j++) {

                            if (j > 0) {
                                sb.append(" | ");
                            }
                            sb.append(triple.get(j));
                        }
                        LOGGER.debug(sb.toString());
                    }
                }
            }
        }
    }

    protected String getEventTypePredicateProperty () {
        return eventTypePredicateProperty;
    }

    protected String getLongRunningJobsPredicateProperty () {
        return longRunningJobsPredicateProperty;
    }

    protected String getUnsDisabledProperty () {
        return unsDisabledProperty;
    }

    protected String getXmlrpcServerUrlProperty () {
        return xmlrpcServerUrlProperty;
    }

    protected String getChannelNameProperty () {
        return channelNameProperty;
    }

    protected String getNotificationFunctionNameProperty () {
        return notificationFunctionNameProperty;
    }

    protected String getUserNameProperty () {
        return userNameProperty;
    }

    protected String getPasswordProperty () {
        return passwordProperty;
    }

    protected String getEventsNamespaceProperty () {
        return eventsNamespaceProperty;
    }

    public String getUnsUrl () {
        return unsUrl;
    }

    public String getSendNotificationRest () {
        return sendNotificationRest;
    }

    public String getRestUserNameProperty () {
        return restUserNameProperty;
    }

    public String getRestPasswordProperty () {
        return restPasswordProperty;
    }

    public String getRancherCircuitBreakerEventTypePredicateProperty() {
        return rancherCircuitBreakerEventTypePredicateProperty;
    }

    public String getRancherCircuitBreakerPredicateProperty() {
        return rancherCircuitBreakerPredicateProperty;
    }

    public String getRancherCircuitBreakerChannelName() {
        return rancherCircuitBreakerChannelName;
    }

    protected void setupProperties () {
        eventTypePredicateProperty = Properties.PROP_UNS_EVENTTYPE_PREDICATE;
        longRunningJobsPredicateProperty = Properties.PROP_UNS_LONG_RUNNING_JOBS_PREDICATE;
        unsDisabledProperty = Properties.PROP_UNS_DISABLED;
        xmlrpcServerUrlProperty = Properties.PROP_UNS_XMLRPC_SERVER_URL;
        channelNameProperty = Properties.PROP_UNS_CHANNEL_NAME;
        notificationFunctionNameProperty = Properties.PROP_UNS_SEND_NOTIFICATION_FUNC;
        userNameProperty = Properties.PROP_UNS_USERNAME;
        passwordProperty = Properties.PROP_UNS_PASSWORD;
        eventsNamespaceProperty = Properties.PROP_UNS_EVENTS_NAMESPACE;
        unsUrl = Properties.PROP_UNS_URL;
        sendNotificationRest = Properties.PROP_UNS_REST_SEND_NOTIFICATION;
        restUserNameProperty = Properties.PROP_UNS_REST_USERNAME;
        restPasswordProperty = Properties.PROP_UNS_REST_PASSWORD;
        rancherCircuitBreakerPredicateProperty = Properties.PROP_UNS_RANCHER_CIRCUIT_BREAKER_PREDICATE;
        rancherCircuitBreakerEventTypePredicateProperty = Properties.PROP_UNS_RANCHER_CIRCUIT_BREAKER_EVENTTYPE_PREDICATE;
        rancherCircuitBreakerChannelName = Properties.PROP_UNS_RANCHER_CIRCUIT_BREAKER_CHANNEL_NAME;
    }

}
