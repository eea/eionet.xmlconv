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
 * The Original Code is "EINRC-7 / GDEM Project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (c) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem.services;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;


/**
 * Interface for logger
 *
 * Date:    27.04.04<BR>
 *
 * @author  Rando Valt
 * @version $Revision: 1.1 $
 */

class LoggerModule implements LoggerIF {
  
  
  //private static GDEMLogger logger;
  Category logger;
  

  
/**
 * Package local method for initializing Logger. Does not do anything as the com.tee.xmlserver.Logger
 * will be initialized by the servlet container.
 */
  LoggerModule() {
    logger = Category.getInstance("gdem");
  }
  
  private Priority convSeverity(int severity) {
    switch (severity) {
      case EMERGENCY:
        return Priority.FATAL;
      case ERROR:
        return Priority.ERROR;
      case WARNING:
        return Priority.WARN;
      case INFO:
        return Priority.INFO;
      case DEBUG:
      default:
        return Priority.DEBUG;
    }
  }
/**
 * Guard function to decide, whether the message of the given level shoul;d be logged.<BR><BR>
 *
 * Log level values can be between 1 and 5: 1 is the most silent, 5 the most talkative.
 */
  public boolean enable(int level)  {
    return logger.isEnabledFor(convSeverity(level));
  }
/**
 * Logs debug level message.
 */
  public void debug(Object msg) {
    logger.debug(msg);
  }
  
  public void debug(Object msg, Throwable t)  {
    logger.debug(msg, t);
  }
  
/**
 * Logs info level message.
 */
  public void info(Object msg)  {
    logger.info(msg);
  }
  
  public void info(Object msg, Throwable t) {
    logger.info(msg, t);
  }

/**
 * Logs debug warning message.
 */
  public void warning(Object msg) {
    logger.warn(msg);
  }
  
  public void warning(Object msg, Throwable t)  {
    logger.warn(msg, t);
  }

/**
 * Logs error level message.
 */
  public void error(Object msg) {
    logger.error(msg);
  }
  
  public void error(Object msg, Throwable t)  {
    logger.error(msg);
  }
  
/**
 * Logs error level message.
 */
  public void fatal(Object msg) {
    logger.fatal(msg);
  }
  
  public void fatal(Object msg, Throwable t)  {
    logger.fatal(msg);
  }
}

