package eionet.gdem.configuration;

import java.io.File;
import java.io.IOException;

import eionet.gdem.Properties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copy Catalog class.
 * @author Ervis Zyka
 */
public class CopyCatalogFileAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyCatalogFileAction.class.getName());

    /**
     * Default constructor
     * @throws IOException If an error occurs.
     */
    public CopyCatalogFileAction() throws IOException {
        String target = Properties.catalogPath;
        if (new File(target).exists()) {
            return;
        }
        String resource = this.getClass().getClassLoader().getResource("catalog.xml").getFile();
        copyFile(resource, target);
    }

    /**
     * Copy file
     * @param source Source
     * @param target Target
     * @throws IOException If an error occurs.
     */
    void copyFile(String source, String target) throws IOException {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        FileUtils.copyFile(sourceFile, targetFile);
        LOGGER.info("Successfully copied file...{0}", target);
    }

}

