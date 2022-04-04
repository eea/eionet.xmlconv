package eionet.gdem.jpa.service;

public interface AsyncFmeJobService {

    /**
     * check if job is completed in fme server
     * @param fmeJobId
     * @return
     */
    boolean jobHasStatusSuccessOnFmeServer(Long fmeJobId);

    /**
     * cancels a job on fme server
     * @param fmeJobId
     */
    void cancelJobOnFMEServer(Long fmeJobId);
}
