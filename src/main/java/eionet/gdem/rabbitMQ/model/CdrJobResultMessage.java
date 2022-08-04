package eionet.gdem.rabbitMQ.model;

public class CdrJobResultMessage {

    private String feedbackStatus;
    private String feedbackContent;
    private String feedbackMessage;
    private String feedbackContentType;
    private String REMOTE_FILES;

    public CdrJobResultMessage() {
    }

    public CdrJobResultMessage(String feedbackStatus, String feedbackContent, String feedbackMessage, String feedbackContentType, String REMOTE_FILES) {
        this.feedbackStatus = feedbackStatus;
        this.feedbackContent = feedbackContent;
        this.feedbackMessage = feedbackMessage;
        this.feedbackContentType = feedbackContentType;
        this.REMOTE_FILES = REMOTE_FILES;
    }

    public String getFeedbackStatus() {
        return feedbackStatus;
    }

    public void setFeedbackStatus(String feedbackStatus) {
        this.feedbackStatus = feedbackStatus;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public String getFeedbackContentType() {
        return feedbackContentType;
    }

    public void setFeedbackContentType(String feedbackContentType) {
        this.feedbackContentType = feedbackContentType;
    }

    public String getREMOTE_FILES() {
        return REMOTE_FILES;
    }

    public void setREMOTE_FILES(String REMOTE_FILES) {
        this.REMOTE_FILES = REMOTE_FILES;
    }
}
