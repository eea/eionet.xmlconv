/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.utils.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Async stream reader.
 * @author Enriko Käsper, Tieto Estonia AsyncStreamReader
 */

class AsyncStreamReader extends Thread {
    private StringBuffer fBuffer = null;
    private InputStream fInputStream = null;
    private String fThreadId = null;
    private boolean fStop = false;
    private ILogDevice fLogDevice = null;

    private String fNewLine = null;

    /**
     * Constructor
     * @param inputStream InputStream
     * @param buffer Buffer
     * @param logDevice Log device
     * @param threadId Thread Id
     */
    public AsyncStreamReader(InputStream inputStream, StringBuffer buffer, ILogDevice logDevice, String threadId) {
        fInputStream = inputStream;
        fBuffer = buffer;
        fThreadId = threadId;
        fLogDevice = logDevice;

        fNewLine = System.getProperty("line.separator");
    }

    public String getBuffer() {
        return fBuffer.toString();
    }

    /**
     * Runs stream reader.
     * TODO: possibility of adding logging
     */
    public void run() {
        try {
            readCommandOutput();
        } catch (Exception ex) {
            // ex.printStackTrace(); //DEBUG
        }
    }

    /**
     * Reads command output
     * @throws IOException If an error occurs.
     */
    private void readCommandOutput() throws IOException {
        BufferedReader bufOut = new BufferedReader(new InputStreamReader(fInputStream));
        String line = null;
        while ((!fStop) && ((line = bufOut.readLine()) != null)) {
            fBuffer.append(line + fNewLine);
            printToDisplayDevice(line);
        }
        bufOut.close();
        // printToConsole("END OF: " + fThreadId); //DEBUG
    }

    /**
     * Stops reading.
     */
    public void stopReading() {
        fStop = true;
    }

    /**
     * Prints to display device
     * @param line Line to print
     */
    private void printToDisplayDevice(String line) {
        if (fLogDevice != null)
            fLogDevice.log(line);
        else {
            // printToConsole(line);//DEBUG
        }
    }

    /**
     * Prints to console
     * TODO: remove maybe
     * @param line Line
     */
    private synchronized void printToConsole(String line) {
        System.out.println(line);
    }
}
