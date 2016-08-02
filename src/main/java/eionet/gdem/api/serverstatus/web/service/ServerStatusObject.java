package eionet.gdem.api.serverstatus.web.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static java.util.Objects.isNull;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
public class ServerStatusObject implements Serializable {

    private HashMap < String, Integer> index;
    
    public List <ServerStatus> serverStatus;
    public Long timestamp;
    
    public ServerStatusObject( ) {
        this.serverStatus = new ArrayList <ServerStatus> ();
        this.index = new HashMap <String ,Integer> ();
        this.timestamp = System.currentTimeMillis() / 1000L ; //http://stackoverflow.com/questions/732034
    }
    
    public List<ServerStatus> getServerStatus() {
        return serverStatus;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    public class ServerStatus implements Serializable{

        public String getInstanceName() {
            return instanceName;
        }

        public List<jobsByStatus> getJobs_by_status() {
            return jobs_by_status;
        }

        public String getHealth() {
            return health;
        }
        String instanceName;
        List <jobsByStatus> jobs_by_status;
        String health;
    }
    
    public class jobsByStatus implements Serializable{

        public String getJob_status() {
            return job_status;
        }

        public Integer getJob_count() {
            return job_count;
        }

        public jobsByStatus(String job_status, Integer job_count) {
            this.job_status = job_status;
            this.job_count = job_count;
        }
        String job_status;
        Integer job_count;
        
    }
    
    public void insertJobStatusByInstance ( String instanceName, String jobStatus , Integer jobCount){
        boolean found = false;
        int i;
        for (i = 0 ; i < this.serverStatus.size() ; i++) {
            if ( isNull(instanceName) && isNull( this.serverStatus.get(i).instanceName) || ( ! isNull( this.serverStatus.get(i).instanceName) && this.serverStatus.get(i).instanceName.equals(instanceName) ) ) {
                found = true;
                break;
            }
        }
        
        if ( isNull(instanceName)) instanceName = "null";
        
        if ( isNull( index.get ( instanceName ) ) ){
            ServerStatus newElem = new ServerStatus ();
            newElem.instanceName = instanceName ;
            newElem.health = "NA";
            newElem.jobs_by_status = new ArrayList <jobsByStatus> () ;
            newElem.jobs_by_status.add( new jobsByStatus(jobStatus , jobCount));
            this.serverStatus.add( newElem );
            
            index.put( instanceName , this.serverStatus.size() - 1 );
        }
        else {
            this.serverStatus.get( index.get ( instanceName ) ).jobs_by_status.add( new jobsByStatus(jobStatus , jobCount) );
        }
        
    }
    
}
