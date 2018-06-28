package eionet.gdem.utils.cdr;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class UrlUtilsTest {

    @Test
    public void getFileNameUnix() {
        String fileName = UrlUtils.getFileName("/home/gso/eea/eionet.xmlconv/target/test-classes/tmp/tmp_1530020988647/seed-general-report.html");
        assertEquals("seed-general-report.html", fileName);
    }

    @Test
    @Ignore
    // TODO: fix test in linux
    public void getFileNameWindows() {
        String fileName = UrlUtils.getFileName("C:\\dev\\reportnet\\eionet.xmlconv\\target\\test-classes\\tmp\\tmp_1530017616429\\seed-general-report.html");
        assertEquals("seed-general-report.html", fileName);
    }

    @Test
    public void getFileNameHttp() {
        String fileName = UrlUtils.getFileName("http://localhost:58081/seed-rivers.xls");
        assertEquals("seed-rivers.xls", fileName);
    }
}