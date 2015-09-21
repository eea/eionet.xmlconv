package eionet.gdem.configuration;

interface ConfigurationResourceProvider<T> {

    T get() throws ConfigurationException;
}
