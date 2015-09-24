/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eionet.gdem.configuration;

import eionet.acl.AccessController;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public final class PropertySerializer {

    private static final Logger LOGGER = Logger.getLogger(PropertySerializer.class.getName());
    private final Set<String> resourceNames;
    private final ConfigurationService configurationService;

    public PropertySerializer(Set<String> resourceNames, ConfigurationService configurationService) throws ConfigurationException {
        this.resourceNames = resourceNames;
        this.configurationService = configurationService;
    }

    URL getResourceURL(String resourceName) {
        return this.getClass().getClassLoader().getResource(resourceName);
    }

    Properties getProperties(String resourceName) throws ConfigurationException {
        return (new PropertiesConfigurationResourceProvider(resourceName)).get();
    }

    void serializeProperties(Properties properties, String filename) throws IOException {
        LOGGER.log(Level.INFO, "Attempting to serialize properties to filepath: {0}", filename);
        properties.store(new OutputStreamWriter(new FileOutputStream(new File(filename))), null);
        
    }

    public void serialize() throws ConfigurationException {

        for (String resourceName : resourceNames) {
            try {
                URL resourceUrl = getResourceURL(resourceName);
                String filename = resourceUrl.getFile();
                Properties properties = getProperties(resourceName);
                for (Object o : properties.keySet()) {
                    String key = (String) o;
                    String value = configurationService.get(key);
                    properties.put(key, value);
                }
                serializeProperties(properties, filename);
                LOGGER.log(Level.INFO, "Successfully serialized property file located at: {0}", properties);
            } catch (ConfigurationException ex) {
                Logger.getLogger(PropertySerializer.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConfigurationException(ex.getMessage());
            } catch (NullPointerException ex) {
                Logger.getLogger(PropertySerializer.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConfigurationException(ex.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(PropertySerializer.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConfigurationException(ex.getMessage());
            } catch (UnResolvedPropertyException ex) {
                Logger.getLogger(PropertySerializer.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConfigurationException(ex.getMessage());
            }
            
        }
    }
}
