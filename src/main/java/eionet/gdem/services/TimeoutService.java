package eionet.gdem.services;

import eionet.gdem.models.TimeoutEntity;

import java.util.List;

public interface TimeoutService {

    List<TimeoutEntity> getAllTimeoutProperties();
}
