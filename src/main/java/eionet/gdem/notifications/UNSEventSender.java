package eionet.gdem.notifications;

import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlrpc.XmlRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class UNSEventSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(UNSEventSender.class);

    public UNSEventSender() { }

    /**
     *
     * @param jobIds
     * @param eventType
     */
    public void longRunningJobsNotifications (List<String> jobIds, String eventType) throws GeneralSecurityException {

        if (jobIds == null || eventType == null) {
            return;
        }

        Hashtable predicateObjects = new Hashtable();
        //the first element of the triple will not be used
        Vector objects = new Vector();
        objects.add("");
        predicateObjects.put("", objects);

        objects = new Vector();
        objects.add(eventType);
        predicateObjects.put(Properties.PROP_UNS_EVENTTYPE_PREDICATE, objects);

        objects = new Vector();
        objects.add(jobIds);
        predicateObjects.put(Properties.PROP_UNS_LONG_RUNNING_JOBS_PREDICATE, objects);

        sendEvent(predicateObjects);
    }

    /**
     *
     * @return
     */
    protected boolean isSendingDisabled() {

        String isDisabledStr = Properties.PROP_UNS_DISABLED;
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
    protected void sendEvent(Hashtable predicateObjects) throws GeneralSecurityException {

        if (predicateObjects == null || predicateObjects.size() == 0) {
            return;
        }

        Vector notificationTriples = prepareTriples(predicateObjects);
        logTriples(notificationTriples);
        makeCall(notificationTriples);
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
        String serverURL = Properties.PROP_UNS_XMLRPC_SERVER_URL;
        String channelName = Properties.PROP_UNS_CHANNEL_NAME;
        String functionName = Properties.PROP_UNS_SEND_NOTIFICATION_FUNC;
        String userName = Properties.PROP_UNS_USERNAME;
        String password = Properties.PROP_UNS_PASSWORD;

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

    /**
     *
     * @param predicateObjects
     * @return
     */
    protected Vector prepareTriples(Hashtable predicateObjects) throws GeneralSecurityException {

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

        eventID = Properties.PROP_UNS_EVENTS_NAMESPACE + eventID;

        triple.setSubject(eventID);
        triple.setProperty(Properties.PROP_UNS_LONG_RUNNING_JOBS_PREDICATE);
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
    private void logTriples(Vector triples) {

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

}
