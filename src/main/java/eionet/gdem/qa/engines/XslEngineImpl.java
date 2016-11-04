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
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.qa.engines;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;




import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.converters.ConvertContext;
import eionet.gdem.conversion.converters.ConvertStrategy;
import eionet.gdem.conversion.converters.XMLConverter;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.cdr.UrlUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XSL engine implementation.
 * @author Enriko Käsper, Tieto Estonia XslEngineImpl
 */

public class XslEngineImpl extends QAScriptEngineStrategy {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(XslEngineImpl.class);

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {

        FileInputStream fisXsl = null;
        String tmpXslFile = null;
        InputStream sourceStream = null;
        HttpFileManager fileManager = new HttpFileManager();
        try {
            // build InputSource for xsl
            if (!Utils.isNullStr(script.getScriptSource())) {
                tmpXslFile = Utils.saveStrToFile(null, script.getScriptSource(), "xsl");
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                fisXsl = new FileInputStream(script.getScriptFileName());
            } else {
                throw new XMLConvException("XQuery engine could not find script source or script file name!");
            }
            // Build InputSource for xml file
            sourceStream = fileManager.getFileInputStream(script.getSrcFileUrl(), null, false);
            // execute xsl transformation

            ConvertContext ctx =
                new ConvertContext(sourceStream, tmpXslFile == null ? script.getScriptFileName() : tmpXslFile,
                        result, null);
            ConvertStrategy cs = new XMLConverter();

            Map<String, String> params = UrlUtils.getCdrParams(script.getSrcFileUrl());
            params.put(Constants.XQ_SOURCE_PARAM_NAME, script.getOrigFileUrl());
            cs.setXslParams(params);
            ctx.executeConversion(cs);

            if (tmpXslFile != null) {
                Utils.deleteFile(tmpXslFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("==== Caught EXCEPTION " + e.toString());
            throw new XMLConvException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(sourceStream);
            fileManager.closeQuietly();
            IOUtils.closeQuietly(fisXsl);
        }

    }
}
