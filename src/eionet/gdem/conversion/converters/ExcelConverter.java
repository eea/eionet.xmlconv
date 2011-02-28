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
 *    Original code: Nedeljko Pavlovic (ED)
 *    							 Alfeldi Istvan (ED) 
 */

package eionet.gdem.conversion.converters;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.ExcelProcessor;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

public class ExcelConverter extends ConvertStartegy {
	private static LoggerIF _logger = GDEMServices.getLogger();

	public String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws GDEMException, Exception {
		String xmlFile = tmpFolder + "gdem_out" + System.currentTimeMillis() + ".xml";
		String excelFile = tmpFolder + "gdem_" + System.currentTimeMillis() + ".xls";
		try {
			runXslTransformation(source, xslt, new FileOutputStream(xmlFile));
			ExcelProcessor ep = new ExcelProcessor();
			if (result != null)
				ep.makeExcel(xmlFile, result);
			else
				ep.makeExcel(xmlFile, excelFile);

			try {
				Utils.deleteFile(xmlFile);
			} catch (Exception e) {
				_logger.error("Couldn't delete the result file: " + xmlFile, e);
			}

		} catch (FileNotFoundException e) {
			_logger.error("Error " + e.toString(), e);
			throw new GDEMException("Error transforming Excel " + e.toString(), e);
		}
		return excelFile;
	}

}
