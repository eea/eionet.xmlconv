/*
 * Created on 17.03.2008
 */
package eionet.gdem.test;

import eionet.gdem.Properties;
import eionet.gdem.datadict.DDServiceClient;
import org.junit.Ignore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS TestUtils
 */
@Ignore
public class TestUtils {

    /**
     * Set up test runtime properties
     *
     * @param obj
     */
    public static void setUpProperties(Object obj) {
        // Fix Mock Dataset or remove completely.
        // GDEMServices.setTestConnection(true);
        Properties.metaXSLFolder = Properties.appRootFolder + "/dcm";
        Properties.convFile = Properties.metaXSLFolder + "/conversions.xml";
    }

    public static File getContextDirectory() {
        return new File(Properties.appRootFolder + "/webapp");
    }

    /**
     * construct URI from seed file name
     *
     * @param seedName
     *            eg. "seed.xml"
     * @return
     */
    public static String getSeedURLOld(String seedName, Object obj) {
        String filename = obj.getClass().getClassLoader().getResource(seedName).getFile();
        return "file://".concat(filename);
    }

    public static String getSeedURL(String seedName, Object obj) {
        return Properties.getStringProperty("test.httpd.url").concat(seedName);
    }

    public static String getLocalURL(String filename) {
        return Properties.getStringProperty("test.httpd.url").concat(filename);
    }

    /**
     * Construct path from seed file name.
     *
     * @param seedName
     *            eg. "seed.xml"
     * @return
     */
    public static String getSeedPath(String seedName, Object obj) {

        String filename = obj.getClass().getClassLoader().getResource(seedName).getFile();
        return filename;
    }

    // conversion service checks if the dataset is the latest released verison
    // otherwise conversion fails
    public static void setUpReleasedDataset() {
        Map<String, String> mockDataset = new HashMap<String, String>();
        mockDataset.put("id", "4948");
        mockDataset.put("status", "Released");
        mockDataset.put("isLatestReleased", "true");
        mockDataset.put("dateOfLatestReleased", "");
        mockDataset.put("idOfLatestReleased", "");
        DDServiceClient.setMockDataset(mockDataset);

    }
}
