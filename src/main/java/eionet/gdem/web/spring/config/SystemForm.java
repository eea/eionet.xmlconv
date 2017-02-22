package eionet.gdem.web.spring.config;

/**
 *
 */
public class SystemForm {

    private String cmdXGawk;
    private Long qaTimeout;

    public String getCmdXGawk() {
        return cmdXGawk;
    }

    public void setCmdXGawk(String cmdXGawk) {
        this.cmdXGawk = cmdXGawk;
    }

    public Long getQaTimeout() {
        return qaTimeout;
    }

    public void setQaTimeout(Long qaTimeout) {
        this.qaTimeout = qaTimeout;
    }
}
