package eionet.gdem.notifications;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Vector;

public class XmlRpcCallThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlRpcCallThread.class);

    /**
     *
     */
    private XmlRpcClient client;
    private String methodName;
    private Vector params;

    /**
     * @param client
     * @param methodName
     * @param params
     */
    public XmlRpcCallThread(XmlRpcClient client, String methodName, Vector params) {

        this.client = client;
        this.methodName = methodName;
        this.params = params;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Thread#run()
     */
    public void run() {

        try {
            client.execute(methodName, params);
        } catch (XmlRpcException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @param client
     * @param methodName
     * @param params
     */
    public static void execute(XmlRpcClient client, String methodName, Vector params) {

        XmlRpcCallThread caller = new XmlRpcCallThread(client, methodName, params);
        caller.start();
    }
}
