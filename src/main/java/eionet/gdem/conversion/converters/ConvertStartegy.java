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
 *                                 Alfeldi Istvan (ED)
 */

package eionet.gdem.conversion.converters;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.Driver;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.XSLTransformer;

public abstract class ConvertStartegy {

    /** */
    private static final Log LOGGER = LogFactory.getLog(ConvertStartegy.class);

    public String xslFolder = Properties.xslFolder + File.separatorChar; // props.getString("xsl.folder");
    public String tmpFolder = Properties.tmpFolder + File.separatorChar; // props.getString("tmp.folder");
    public static final String XML_FOLDER_URI_PARAM = "xml_folder_uri";
    public static final String DD_DOMAIN_PARAM = "dd_domain";

    /** Map of paramters sent to XSL transformer. */
    private Map<String, String> xslParams = null;
    /** Absolute path to XSL file. */
    private String xslPath;
    private static XSLTransformer transform = new XSLTransformer();

    public abstract String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt)
            throws GDEMException, Exception;

    /**
     * sets the map of xsl global paramters for this strategy.
     * @param map Map of String key value pairs.
     */
    public void setXslParams(Map<String, String> map) {
        this.xslParams = map;
    }

    protected void runXslTransformation(InputStream in, InputStream xslStream, OutputStream out) throws GDEMException {
        try {
            TransformerFactory tFactory = transform.getTransformerFactoryInstance();
            TransformerErrorListener errors = new TransformerErrorListener();
            tFactory.setErrorListener(errors);

            StreamSource transformerSource = new StreamSource(xslStream);
            if (getXslPath() != null) {
                transformerSource.setSystemId(getXslPath());
            }

            Transformer transformer = tFactory.newTransformer(transformerSource);
            transformer.setErrorListener(errors);

            transformer.setParameter(DD_DOMAIN_PARAM, Properties.ddURL);
            setTransformerParameters(transformer);
            long l = System.currentTimeMillis();
            transformer.transform(new StreamSource(in), new StreamResult(out));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug((new StringBuilder()).append("generate: transformation needed ").append(System.currentTimeMillis()
                        - l).append(" ms").toString());
            }
        } catch (TransformerConfigurationException tce) {
            throw new GDEMException("Error transforming XML - incorrect stylesheet file: " + tce.toString(), tce);
        } catch (TransformerException tfe) {
            throw new GDEMException("Error transforming XML - it's not probably well-formed xml file: " + tfe.toString(), tfe);
        } catch (Throwable th) {
            LOGGER.error("Error " + th.toString(), th);
            th.printStackTrace(System.out);
            throw new GDEMException("Error transforming XML: " + th.toString());
        }
    }

    protected void runFOPTransformation(InputStream in, InputStream xsl, OutputStream out) throws GDEMException {
        try {
            Driver driver = new Driver();
            driver.setRenderer(Driver.RENDER_PDF);
            driver.setOutputStream(out);
            Result res = new SAXResult(driver.getContentHandler());
            Source src = new StreamSource(in);
            TransformerFactory transformerFactory = transform.getTransformerFactoryInstance();
            TransformerErrorListener errors = new TransformerErrorListener();

            transformerFactory.setErrorListener(errors);
            StreamSource transformerSource = new StreamSource(xsl);
            if (getXslPath() != null) {
                transformerSource.setSystemId(getXslPath());
            }

            Transformer transformer = transformerFactory.newTransformer(transformerSource);
            setTransformerParameters(transformer);
            transformer.setErrorListener(errors);

            long l = System.currentTimeMillis();
            transformer.transform(src, res);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug((new StringBuilder()).append("generate: transformation needed ").append(System.currentTimeMillis()
                        - l).append(" ms").toString());
            }

        } catch (TransformerConfigurationException tce) {
            throw new GDEMException("Error transforming XML to PDF - incorrect stylesheet file: " + tce.toString(), tce);
        } catch (TransformerException tfe) {
            throw new GDEMException("Error transforming XML to PDF - it's not probably well-formed xml file: " + tfe.toString(), tfe);
        } catch (Throwable e) {
            LOGGER.error("Error " + e.toString(), e);
            throw new GDEMException("Error transforming XML to PDF " + e.toString());
        }
    }

    /**
     * Sets the map of xsl global parameters to xsl transformer.
     * @param transformer XSL transformer object.
     */
    private void setTransformerParameters(Transformer transformer) {

        if (xslParams == null) {
            return;
        }

        Iterator<String> keys = xslParams.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = xslParams.get(key);
            if (value != null) {
                transformer.setParameter(key, value);
            }
        }

        // sets base URI for xmlfiles uploaded into xmlconv
        String xmlFilePathURI = Utils.getURIfromPath(eionet.gdem.Properties.xmlfileFolder, true);

        if (xmlFilePathURI != null) {
            transformer.setParameter(XML_FOLDER_URI_PARAM, xmlFilePathURI);
        }

    }

    /**
     * @return the xslPath
     */
    public String getXslPath() {
        return xslPath;
    }

    /**
     * @param xslPath the xslPath to set
     */
    public void setXslPath(String xslPath) {
        this.xslPath = xslPath;
    }

}
