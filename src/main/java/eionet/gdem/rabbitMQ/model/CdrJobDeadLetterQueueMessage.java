package eionet.gdem.rabbitMQ.model;

public class CdrJobDeadLetterQueueMessage {

    String envelopeUrl;

    String UUID;

    String errorMessage;

    public CdrJobDeadLetterQueueMessage() {
    }

    public CdrJobDeadLetterQueueMessage(String envelopeUrl, String UUID, String errorMessage) {
        this.envelopeUrl = envelopeUrl;
        this.UUID = UUID;
        this.errorMessage = errorMessage;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
