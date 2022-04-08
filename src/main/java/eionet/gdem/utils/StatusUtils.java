package eionet.gdem.utils;

import eionet.gdem.Constants;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

public final class StatusUtils {

    /**
     * Private constructor
     */
    private StatusUtils() {
        // do nothing
    }

    public static Set<Integer> getStatusIdsBasedOnStatusNames(String[] statusNames){
        Set<Integer> statusIds = new HashSet<>();
        for(String statusName: statusNames){
            statusIds.add(getNumberOfStatusBasedOnContainedStringIgnoreCase(statusName));
        }
        return statusIds;
    }

    public static Integer getNumberOfStatusBasedOnContainedStringIgnoreCase(String keyword){
        Integer status = -1;
        if(StringUtils.containsIgnoreCase("JOB RECEIVED", keyword)){
            status = Constants.XQ_RECEIVED;
        }
        else if(StringUtils.containsIgnoreCase("DOWNLOADING SOURCE", keyword)){
            status = Constants.XQ_DOWNLOADING_SRC;
        }
        else if(StringUtils.containsIgnoreCase("PROCESSING", keyword)){
            status = Constants.XQ_PROCESSING;
        }
        else if(StringUtils.containsIgnoreCase("READY", keyword)){
            status = Constants.XQ_READY;
        }
        else if(StringUtils.containsIgnoreCase("FATAL ERROR", keyword)){
            status = Constants.XQ_FATAL_ERR;
        }
        else if(StringUtils.containsIgnoreCase("RECOVERABLE ERROR", keyword)){
            status = Constants.XQ_LIGHT_ERR;
        }
        else if(StringUtils.containsIgnoreCase("INTERRUPTED", keyword)){
            status = Constants.XQ_INTERRUPTED;
        }
        else if(StringUtils.containsIgnoreCase("CANCELLED BY USER", keyword)){
            status = Constants.CANCELLED_BY_USER;
        }
        else if(StringUtils.containsIgnoreCase("DELETED", keyword)){
            status = Constants.DELETED;
        }
        return status;
    }
}