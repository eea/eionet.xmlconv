/*
 * Created on 18.03.2008
 */
package eionet.gdem.test.mocks;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;

/**
 * The class mocks ServletInputStream class.
 * It overwrites some methods to be able to use ByteArrayInputStream for holding data and validating HTTP requests.
 * @author Enriko Käsper, TietoEnator Estonia AS
 * MockServletInputStream
 */

public class MockServletInputStream extends ServletInputStream {
    ByteArrayInputStream is=null;
    public MockServletInputStream(ByteArrayInputStream is){
        this.is=is;
    }
    public int read() throws IOException {
        return is.read();
    }
    public int read(byte b[]) throws IOException {
        return is.read(b);
    }
    public int read(byte b[], int off, int len) throws IOException {
        return is.read(b,off,len);
    }
}
