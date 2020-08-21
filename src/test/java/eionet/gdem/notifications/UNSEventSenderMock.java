package eionet.gdem.notifications;

import org.apache.xmlrpc.XmlRpcClient;

import java.net.MalformedURLException;

public class UNSEventSenderMock extends UNSEventSender {
    /** */
    private XmlRpcClientMock rpcClientMock;

    /** */
    private boolean dontCallActually = false;

    /**
     * Default constructor.
     */
    public UNSEventSenderMock() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see eionet.meta.notif.UNSEventSender#newXmlRpcClient(java.lang.String)
     */
    @Override
    protected XmlRpcClient newXmlRpcClient(String serverURL) throws MalformedURLException {

        rpcClientMock = new XmlRpcClientMock(serverURL);
        return rpcClientMock;
    }

    /*
     * (non-Javadoc)
     * @see eionet.meta.notif.UNSEventSender#dontCallActually()
     */
    @Override
    protected boolean isSendingDisabled() {
        return dontCallActually;
    }

    /**
     * @return the rpcClientMock
     */
    public XmlRpcClientMock getRpcClientMock() {
        return rpcClientMock;
    }

    /**
     * @param dontCallActually the dontCallActually to set
     */
    public void setDontCallActually(boolean dontCallActually) {
        this.dontCallActually = dontCallActually;
    }
}
