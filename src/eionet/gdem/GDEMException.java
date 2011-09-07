/**
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
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem;

import java.io.PrintStream;
import java.io.PrintWriter;

public class GDEMException extends Exception {
    /**
     * The cause for this exception.
     */
    protected Exception cause = null;

    public GDEMException(String msg) {
        super(msg);
    }

    public GDEMException(String msg, Exception cause) {
        super(msg);
        this.cause = cause;
    }

    /**
     * Prints this exception and its backtrace to the standard error stream.
     */
    public void printStackTrace() {
        super.printStackTrace();

        if (this.cause != null) {
            this.cause.printStackTrace();
        }
    }

    /**
     * Prints this exception and its backtrace to the given print stream.
     * 
     * @param ps
     *            the print stream.
     */
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);

        if (this.cause != null) {
            this.cause.printStackTrace(ps);
        }
    }

    /**
     * Prints this exception and its backtrace to the given print writer.
     * 
     * @param pw
     *            - the print writer.
     */
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (this.cause != null) {
            this.cause.printStackTrace(pw);
        }

    }

    /**
     * Returns the message of original cause.
     * 
     * @return cause message
     */
    public String getCauseMessage() {

        if (cause != null && cause.getMessage() != null) {
            return cause.getMessage();
        }
        return "";
    }

}
