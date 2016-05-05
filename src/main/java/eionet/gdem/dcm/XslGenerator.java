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
 */

package eionet.gdem.dcm;

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.converters.TransformerErrorListener;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.cache.MemoryCache;
import net.sf.saxon.s9api.*;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class XslGenerator {

    public static MemoryCache MemCache = new MemoryCache(10000, 10);

    public static ByteArrayInputStream convertXML(String xmlURL, String conversionURL) throws GDEMException, Exception {
        String cacheId = xmlURL + "_" + conversionURL;
        byte[] result = (byte[]) MemCache.getContent(cacheId);
        if (result == null) {
            result = makeDynamicXSL(xmlURL, conversionURL);
            MemCache.put(cacheId, result, Integer.MAX_VALUE);
        }
        return new ByteArrayInputStream(result);
    }

    private static byte[] makeDynamicXSL(String sourceURL, String xslFile) throws GDEMException {
        InputFile src = null;
        byte[] result = null;
        try {
            src = new InputFile(sourceURL);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            Processor proc = new Processor(false);
            XsltCompiler comp = proc.newXsltCompiler();
            TransformerErrorListener errors = new TransformerErrorListener();
            StreamSource transformerSource = new StreamSource(xslFile);
            transformerSource.setSystemId(xslFile);

            XsltExecutable exp = comp.compile(transformerSource);
            XdmNode source = proc.newDocumentBuilder().build(new StreamSource(sourceURL));
            Serializer ser = proc.newSerializer(os);
            ser.setOutputProperty(Serializer.Property.METHOD, "html");
            ser.setOutputProperty(Serializer.Property.INDENT, "yes");
            XsltTransformer trans = exp.load();
            trans.setInitialContextNode(source);
            trans.setParameter(new QName("dd_domain"), new XdmAtomicValue(eionet.gdem.Properties.ddURL));

            trans.setErrorListener(errors);
            trans.setDestination(ser);
            trans.transform();

            result = os.toByteArray();
        } catch (MalformedURLException mfe) {
            throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
        } catch (IOException ioe) {
            throw new GDEMException("Error opening URL " + ioe.toString(), ioe);
        } catch (Exception e) {
            throw new GDEMException("Error converting: " + e.toString(), e);
        } finally {
            try {
                if (src != null) {
                    src.close();
                }
            } catch (Exception e) {
            }
        }
        return result;
    }

}
