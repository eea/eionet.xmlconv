package eionet.gdem.conversion.access;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author George Sofianos
 */
public class ConversionTableMetadataTest {

    @Test
    public void dummyCoverage() {
        ConversionTableMetadata table = new ConversionTableMetadata();
        table.setDstIdf("1");
        table.setDstNr("2");
        table.setDstNsID("3");
        table.setDstNsURL("4");
        table.setDstSchemaLocation("5");
        table.setDstSchemaURL("6");
        table.setDstsNsID("7");
        table.setDstsNsURL("8");
        table.setTblsNamespaces("9");
        assertEquals("Wrong result: ", "1", table.getDstIdf());
        assertEquals("Wrong result: ", "2", table.getDstNr());
        assertEquals("Wrong result: ", "3", table.getDstNsID());
        assertEquals("Wrong result: ", "4", table.getDstNsURL());
        assertEquals("Wrong result: ", "5", table.getDstSchemaLocation());
        assertEquals("Wrong result: ", "6", table.getDstSchemaURL());
        assertEquals("Wrong result: ", "7", table.getDstsNsID());
        assertEquals("Wrong result: ", "8", table.getDstsNsURL());
        assertEquals("Wrong result: ", "9", table.getTblsNamespaces());
        assertNotNull("Wrong result:", table.toString());
    }

}