/*
 * Created on 18.03.2008
 */
package eionet.gdem.test.mocks;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 * The class mocks ServletOutputStream class. It overwrites some methods to be able to use ByteArrayOutputStream for writing data
 * and validating HTTP response.
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS MockServletOutputStream
 */

public class MockServletOutputStream extends ServletOutputStream {
    ByteArrayOutputStream os = null;

    public MockServletOutputStream(ByteArrayOutputStream os) {
        this.os = os;
    }

    public void write(int b) {
        os.write(b);
    }

    public String toString() {
        return os.toString();
    }

    public byte[] toByteArray() {
        return os.toByteArray();
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }
}
