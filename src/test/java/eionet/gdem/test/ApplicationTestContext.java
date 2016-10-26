package eionet.gdem.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


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
 * The Original Code is Conversion and QA Service.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */
@Configuration
@ImportResource({"/test-spring-app-context.xml",
        "/test-datasource-context.xml", "/test-runtime.xml"})
public class ApplicationTestContext {

}
