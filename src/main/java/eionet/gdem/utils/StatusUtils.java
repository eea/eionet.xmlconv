package eionet.gdem.utils;

import eionet.gdem.Constants;

public final class StatusUtils {

    /**
     * Private constructor
     */
    private StatusUtils() {
        // do nothing
    }

    public static Integer getNumberOfStatusBasedOnContainedString(String keyword){
        Integer status = -1;
        if("JOB RECEIVED".contains(keyword)){
            status = Constants.XQ_RECEIVED;
        }
        else if("DOWNLOADING SOURCE".contains(keyword)){
            status = Constants.XQ_DOWNLOADING_SRC;
        }
        else if("PROCESSING".contains(keyword)){
            status = Constants.XQ_PROCESSING;
        }
        else if("READY".contains(keyword)){
            status = Constants.XQ_READY;
        }
        else if("FATAL ERROR".contains(keyword)){
            status = Constants.XQ_FATAL_ERR;
        }
        else if("RECOVERABLE ERROR".contains(keyword)){
            status = Constants.XQ_LIGHT_ERR;
        }
        else if("INTERRUPTED".contains(keyword)){
            status = Constants.XQ_INTERRUPTED;
        }
        else if("CANCELLED BY USER".contains(keyword)){
            status = Constants.CANCELLED_BY_USER;
        }
        else if("DELETED".contains(keyword)){
            status = Constants.DELETED;
        }
        return status;
    }
}
