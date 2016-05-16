package eionet.gdem.acl;

import eionet.gdem.Properties;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
public class CopyAclFiles {

    private static final Logger LOGGER = Logger.getLogger(CopyAclFiles.class.getName());
    // Copy acl files at startup
    public CopyAclFiles( ) throws IOException, URISyntaxException   {
        String appHome = Properties.getStringProperty("app.home");
        copyFiles(appHome);
    }
    
    void copyFiles ( String target) throws URISyntaxException, IOException  {
        
        target = target + "/acl/";
        URL sourceURL = this.getClass().getClassLoader().getResource("acl/");
        
        File sourceFolder = new File(sourceURL.toURI());
        
        File[] files = sourceFolder.listFiles();
            for(File file:files){
                if ( file.getName().contains(".prms") || file.getName().contains(".permissions")  || ! ( (new File(target + (file.getName() ) ) .exists() ) ) ) {
                    copyFile( file.toURI().toURL() ,target + (file.getName() ) );
                }
            }            
        
    }
    void copyFile(URL source, String target) throws IOException, URISyntaxException {
        File sourceFile = new File(source.toURI());
        File targetDirectory = new File(target);
        org.apache.commons.io.FileUtils.copyFile(sourceFile, targetDirectory);
        LOGGER.log(Level.INFO, "Successfully copied file...{0}", target);
    }
}
 

    


