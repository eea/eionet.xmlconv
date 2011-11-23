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
 *        Enriko Käsper
 */

package eionet.gdem.qa.functions;

import junit.framework.TestCase;

/**
 * Test Json to XML conversion methods.
 * 
 * @author Enriko Käsper
 */
public class JsonTest extends TestCase {

    public void testJsonString2xml() {
        String resultXmlSubstr = "<element>json</element><element>is</element><element>easy</element>";
        String xml = Json.jsonString2xml("['json','is','easy']");
        assertTrue(xml.contains(resultXmlSubstr));
    }

    public void testJsonString2xmlInvalid() {
        String resultXmlSubstr = "<code>400</code>";
        String xml = Json.jsonString2xml("this is not a json string");
        assertNotNull(xml);
        assertTrue(xml.contains(resultXmlSubstr));
    }

    public void testJson2xmlError() {
        String resultXmlSubstr = "<root><error><code>500</code><details/><message>Error Executing Task.</message></error></root>";
        String json = "{ \"error\" : {\"code\" : 500, \"message\" : \"Error Executing Task.\", \"details\" : [] }}";
        String xml = Json.jsonString2xml(json);
        assertNotNull(xml);
        assertTrue(xml.contains(resultXmlSubstr));
    }

    public void testJson2xmlResult() {
        String resultXmlSubstr =
            "<results><element><dataType>GPString</dataType><paramName>outputPointXML</paramName><value>&lt;fields&gt;&lt;field id=\"FR_985461\" code=\"IT\" status=\"FR\" x=\"3811754.01892\" y=\"2884556.77332\"/&gt;&lt;/fields&gt;</value></element></results>";
        String json =
            "{ \"results\" : [ { \"paramName\" : \"outputPointXML\", \"dataType\" : \"GPString\", \"value\" : \"<fields><field id="
            + "\\\"FR_985461\\\" code=\\\"IT\\\" status=\\\"FR\\\" x=\\\"3811754.01892\\\" y=\\\"2884556.77332\\\"\\/><\\/fields>\" }], \"messages\" : [    ]}";
        String xml = Json.jsonString2xml(json);
        assertNotNull(xml);
        assertTrue(xml.contains(resultXmlSubstr));
    }

}
