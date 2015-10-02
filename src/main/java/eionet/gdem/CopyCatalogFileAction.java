package eionet.gdem;

import eionet.propertyplaceholderresolver.CircularReferenceException;
import eionet.propertyplaceholderresolver.ConfigurationPropertyResolver;
import eionet.propertyplaceholderresolver.UnresolvedPropertyException;
import eionet.propertyplaceholderresolver.util.ConfigurationLoadException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class CopyCatalogFileAction {

    private static final Logger LOGGER = Logger.getLogger(CopyCatalogFileAction.class.getName());

    public CopyCatalogFileAction(ConfigurationPropertyResolver configurationPropertyResolver) throws UnresolvedPropertyException, CircularReferenceException, ConfigurationLoadException, IOException {
        String target = configurationPropertyResolver.resolveValue("catalogs");
        String resource = this.getClass().getClassLoader().getResource("catalog.xml").getFile();
        copyFile(resource, target);

    }

    void copyFile(String source, String target) throws IOException {
        File sourceFile = new File(source);
        File targetDirectory = new File(target);
        FileUtils.copyFile(sourceFile, targetDirectory);
        LOGGER.log(Level.INFO, "Successfully copied directory...{0}", target);
    }

}

