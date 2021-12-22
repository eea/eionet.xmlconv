package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.PropertiesEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.PropertiesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PropertiesServiceImpl implements PropertiesService {

    private PropertiesRepository propertiesRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutorServiceImpl.class);

    @Autowired
    public PropertiesServiceImpl(PropertiesRepository propertiesRepository) {
        this.propertiesRepository = propertiesRepository;
    }

    @Override
    public PropertiesEntry findByName(String name) {
        return propertiesRepository.findByName(name);
    }

    @Override
    public Object getValue(String name) throws DatabaseException {
        PropertiesEntry propertiesEntry = null;
        try {
            propertiesEntry = findByName(name);
        } catch (Exception e) {
            LOGGER.error("Database exception during retrieval of property with name " + name);
            throw new DatabaseException(e.getMessage());
        }
        Object value = null;
        if (propertiesEntry!=null) {
            switch (propertiesEntry.getType().getId()) {
                case 0 :
                    value = Integer.parseInt(propertiesEntry.getValue());
                    break;
                case 1:
                    value = Long.parseLong(propertiesEntry.getValue());
                    break;
                case 2:
                    value = new BigInteger(propertiesEntry.getValue());
                    break;
                case 3:
                    value = propertiesEntry.getValue();
                    break;
                case 4:
                    value = LocalDate.parse(propertiesEntry.getValue(), DateTimeFormatter.BASIC_ISO_DATE);
                    break;
            }
        }
        return value;
    }

    @Override
    public List<PropertiesEntry> findAll() {
        return propertiesRepository.findAll();
    }

    @Override
    public void save(PropertiesEntry propertiesEntry) {
        propertiesRepository.save(propertiesEntry);
    }


}
