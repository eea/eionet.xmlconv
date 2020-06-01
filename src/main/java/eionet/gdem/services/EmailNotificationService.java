package eionet.gdem.services;

import eionet.gdem.exceptions.EmailException;

import java.util.List;

public interface EmailNotificationService {

    void sendNotificationForLongRunningJobs(List<String> jobIds) throws EmailException;
}
