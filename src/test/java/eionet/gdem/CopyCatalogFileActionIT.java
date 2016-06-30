package eionet.gdem;

import eionet.gdem.test.ApplicationTestContext;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class CopyCatalogFileActionIT {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void copyCatalogTest() throws IOException {
        String target = Properties.getStringProperty("catalogs");
        if (FileUtils.getFile(target).exists()) {
            FileUtils.deleteQuietly(FileUtils.getFile(target));
        }
        CopyCatalogFileAction action = new CopyCatalogFileAction();
    }

    @Test
    public void catalogExistingTest() throws IOException {
        String target = Properties.getStringProperty("catalogs");
        if (FileUtils.getFile(target).exists()) {
            FileUtils.deleteQuietly(FileUtils.getFile(target));
        }
        CopyCatalogFileAction action = new CopyCatalogFileAction();
        action = new CopyCatalogFileAction();
    }


}