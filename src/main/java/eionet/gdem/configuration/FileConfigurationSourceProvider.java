/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eionet.gdem.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
class FileConfigurationSourceProvider implements ConfigurationResourceProvider<Properties> {

    private final String path;

    public FileConfigurationSourceProvider(String path) {
        this.path = path;
    }

    @Override
    public Properties get() throws ConfigurationException {
        Properties p = new Properties();
        try {
            InputStream inStream = new FileInputStream(path);
            p.load(inStream);
        } catch (IOException e) {
            throw new ConfigurationException("Error while trying to load properties from " + path);
        }
        return p;
    }

}
