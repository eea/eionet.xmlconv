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

package eionet.gdem.services;

/**
 * Log4j implementation is used, if an error occurs, output is written to System.err
 */
public interface LoggerIF {

    public static final int DEBUG = 5;
    public static final int INFO = 4;
    public static final int WARNING = 3;
    public static final int ERROR = 2;
    public static final int EMERGENCY = 1;

    /**
     * Guard function to decide, whether the message of the given level shoul;d be logged.<BR>
     * <BR>
     * 
     * Log level values can be between 1 and 5: 1 is the most silent, 5 the most talkative.
     */
    public boolean enable(int level);

    /**
     * Logs debug level message.
     */
    public void debug(Object msg);

    public void debug(Object msg, Throwable t);

    /**
     * Logs info level message.
     */
    public void info(Object msg);

    public void info(Object msg, Throwable t);

    /**
     * Logs debug warning message.
     */
    public void warning(Object msg);

    public void warning(Object msg, Throwable t);

    /**
     * Logs error level message.
     */
    public void error(Object msg);

    public void error(Object msg, Throwable t);

    /**
     * Logs error level message.
     */
    public void fatal(Object msg);

    public void fatal(Object msg, Throwable t);
}
