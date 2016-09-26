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

package eionet.gdem.utils.xml.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlException;
import eionet.gdem.utils.xml.XmlSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

/**
 * XML Serialization class.
 * @author Unknown
 * @author George Sofianos
 * XXX: Methods are deprecated, replace ASAP.
 */
public class XmlSerialization implements XmlSerializer {

    private XMLSerializer xmlSerializer;
    private IXmlCtx ctx = null;

    /**
     * Default constructor.
     */
    public XmlSerialization() {
        xmlSerializer = new XMLSerializer();
        OutputFormat outputFormat = new OutputFormat();
        //outputFormat.setAllowJavaNames(true);
        outputFormat.setEncoding("UTF-8");
        outputFormat.setVersion("1.0");
        outputFormat.setIndenting(true);
        outputFormat.setIndent(5);
        xmlSerializer.setOutputFormat(outputFormat);
    }

    /**
     * Constructor
     * @param ctx XML Context
     */
    public XmlSerialization(IXmlCtx ctx) {
        this();
        this.ctx = ctx;
    }

    @Override
    public ByteArrayOutputStream serializeToOutStream() throws XmlException {
        ByteArrayOutputStream byteOutputStream = null;
        try {
            byteOutputStream = new ByteArrayOutputStream();
            xmlSerializer.setOutputByteStream(byteOutputStream);
            xmlSerializer.serialize(ctx.getDocument());
        } catch (IOException ioe) {
            throw new XmlException("Error occurred while serializing XML document. Reason: " + ioe.getMessage());
        } finally {
        }
        return byteOutputStream;
    }

    @Override
    public void serializeToFs(String fullFileName) throws XmlException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fullFileName);
            xmlSerializer.setOutputByteStream(fileOutputStream);
            xmlSerializer.serialize(ctx.getDocument());
        } catch (IOException ioe) {
            throw new XmlException("Error occurred while serializing XML document. Reason: " + ioe.getMessage());
        }
        finally{
            IOUtils.closeQuietly(fileOutputStream);
        }
    }

    @Override
    public ByteArrayInputStream serializeToInStream() throws XmlException {
        ByteArrayInputStream byteInputStream = null;
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            xmlSerializer.setOutputByteStream(byteOutputStream);
            xmlSerializer.serialize(ctx.getDocument());
            byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
            byteOutputStream.flush();
            byteOutputStream.close();
        } catch (IOException ioe) {
            throw new XmlException("Error occurred while serializing XML document . Reason: " + ioe.getMessage());
        } finally {
        }
        return byteInputStream;
    }

    @Override
    public String serializeToString() throws XmlException {

        StringWriter stringOut;
        try {
            OutputFormat format = new OutputFormat(ctx.getDocument());
            format.setOmitXMLDeclaration(true);
            stringOut = new StringWriter();
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.serialize(ctx.getDocument());

        } catch (IOException ioe) {
            throw new XmlException("Error occurred while serializing XML document . Reason: " + ioe.getMessage());
        }
        return stringOut.toString();
    }

}
