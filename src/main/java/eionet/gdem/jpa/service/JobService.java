package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.qa.XQScript;

public interface JobService {

    void changeNStatus(XQScript xqScript, Integer status);

    void changeInternalStatus(InternalSchedulingStatus intStatus, Integer jobId);
}
