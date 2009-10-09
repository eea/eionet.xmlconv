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

import java.io.InputStream;
import java.util.Hashtable;

import eionet.gdem.GDEMException;

/**
* The main class, which is calling POI HSSF methods for reading Excel file
* @author Enriko Käsper
*/
public interface ExcelReaderIF 
{

/**
* If the excel file is generated from Data Dictionary,
* then it finds the XML Shema from Excel file
* @return - XML Schema URL
*/
public String getXMLSchema();

/**
* Initialize the Excel Workbook from InputStream
* @param InputStream input - input Excel file
*/
public void initReader(InputStream input) throws GDEMException;


/**
* Goes through the Excel worksheets and writes the data into DD_XMLInstance as xml
* @param DD_XMLInstance instance - XML instance file, where the structure xml has been efined before
*/
public void readDocumentToInstance(DD_XMLInstance instance)throws GDEMException;
/**
* Finds the first sheet name, that is not DO_NOT_DELETE_THIS_SHEET 
* @return - Excel sheet name
*/
public String getFirstSheetName();
/**
* If the excel file is generated from Data Dictionary,
* then it finds the XML Shemas for each Excel sheet
* @return - Excel sheet name
*/
public Hashtable getSheetSchemas();

/**
* Check if sheet has data or not
* @param sheet_name - Excel sheet name
* @return boolean - true if has data
*/
public boolean isEmptySheet(String sheet_name);
	  
}
