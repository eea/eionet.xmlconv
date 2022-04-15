package eionet.gdem.api.model;

public class ContainerInfo {

    private String[] dns;
    private String[] dns_search;
    private String environment_uuid;
    private String external_id;
    private String[] health_check_hosts;
    private String health_state;
    private String host_uuid;
    private String[] ips;
    private Long memory_reservation;
    private String metadata_kind;
    private String name;
    private String service_index;
    private String service_name;
    private String service_uuid;
    private String stack_name;
    private String stack_uuid;
    private Integer start_count;
    private String state;
    private boolean system;
    private String uuid;

    public String[] getDns() {
        return dns;
    }

    public void setDns(String[] dns) {
        this.dns = dns;
    }

    public String[] getDns_search() {
        return dns_search;
    }

    public void setDns_search(String[] dns_search) {
        this.dns_search = dns_search;
    }

    public String getEnvironment_uuid() {
        return environment_uuid;
    }

    public void setEnvironment_uuid(String environment_uuid) {
        this.environment_uuid = environment_uuid;
    }

    public String getExternal_id() {
        return external_id;
    }

    public void setExternal_id(String external_id) {
        this.external_id = external_id;
    }

    public String[] getHealth_check_hosts() {
        return health_check_hosts;
    }

    public void setHealth_check_hosts(String[] health_check_hosts) {
        this.health_check_hosts = health_check_hosts;
    }

    public String getHealth_state() {
        return health_state;
    }

    public void setHealth_state(String health_state) {
        this.health_state = health_state;
    }

    public String getHost_uuid() {
        return host_uuid;
    }

    public void setHost_uuid(String host_uuid) {
        this.host_uuid = host_uuid;
    }

    public String[] getIps() {
        return ips;
    }

    public void setIps(String[] ips) {
        this.ips = ips;
    }

    public Long getMemory_reservation() {
        return memory_reservation;
    }

    public void setMemory_reservation(Long memory_reservation) {
        this.memory_reservation = memory_reservation;
    }

    public String getMetadata_kind() {
        return metadata_kind;
    }

    public void setMetadata_kind(String metadata_kind) {
        this.metadata_kind = metadata_kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService_index() {
        return service_index;
    }

    public void setService_index(String service_index) {
        this.service_index = service_index;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_uuid() {
        return service_uuid;
    }

    public void setService_uuid(String service_uuid) {
        this.service_uuid = service_uuid;
    }

    public String getStack_name() {
        return stack_name;
    }

    public void setStack_name(String stack_name) {
        this.stack_name = stack_name;
    }

    public String getStack_uuid() {
        return stack_uuid;
    }

    public void setStack_uuid(String stack_uuid) {
        this.stack_uuid = stack_uuid;
    }

    public Integer getStart_count() {
        return start_count;
    }

    public void setStart_count(Integer start_count) {
        this.start_count = start_count;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
