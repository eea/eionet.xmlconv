package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.CdrRequestEntry;
import eionet.gdem.jpa.repositories.CdrRequestsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CdrRequestsServiceImpl implements CdrRequestsService{

    private static final Logger LOGGER = LoggerFactory.getLogger(CdrRequestsServiceImpl.class);

    private CdrRequestsRepository cdrRequestsRepository;

    @Autowired
    public CdrRequestsServiceImpl(CdrRequestsRepository cdrRequestsRepository) {
        this.cdrRequestsRepository = cdrRequestsRepository;
    }

    @Override
    public void save(CdrRequestEntry cdrRequestEntry){
        try {
            cdrRequestsRepository.save(cdrRequestEntry);
        } catch (Exception e) {
            LOGGER.error("Error trying to save cdr request entry " + cdrRequestEntry);
        }
    }

    @Override
    public Integer findByUuid(String uuid) {
        Integer numberOfJobs = null;
        List<CdrRequestEntry> entries = cdrRequestsRepository.findByUuidOrderByDateAddedDesc(uuid);
        if (entries.size() == 0){
            LOGGER.info("No entry found in CDR_REQUESTS table for uuid " + uuid);
        }
        else if(entries.size() > 1){
            LOGGER.info("Found more than one entries in CDR_REQUESTS table for uuid " + uuid + ". The last added value is returned");
            numberOfJobs = entries.get(0).getNumberOfJobs();
        }
        else{
            numberOfJobs = entries.get(0).getNumberOfJobs();
        }
        return numberOfJobs;
    }
}
