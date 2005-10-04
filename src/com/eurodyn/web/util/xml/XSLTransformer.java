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
 *    Original code: Dusan Popovic (ED) 
 *                          Nedeljko Pavlovic (ED)
 */

package com.eurodyn.web.util.xml;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.eurodyn.web.util.Streams;

public class XSLTransformer {

    public XSLTransformer() {
        synchronized (mutex) {
            try {
                ResourceBundle props = ResourceBundle.getBundle("xml");
                String jaxSaxFactoryProp = props.getString(JAX_SAX_PARSER_PROPERTY);
                String jaxDomFactoryProp = props.getString(JAX_DOM_PARSER_PROPERTY);
                String jaxTransformFactoryProp = props.getString(JAX_TRANSFORM_PROPERTY);
                if (transformerFactory == null) {
                    System.setProperty(JAX_TRANSFORM_PROPERTY, jaxTransformFactoryProp);
                    System.setProperty(JAX_SAX_PARSER_PROPERTY, jaxSaxFactoryProp);
                    System.setProperty(JAX_DOM_PARSER_PROPERTY, jaxDomFactoryProp);

                    TransformerFactory tFactory = TransformerFactory.newInstance();
                    domFactory = DocumentBuilderFactory.newInstance();
                    domFactory.setValidating(false);
                    saxFactory = SAXParserFactory.newInstance();
                    saxFactory.setValidating(false);
                    // if (!tFactory.getFeature(SAXTransformerFactory.FEATURE))
                    // { throw new TransformException(
                    // "Invalid SAX Tranformer. Doesn't support SAX"); }
                    transformerFactory = ((SAXTransformerFactory) tFactory);
                }

            } catch (MissingResourceException mre) {
            	mre.printStackTrace();
            }

        }
    }

    private static DocumentBuilderFactory domFactory = null;

    private static SAXParserFactory saxFactory = null;

    private static SAXTransformerFactory transformerFactory = null;

    //
    // JAXP Service Configuration
    //
    /*
     private final static String CONFIG_JAX_FACTORY_SAX = "jax.factory.sax";

     private final static String jaxSaxFactoryProp = "org.apache.xerces.jaxp.SAXParserFactoryImpl";

     private final static String CONFIG_JAX_FACTORY_DOM = "jax.factory.dom";

     private final static String jaxDomFactoryProp = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";

     private final static String CONFIG_JAX_FACTORY_TRANSFORM = "jax.factory.transform";

     //private final static String jaxTransformFactoryProp = "org.apache.xalan.processor.TransformerFactoryImpl";
     private final static String jaxTransformFactoryProp = "net.sf.saxon.TransformerFactoryImpl";
     */

    private final static Object mutex = new Object();

    //
    // JAXP System Wide Properties
    //
    private static final String JAX_TRANSFORM_PROPERTY = "javax.xml.transform.TransformerFactory";

    private static final String JAX_SAX_PARSER_PROPERTY = "javax.xml.parsers.SAXParserFactory";

    private static final String JAX_DOM_PARSER_PROPERTY = "javax.xml.parsers.DocumentBuilderFactory";

    // DTD Map
    static private Map dtds = new HashMap();

    public void transform(String xslt, InputSource inputSource, OutputStream os, Map parameters) throws TransformException {
        if (xslt == null) {
            try { // if no stylesheet specified simply drain the stream
                Streams.drain(inputSource.getByteStream(), os);
            } catch (IOException e) {
                throw new TransformException(e);
            }
        } else {
			java.io.FileInputStream is;
			try {
				is = new java.io.FileInputStream(xslt);
			} catch (FileNotFoundException e) {
                throw new TransformException(e);
			}
	        InputStreamReader ssreader = new InputStreamReader(is);
            transformStream(xslt, ssreader, inputSource,  new StreamResult(os), parameters);
        }
    }

    public void transform(String xslt, InputSource inputSource, Writer writer, Map parameters) throws TransformException {
        if (xslt == null) {
            try { // if no stylesheet specified simply drain the stream
                Streams.drain(inputSource.getByteStream(), writer);
            } catch (IOException e) {
                throw new TransformException(e);
            }
        } else {
			java.io.FileInputStream is;
			try {
				is = new java.io.FileInputStream(xslt);
			} catch (FileNotFoundException e) {
                throw new TransformException(e);
			}
	        InputStreamReader ssreader = new InputStreamReader(is);
            transformStream(xslt, ssreader, inputSource, new StreamResult(writer), parameters);
        }
    }
	
	public void transform(String xsltName, String xslContent, InputSource inputSource, OutputStream os, Map parameters) throws TransformException {
		  ByteArrayInputStream bais=new ByteArrayInputStream(xslContent.getBytes());
          InputStreamReader ssreader = new InputStreamReader(bais);
		  transformStream(xsltName, ssreader, inputSource,  new StreamResult(os), parameters);
		  
	}
	
	
	/**
	 * 
	 * @param xsltName
	 * @param xslIs
	 * @param is
	 * @param os
	 * @param parameters
	 * @throws TransformException
	 */
	public void transform(String xsltName, InputStream xslIs, InputStream is, OutputStream os, Map parameters) throws TransformException {
        InputStreamReader ssreader = new InputStreamReader(xslIs);
		  transformStream(xsltName, ssreader, new InputSource(is),  new StreamResult(os), parameters);
		  
	}
	
	

    private void transformStream(String xslt, InputStreamReader xslReader, InputSource inputSource, StreamResult streamResult, Map parameters) throws TransformException {
        if (xslt == null) {
            throw new TransformException("Invalid Transform, no stylesheet set!");
        }
        /*
         try
         {
         java.io.FileInputStream is = new java.io.FileInputStream(xslt);
         StreamSource ss = new StreamSource(is, xslt );
         
         Transformer transformer = transformerFactory.newTransformer(ss);
         transformer.setURIResolver(this);
         //
         // Get the transform variables (parameters)
         //
         Iterator keys = parameters.keySet().iterator();
         while (keys.hasNext())
         {
         String name = (String) keys.next();
         String value = (String) parameters.get(name);
         transformer.setParameter(name, value); 
         }
         
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         URL url = new URL(inputSource.getSystemId());
         Streams.drain(url.openStream(), baos);
         
         transformer.transform(new StreamSource(new ByteArrayInputStream(baos.toByteArray())), streamResult);
         }
         catch (Exception e)
         {
         throw new TransformException("Error in Stylesheet: " + e.toString());
         }
         */

        //
        // create a new document builder to load the XML file for transformation
        //
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = domFactory.newDocumentBuilder();
            docBuilder.setEntityResolver(new TransformDTDEntityResolver(dtds));

        } catch (ParserConfigurationException e) {
            throw new TransformException("Failed to load JAX Document Builder: " + e.toString());
        }

        try {
            // Create a ContentHandler to handle parsing of the stylesheet.
            TemplatesHandler templatesHandler = transformerFactory.newTemplatesHandler();

            // Create an XMLReader and set its ContentHandler.
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(templatesHandler);

            // Set it to solve Entities via cache
            reader.setEntityResolver(new TransformDTDEntityResolver(dtds));


            // Parse the stylesheet.
            final InputSource xstyle = new InputSource(xslReader);
            xstyle.setSystemId(xslt);
            reader.parse(xstyle);

            //Get the Templates object from the ContentHandler.
            Templates templates = templatesHandler.getTemplates();

            // Create a ContentHandler to handle parsing of the XML source.
            TransformerHandler handler = transformerFactory.newTransformerHandler(templates);

            // Reset the XMLReader's ContentHandler.
            reader.setContentHandler(handler);

            //
            // Parse the Document into a DOM tree
            // 
            //
            org.w3c.dom.Document doc = docBuilder.parse(inputSource);

            // reader.setProperty("http://xml.org/sax/properties/lexical-handler",
            // handler);

            final Transformer processor = handler.getTransformer();

            if(parameters!=null) {
                //
                // Get the transform variables (parameters)
                //
                Iterator keys = parameters.keySet().iterator();
                while (keys.hasNext()) {
                    String name = (String) keys.next();
                    String value = (String) parameters.get(name);
                    processor.setParameter(name, value);
                }
            }

            //
            // do the transformation now
            //
            processor.transform(new DOMSource(doc), streamResult);

        } catch (Exception e) {
            throw new TransformException(e);
        }

    }

}