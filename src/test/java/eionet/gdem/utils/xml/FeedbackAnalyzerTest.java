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
 * Agency.  Portions created by Tripledev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Kaido Laine
 */
package eionet.gdem.utils.xml;

import java.util.HashMap;

import junit.framework.TestCase;
import eionet.gdem.Constants;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

public class FeedbackAnalyzerTest extends TestCase {

    public void testFeedBackFileERROR() {
        HashMap<String, String> fbResult = FeedbackAnalyzer.getFeedbackResultFromFile(TestUtils.getSeedURL(TestConstants.SEED_FEEDBACKANALYZE_TEST, this));

        String fbStatus = fbResult.get(Constants.RESULT_FEEDBACKSTATUS_PRM);
        String fbMsg = fbResult.get(Constants.RESULT_FEEDBACKMESSAGE_PRM);
        assertTrue(fbStatus.equals("BLOCKER"));
        assertTrue(fbMsg.equals("This delivery is not acceptable because it contains fatal errors."));
    }

    public void testFeedbackStringWithNoAttributes() {

        String qaResult = "<div style=\"font-size:13px;\" class=\"feedbacktext\" >"
                + "<h1>Header</h1><div>This is text</div>"
                + "</div>";


        HashMap<String, String> fbResult = FeedbackAnalyzer.getFeedbackResultFromStr(qaResult);

        String fbStatus = fbResult.get(Constants.RESULT_FEEDBACKSTATUS_PRM);

        assertTrue(fbStatus.equals(Constants.XQ_FEEDBACKSTATUS_UNKNOWN));


    }


    public void testFeedbackStringWarning() {

        String qaResult = "<span><div style=\"font-size:13px;\" class=\"WARNING\" id=\"feedbackStatus\">There are some warnings</div>"
                + "<h1>Header</h1><div>This is text</div>"
                + "</span>";


        HashMap<String, String> fbResult = FeedbackAnalyzer.getFeedbackResultFromStr(qaResult);

        String fbStatus = fbResult.get(Constants.RESULT_FEEDBACKSTATUS_PRM);

        assertTrue(fbStatus.equals("WARNING"));

    }
}
