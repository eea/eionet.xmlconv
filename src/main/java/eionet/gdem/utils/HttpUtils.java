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
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.utils;

import java.io.IOException;
import java.io.InputStream;

import eionet.gdem.http.HttpDefaultClientFactory;
import org.apache.commons.io.IOUtils;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP Utilities.
 * @author Enriko Käsper, Tieto Estonia HttpUtils
 */

public final class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * Private constructor to deal with reflection
     */
    private HttpUtils() {
        throw new AssertionError();
    }

    /**
     * Downloads remote file
     * @param url URL
     * @return Downloaded file
     * @throws DCMException If an error occurs.
     * @throws IOException If an error occurs.
     */
    public static byte[] downloadRemoteFile(String url) throws DCMException, IOException {
        byte[] responseBody = null;
        CloseableHttpClient client = HttpDefaultClientFactory.getInstance();

        HttpGet method = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.error("Method failed: " + response.getStatusLine().getReasonPhrase());
                throw new DCMException(BusinessConstants.EXCEPTION_SCHEMAOPEN_ERROR, response.getStatusLine().getReasonPhrase());
            }
            InputStream instream = entity.getContent();
            responseBody = IOUtils.toByteArray(instream);
        } catch (IOException e) {
            LOGGER.error("Fatal transport error: " + e.getMessage());
            throw e;
        } finally {
            response.close();
            method.releaseConnection();
        }
        return responseBody;
    }

    /**
     * Method checks whether the resource behind the given URL exist. The method calls HEAD request and if the resonse code is 200,
     * then returns true. If exception is thrown or response code is something else, then the result is false.
     *
     * @param url URL
     * @return True if resource behind the url exists.
     */
    public static boolean urlExists(String url) {

        CloseableHttpClient client = HttpDefaultClientFactory.getInstance();
        HttpHead method = new HttpHead(url);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode == HttpStatus.SC_OK;
        } catch (IOException e) {
            LOGGER.error("Fatal transport error: " + e.getMessage());
            return false;
        } finally {
            method.releaseConnection();
            try {
                response.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }
}
