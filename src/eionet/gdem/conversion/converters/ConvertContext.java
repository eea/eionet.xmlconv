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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ConvertContext {

    private InputStream source;
    private Object xsl;
    private OutputStream result;
    private String cnvFileExt;

    public ConvertContext(InputStream source, Object xsl, OutputStream result, String cnvFileExt) {
        this.cnvFileExt = cnvFileExt;
        this.result = result;
        this.source = source;
        this.xsl = xsl;
    }

    public String executeConversion(ConvertStartegy converter) throws Exception {
        if (xsl instanceof String)
            xsl = new FileInputStream((String) xsl);
        return converter.convert(source, (InputStream) xsl, result, cnvFileExt);
    }

}
