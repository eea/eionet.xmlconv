package eionet.gdem.qa.engines.fme;

import org.basex.core.cmd.Run;

public class RetryPolicy {

private Integer numberOfRetries;

private Long intervalBetweenRetries;

    public RetryPolicy(Integer numberOfRetries, Long intervalBetweenRetries) {
        this.numberOfRetries = numberOfRetries;
        this.intervalBetweenRetries = intervalBetweenRetries;
    }

    public Integer getNumberOfRetries() {
        return numberOfRetries;
    }

    public void setNumberOfRetries(Integer numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    public Long getIntervalBetweenRetries() {
        return intervalBetweenRetries;
    }

    public void setIntervalBetweenRetries(Long intervalBetweenRetries) {
        this.intervalBetweenRetries = intervalBetweenRetries;
    }
}


