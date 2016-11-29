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

/**
 * XMLConv Exception class.
 */
public class XMLConvException extends Exception {
    /**
     * Constructs a new exception with null as its detail message.
     */
    public XMLConvException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param s Exception message
     */
    public XMLConvException(String s) {
        super(s);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param s Exception message
     * @param cause Exception cause
     */
    public XMLConvException(String s, Throwable cause) {
        super(s, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of
     * (cause==null ? null : cause.toString())
     * (which typically contains the class and detail message of cause).
     * @param cause Exception cause
     */
    public XMLConvException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message, cause,
     * suppression enabled or disabled, and writable stack trace enabled or disabled.
     * @param s Exception message
     * @param cause Exception cause
     * @param b supression enabled
     * @param b1 writable stack trace enabled
     */
    protected XMLConvException(String s, Throwable cause, boolean b, boolean b1) {
        super(s, cause, b, b1);
    }
}
