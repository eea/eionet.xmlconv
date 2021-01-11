package eionet.gdem.rancher.model;

import java.util.HashMap;

public class ServiceLaunchConfig {

    private String[] dataVolumes;
    private String[] devices;
    private String[] dns;
    private String[] dnsSearch;
    private String imageUuid;
    private HashMap<String, String> environment;
    private String[] ports;
    private HashMap<String, String> labels;
    private boolean stdinOpen;
    private boolean tty;
    private String[] dataVolumesFromLaunchConfigs;
    private Long memory;
    private Long memoryMb;
    private Long memoryReservation;

    public String[] getDataVolumes() {
        return dataVolumes;
    }

    public ServiceLaunchConfig setDataVolumes(String[] dataVolumes) {
        this.dataVolumes = dataVolumes;
        return this;
    }

    public String[] getDevices() {
        return devices;
    }

    public ServiceLaunchConfig setDevices(String[] devices) {
        this.devices = devices;
        return this;
    }

    public String[] getDns() {
        return dns;
    }

    public ServiceLaunchConfig setDns(String[] dns) {
        this.dns = dns;
        return this;
    }

    public String[] getDnsSearch() {
        return dnsSearch;
    }

    public ServiceLaunchConfig setDnsSearch(String[] dnsSearch) {
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

    public String[] getPorts() {
        return ports;
    }

    public ServiceLaunchConfig setPorts(String[] ports) {
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

    public String[] getDataVolumesFromLaunchConfigs() {
        return dataVolumesFromLaunchConfigs;
    }

    public ServiceLaunchConfig setDataVolumesFromLaunchConfigs(String[] dataVolumesFromLaunchConfigs) {
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
