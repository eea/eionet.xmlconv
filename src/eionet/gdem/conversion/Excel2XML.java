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

package eionet.gdem.conversion;


import eionet.gdem.conversion.excel.ExcelUtils;

/**
* This class is returns MS Excel specific handlers for DDXMLConverter
* @author Enriko Käsper
*/

public class Excel2XML extends DDXMLConverter
{
	private final static String FORMAT_NAME = "MS Excel";

	public SourceReaderIF getSourceReader(){
		return ExcelUtils.getExcelReader();
	}
	public String getSourceFormatName(){
		return FORMAT_NAME;
	}



  public static void main(String[] args){
    //String excelFile = "E:/Projects/gdem/public/test.xls";
  	//String excelFile = "E:/Projects/gdem/tmp/Summer_ozone.xls";
    //String excelFile = E\\Projects\\gdem\\exelToXML\\Groundwater_GG_CCxxx.xls";
	  /*
  	String excelFile = "E:/Projects/gdem/public/tmp/Rivers.xls";
    String outFile = "E:\\Projects\\gdem\\tmp\\Instance1925_.xml";
    try{
      Excel2XML processor = new Excel2XML();
      //processor.convertDD_XML_split(excelFile,outFile);
      processor.convertDD_XML_split(excelFile, null);
    }
    catch(Exception e){
      System.out.println(e.toString());
    }
    */
  }
}
