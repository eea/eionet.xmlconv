package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.AlertEntry;
import eionet.gdem.jpa.repositories.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertServiceImpl implements AlertService {

    private AlertRepository alertRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertServiceImpl.class);

    @Autowired
    public AlertServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public void save(AlertEntry alertEntry) {
        try {
            alertRepository.save(alertEntry);
        } catch (Exception e) {
            LOGGER.error("Error trying to save alert " + alertEntry);
        }
    }

    @Override
    public List<AlertEntry> findAll() {
        List<AlertEntry> alerts = alertRepository.findAll();
        alerts.forEach(alertEntry -> alertEntry.setOccurrenceDateMod(alertEntry.getOccurrenceDate().toString()));
        return alerts;
    }

    @Override
    public void delete(Integer id) {
        alertRepository.delete(id);
    }
}
