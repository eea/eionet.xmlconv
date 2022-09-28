package eionet.gdem.rabbitMQ.model;

public class CdrJobRequestMessage {

    String envelopeUrl;

    String UUID;

    public CdrJobRequestMessage() {
    }

    public CdrJobRequestMessage(String envelopeUrl, String UUID) {
        this.envelopeUrl = envelopeUrl;
        this.UUID = UUID;
    }

    public String getEnvelopeUrl() {
        return envelopeUrl;
    }

    public void setEnvelopeUrl(String envelopeUrl) {
        this.envelopeUrl = envelopeUrl;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
