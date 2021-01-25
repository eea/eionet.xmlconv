package eionet.gdem.rancher.model;

import java.util.HashMap;
import java.util.List;

public class ServiceLaunchConfig {

    private List<String> dataVolumes;
    private List<String> devices;
    private List<String> dns;
    private List<String> dnsSearch;
    private String imageUuid;
    private HashMap<String, String> environment;
    private List<String> ports;
    private HashMap<String, String> labels;
    private boolean stdinOpen;
    private boolean tty;
    private List<String> dataVolumesFromLaunchConfigs;
    private Long memory;
    private Long memoryMb;
    private Long memoryReservation;

    public List<String> getDataVolumes() {
        return dataVolumes;
    }

    public ServiceLaunchConfig setDataVolumes(List<String> dataVolumes) {
        this.dataVolumes = dataVolumes;
        return this;
    }

    public List<String> getDevices() {
        return devices;
    }

    public ServiceLaunchConfig setDevices(List<String> devices) {
        this.devices = devices;
        return this;
    }

    public List<String> getDns() {
        return dns;
    }

    public ServiceLaunchConfig setDns(List<String> dns) {
        this.dns = dns;
        return this;
    }

    public List<String> getDnsSearch() {
        return dnsSearch;
    }

    public ServiceLaunchConfig setDnsSearch(List<String> dnsSearch) {
        this.dnsSearch = dnsSearch;
        return this;
    }

    public String getImageUuid() {
        return imageUuid;
    }

    public ServiceLaunchConfig setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
        return this;
    }

    public HashMap<String, String> getEnvironment() {
        return environment;
    }

    public ServiceLaunchConfig setEnvironment(HashMap<String, String> environment) {
        this.environment = environment;
        return this;
    }

    public List<String> getPorts() {
        return ports;
    }

    public ServiceLaunchConfig setPorts(List<String> ports) {
        this.ports = ports;
        return this;
    }

    public HashMap<String, String> getLabels() {
        return labels;
    }

    public ServiceLaunchConfig setLabels(HashMap<String, String> labels) {
        this.labels = labels;
        return this;
    }

    public boolean isStdinOpen() {
        return stdinOpen;
    }

    public ServiceLaunchConfig setStdinOpen(boolean stdinOpen) {
        this.stdinOpen = stdinOpen;
        return this;
    }

    public boolean isTty() {
        return tty;
    }

    public ServiceLaunchConfig setTty(boolean tty) {
        this.tty = tty;
        return this;
    }

    public List<String> getDataVolumesFromLaunchConfigs() {
        return dataVolumesFromLaunchConfigs;
    }

    public ServiceLaunchConfig setDataVolumesFromLaunchConfigs(List<String> dataVolumesFromLaunchConfigs) {
        this.dataVolumesFromLaunchConfigs = dataVolumesFromLaunchConfigs;
        return this;
    }

    public Long getMemory() {
        return memory;
    }

    public ServiceLaunchConfig setMemory(Long memory) {
        this.memory = memory;
        return this;
    }

    public Long getMemoryMb() {
        return memoryMb;
    }

    public ServiceLaunchConfig setMemoryMb(Long memoryMb) {
        this.memoryMb = memoryMb;
        return this;
    }

    public Long getMemoryReservation() {
        return memoryReservation;
    }

    public ServiceLaunchConfig setMemoryReservation(Long memoryReservation) {
        this.memoryReservation = memoryReservation;
        return this;
    }
}
