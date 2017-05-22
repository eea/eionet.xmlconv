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
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */
package eionet.gdem.web.spring.stylesheet;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import eionet.gdem.utils.xml.sax.SaxContext;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.utils.xml.IXmlCtx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Utility methods for Add and Edit stylesheet actions.
 *
 * @author Enriko Käsper
 */
public class ConversionsUtils {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsUtils.class);


    /**
     * Creates a new Stylesheet dto object and fills the properties with values inserted to web form.
     * @param form StylesheetForm
     * @param httpServletRequest HTTP servlet request object.
     * @return eionet.gdem.dto.Stylesheet object
     */
    public static Stylesheet convertFormToStylesheetDto(StylesheetForm form, HttpServletRequest httpServletRequest) {

        Stylesheet stylesheet = new Stylesheet();
        stylesheet.setConvId(form.getStylesheetId());
        stylesheet.setDescription(form.getDescription());
        stylesheet.setType(form.getOutputtype());
        stylesheet.setDependsOn(form.getDependsOn());
        stylesheet.setXslFileName(form.getXslFileName());
        stylesheet.setXslContent(form.getXslContent());

        if (httpServletRequest.getParameterValues("newSchemas") != null) {
            form.setNewSchemas(httpServletRequest.getParameterValues("newSchemas"));
            stylesheet.setSchemaUrls(form.getNewSchemas());
        }
        if (httpServletRequest.getParameterValues("schemaIds") != null) {
            stylesheet.setSchemaIds(Arrays.asList(httpServletRequest.getParameterValues("schemaIds")));
        }
        return stylesheet;
    }

    /**
     * Check the well-formedness of uploaded/inserted XSL file.
     * If the file is not well formed XML, then adds an error ito the list of Struts AcitonMessages.
     *
     * @param stylesheet Stylehseet dto
     * @param errors Struts ActionMessages
     */
    public static void validateXslFile(Stylesheet stylesheet, SpringMessages errors) {
        try {
            IXmlCtx x = new SaxContext();
            x.setWellFormednessChecking();
            x.checkFromString(stylesheet.getXslContent());
        } catch (Exception e) {
            LOGGER.error("stylesheet not valid", e);
            // TODO change to use messaging service
            //("label.stylesheet.error.notvalid")
            errors.add("Stylesheet content is not valid");
        }
    }
}
