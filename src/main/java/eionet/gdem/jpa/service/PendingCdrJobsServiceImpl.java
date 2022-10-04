package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.PendingCdrJobEntry;
import eionet.gdem.jpa.repositories.PendingCdrJobsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PendingCdrJobsServiceImpl implements PendingCdrJobsService{

    @Autowired
    PendingCdrJobsRepository repository;

    @Override
    public List<PendingCdrJobEntry> getAllPendingEntries(){
        return repository.findAll();
    }

    @Override
    public void savePendingEntry(PendingCdrJobEntry entry){
        repository.save(entry);
    }

    @Override
    public void removePendingEntry(Integer id){
        repository.delete(id);
    }

}
