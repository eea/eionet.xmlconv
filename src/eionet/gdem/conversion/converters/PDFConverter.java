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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eionet.gdem.GDEMException;

public class PDFConverter extends ConvertStartegy {

    public String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws GDEMException, Exception {
        String pdfFile = tmpFolder + "gdem_" + System.currentTimeMillis() + ".pdf";
        if (result != null)
            runFOPTransformation(source, xslt, result);
        else {
            try {
                runFOPTransformation(source, xslt, new FileOutputStream(pdfFile));
            } catch (IOException e) {
                throw new GDEMException("Error creating PDF output file " + e.toString(), e);
            }
        }
        return pdfFile;
    }

}
