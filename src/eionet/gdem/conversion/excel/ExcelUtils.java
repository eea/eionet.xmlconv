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
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem.conversion.excel;

public class ExcelUtils {

  /**
  * returns a valid ExcelConversionHandlerIF
  */
  public static ExcelConversionHandlerIF getExcelConversionHandler(){
    return new ExcelConversionHandler();
  }
  /**
  * returns a valid ExcelStyleIF
  */
  public static ExcelStyleIF getExcelStyle(){
    return new ExcelStyle();
  }
  /**
  * returns a valid ExcelReaderIF
  */
  public static ExcelReaderIF getExcelReader(){
    return new ExcelReader();
  }
}