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
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper (TripleDev)
 */

package eionet.gdem.validation;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import eionet.gdem.dto.ValidateDto;

/**
 * Callback methods for validation errors. Store errors in the list of ValidateDto objects.
 *
 * @author Enriko Käsper, TripleDev
 */
public class ValidatorErrorHandler extends DefaultHandler {

    /** List of errors. */
    private List<ValidateDto> errContainer = new ArrayList<ValidateDto>();

    @Override
    public void warning(SAXParseException ex) throws SAXException {
        addError(ValidatorErrorType.WARNING, ex);
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        addError(ValidatorErrorType.ERROR, ex);
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        addError(ValidatorErrorType.FATAL_ERROR, ex);
    }

    /**
     * Create ValidateDto object from the SAX error and add it to the error container.
     * @param type ERROR, WARNING or FATAL_ERROR
     * @param ex SAXParseException
     */
    private void addError(ValidatorErrorType type, SAXParseException ex) {
        ValidateDto val = new ValidateDto();
        val.setType(type);
        val.setDescription(ex.getMessage());
        val.setColumn(ex.getColumnNumber());
        val.setLine(ex.getLineNumber());

        errContainer.add(val);
    }

    /**
     * Get the list of errors found by XML Schema validator.
     * @return List of ValidateDto objects
     */
    public List<ValidateDto> getErrors() {
        return errContainer;
    }

}
