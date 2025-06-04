package eionet.gdem;

import eionet.gdem.configuration.CopyCatalogFileAction;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

/**
 * @author George Sofianos
 */
public class CopyCatalogFileActionIT {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void copyCatalogTest() throws IOException {
        String target = Properties.catalogPath;
        if (FileUtils.getFile(target).exists()) {
            FileUtils.deleteQuietly(FileUtils.getFile(target));
        }
        CopyCatalogFileAction action = new CopyCatalogFileAction();
    }

    @Test
    public void catalogExistingTest() throws IOException {
        String target = Properties.catalogPath;
        if (FileUtils.getFile(target).exists()) {
            FileUtils.deleteQuietly(FileUtils.getFile(target));
        }
        CopyCatalogFileAction action = new CopyCatalogFileAction();
        action = new CopyCatalogFileAction();
    }


}