package eionet.gdem;

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

    public CopyCatalogFileAction( ) throws IOException  {
        
        String target = Properties.getStringProperty("catalogs");
        if ( new File(target ) .exists() ) {
            return;
        }
        String resource = this.getClass().getClassLoader().getResource("catalog.xml").getFile();
        copyFile(resource, target);

    }

    void copyFile(String source, String target) throws IOException {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        FileUtils.copyFile(sourceFile, targetFile);
        LOGGER.log(Level.INFO, "Successfully copied file...{0}", target);
    }

}

