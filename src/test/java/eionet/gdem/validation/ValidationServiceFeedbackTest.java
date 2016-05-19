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

import eionet.gdem.Properties;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.qa.QAFeedbackType;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * Test ValidationServiceFeedback methods.
 *
 * @author Enriko Käsper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ValidationServiceFeedbackTest {
    @Test
    public void testBlockerFeedback() {

        ValidationServiceFeedback feedback = new ValidationServiceFeedback();
        String feedbackText = feedback.formatFeedbackText("The test contains errors", QAFeedbackType.ERROR, true);
        assertTrue(feedbackText.contains("<span id=\"feedbackStatus\" class=\"BLOCKER\""));

        ValidationServiceFeedback feedback2 = new ValidationServiceFeedback();
        String feedbackText2 = feedback2.formatFeedbackText("The test contains errors", QAFeedbackType.ERROR, false);
        assertTrue(feedbackText2.contains("<span id=\"feedbackStatus\" class=\"ERROR\""));

    }
    @Test
    public void testFeedbackIsXml() throws Exception {

        List<ValidateDto> validationErrors = new ArrayList<ValidateDto>();
        ValidateDto error = new ValidateDto();
        error.setColumn(11);
        error.setLine(12);
        error.setDescription("Unknown error <>&'|");
        error.setType(ValidatorErrorType.WARNING);
        validationErrors.add(error);

        ValidationServiceFeedback feedback = new ValidationServiceFeedback();
        feedback.setValidationErrors(validationErrors);
        String feedbackText = feedback.formatFeedbackText(false);
        assertTrue(feedbackText.contains("<span id=\"feedbackStatus\" class=\"WARNING\""));

        IXmlCtx ctx = new XmlContext();
        ctx.checkFromString(feedbackText);
        IXQuery xQuery = ctx.getQueryManager();
        assertEquals(xQuery.findElementById("feedbackStatus").getTextContent(), Properties.getMessage("label.validation.result.warning"));

    }

    public void testContainsOnlyWarnings() throws Exception {

        List<ValidateDto> validationErrors = new ArrayList<ValidateDto>();
        ValidateDto error = new ValidateDto();
        error.setType(ValidatorErrorType.WARNING);
        validationErrors.add(error);

        ValidateDto error2 = new ValidateDto();
        error2.setType(ValidatorErrorType.WARNING);
        validationErrors.add(error2);

        ValidationServiceFeedback feedback = new ValidationServiceFeedback();
        feedback.setValidationErrors(validationErrors);
        assertTrue(feedback.validationContainsOnlyWarnings());

        ValidateDto error3 = new ValidateDto();
        error3.setType(ValidatorErrorType.FATAL_ERROR);
        validationErrors.add(error3);

        feedback.setValidationErrors(validationErrors);
        assertFalse(feedback.validationContainsOnlyWarnings());
    }
}
