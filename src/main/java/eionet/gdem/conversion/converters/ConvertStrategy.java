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

import eionet.gdem.XMLConvException;
import eionet.gdem.Properties;
import eionet.gdem.qa.engines.SaxonProcessor;
import eionet.gdem.utils.Utils;
import net.sf.saxon.s9api.*;


import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

//import org.apache.fop.apps.Driver;

/**
 *
 * Abstract class defining the procedure for XML conversions. Extending classes should implement convert() method for output
 * type specific conversions.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */
public abstract class ConvertStrategy {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertStrategy.class);

    /** System folder where XSL files are stored. */
    public String xslFolder = Properties.getXslFolder() + File.separatorChar;
    /** System folder where supporting XML files are stored. */
    public String tmpFolder = Properties.getTmpFolder() + File.separatorChar; // props.getString("tmp.folder");
    /** XSL transformer parameter name containing XML files folder path. */
    public static final String XML_FOLDER_URI_PARAM = "xml_folder_uri";
    /** XSL transformer parameter name containing DD domain URL. */
    public static final String DD_DOMAIN_PARAM = "dd_domain";

    /** Map of parameters sent to XSL transformer. */
    private Map<String, String> xslParams = null;
    /** Absolute path to XSL file. */
    private String xslPath;

    /**
     * Method for converting XML source to output stream using XSLT stream.
     * @param source InputStream containing source XML.
     * @param xslt InputStream containing XSL content.
     * @param result OutputStream for conversion result.
     * @param cnvFileExt File extension for conversion result.
     * @return Preferred file name for conversion result.
     * @throws XMLConvException In case of unexpected XML or XSL errors.
     * @throws Exception In case of unexpected system error.
     */
    public abstract String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt)
            throws XMLConvException, Exception;

    /**
     * Sets the map of xsl global parameters for this strategy.
     * @param map Map of String key value pairs.
     */
    public void setXslParams(Map<String, String> map) {
        this.xslParams = map;
    }

    /**
     * Method transforms XML source using XSL stream.
     * @param in InputStream containing source XML.
     * @param xslStream InputStream containing XSL content.
     * @param out OutputStream for conversion result.
     * @throws XMLConvException In case of unexpected XML or XSL errors.
     */
    protected void runXslTransformation(InputStream in, InputStream xslStream, OutputStream out) throws XMLConvException {
        try {
            Processor proc = SaxonProcessor.getProcessor();
            XsltCompiler comp = proc.newXsltCompiler();
            TransformerErrorListener errors = new TransformerErrorListener();
            StreamSource transformerSource = new StreamSource(xslStream);
            if (getXslPath() != null) {
                transformerSource.setSystemId(getXslPath());
            }
            XsltExecutable exp = comp.compile(transformerSource);
            XdmNode source = proc.newDocumentBuilder().build(new StreamSource(in));
            Serializer ser = proc.newSerializer(out);
            //ser.setOutputProperty(Serializer.Property.METHOD, "html");
            //ser.setOutputProperty(Serializer.Property.INDENT, "yes");
            XsltTransformer trans = exp.load();
            trans.setInitialContextNode(source);
            trans.setParameter(new QName(DD_DOMAIN_PARAM), new XdmAtomicValue(eionet.gdem.Properties.ddURL));
            setTransformerParameters(trans);
            trans.setErrorListener(errors);
            trans.setDestination(ser);
            trans.transform();

        //} catch (TransformerConfigurationException tce) {
        //    throw new XMLConvException("Error transforming XML - incorrect stylesheet file: " + tce.toString(), tce);
        //} catch (TransformerException tfe) {
        //    throw new XMLConvException("Error transforming XML - it's not probably well-formed xml file: " + tfe.toString(), tfe);
        //} catch (Throwable th) {
        //    LOGGER.error("Error " + th.toString(), th);
        //    th.printStackTrace(System.out);
        //    throw new XMLConvException("Error transforming XML: " + th.toString());
        } catch (SaxonApiException e) {
            throw new XMLConvException("Error transforming XML: " + e.getMessage(), e);
        }
    }

    /**
     * Method transforms XML source to PDF using XSL-FO stream.
     * @param in InputStream containing source XML.
     * @param xsl InputStream containing XSL-FO content.
     * @param out OutputStream for conversion result.
     * @throws XMLConvException In case of unexpected XML or XSL errors.
     * @throws IOException In case of unexpected XML or XSL errors.
     * @throws SAXException In case of unexpected XML or XSL errors.
     */
    protected void runFOPTransformation(InputStream in, InputStream xsl, OutputStream out) throws XMLConvException, IOException, SAXException {
        FopFactory fopFactory = FopFactory.newInstance(new File("fop.xconf"));
        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            Processor proc = SaxonProcessor.getProcessor();
            XsltCompiler comp = proc.newXsltCompiler();
            TransformerErrorListener errors = new TransformerErrorListener();
            StreamSource transformerSource = new StreamSource(xsl);
            if (getXslPath() != null) {
                transformerSource.setSystemId(getXslPath());
            }
            XsltExecutable exp = comp.compile(transformerSource);
            XdmNode source = proc.newDocumentBuilder().build(new StreamSource(in));
            Serializer ser = proc.newSerializer(out);
            //ser.setOutputProperty(Serializer.Property.METHOD, "html");
            //ser.setOutputProperty(Serializer.Property.INDENT, "yes");
            XsltTransformer trans = exp.load();
            trans.setErrorListener(errors);
            trans.setInitialContextNode(source);
            trans.setDestination(ser);
            long l = System.currentTimeMillis();
            setTransformerParameters(trans);
            trans.transform();

            //Result res = new SAXResult(fop.getDefaultHandler());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug((new StringBuilder()).append("generate: transformation needed ").append(System.currentTimeMillis()
                        - l).append(" ms").toString());
            }

        //} catch (TransformerConfigurationException tce) {
        //    throw new XMLConvException("Error transforming XML to PDF - incorrect stylesheet file: " + tce.toString(), tce);
        //} catch (TransformerException tfe) {
        //    throw new XMLConvException("Error transforming XML to PDF - it's not probably well-formed xml file: " + tfe.toString(),
        //            tfe);
        } catch (SaxonApiException e) {
            LOGGER.error("Error " + e.toString(), e);
            throw new XMLConvException("Error transforming XML to PDF " + e.toString());
        }
    }

    /**
     * Sets the map of xsl global parameters to xsl transformer.
     * @param transformer XSL transformer object.
     */
    private void setTransformerParameters(XsltTransformer transformer) {

        if (xslParams == null) {
            return;
        }

        Iterator<String> keys = xslParams.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = xslParams.get(key);
            if (value != null) {
                transformer.setParameter(new QName(key), new XdmAtomicValue(value));
            }
        }

        // sets base URI for xmlfiles uploaded into xmlconv
        String xmlFilePathURI = Utils.getURIfromPath(eionet.gdem.Properties.xmlfileFolder, true);

        if (xmlFilePathURI != null) {
            transformer.setParameter(new QName(XML_FOLDER_URI_PARAM), new XdmAtomicValue(xmlFilePathURI));
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
