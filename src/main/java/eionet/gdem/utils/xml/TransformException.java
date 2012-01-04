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
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Dusan Popovic (ED)
 */

package eionet.gdem.utils.xml;

public class TransformException extends Exception {
    /**
     * Constructs a new <code>TransformException</code>
     */
    public TransformException() {
    }

    /**
     * Constructs a new <code>TransformException</code> with specified detail message.
     *
     * @param msg
     *            the error message.
     */
    public TransformException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new <code>TransformException</code> with specified nested <code>Throwable</code>.
     *
     * @param nested
     *            the exception or error that caused this exception to be thrown.
     */
    public TransformException(Throwable nested) {
        super(nested);
    }

    /**
     * Constructs a new <code>TransformException</code> with specified detail message and nested <code>Throwable</code>.
     *
     * @param msg
     *            the error message.
     * @param nested
     *            the exception or error that caused this exception to be thrown.
     */
    public TransformException(String msg, Throwable nested) {
        super(msg, nested);
    }
}
