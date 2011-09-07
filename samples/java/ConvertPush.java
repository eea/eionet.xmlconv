import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * Sample java code for calling ConversionService convertPush method Requires http-client-3.1 and commons-codec-2.1 libraries
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS Convert
 */

public class ConvertPush {

    private final static String server_url = "http://80.235.29.171:8080/xmlconv/api";

    public void convertPush(String convert_file, String convert_id) {
        String method_path = "/convertPush";

        // NB the source file has to be in the same directory or modify the path info

        String convert_file_path = getClass().getClassLoader().getResource(convert_file).getFile();
        File targetFile = new File(convert_file_path);
        // Create an instance of HttpClient.
        HttpClient client = new HttpClient();

        // Create a method instance.
        PostMethod filePost = new PostMethod(server_url.concat(method_path));
        try {
            Part[] parts = {new FilePart("convert_file", targetFile), new StringPart("convert_id", convert_id)};
            filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
            // execute method and handle any error responses.
            int statusCode = client.executeMethod(filePost);

            // handle response.
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + filePost.getStatusLine());
            }

            // Read the response body.
            byte[] responseBody = filePost.getResponseBody();

            // Deal with the response.
            System.out.println("Content-type: " + filePost.getResponseHeader("Content-type").getValue());
            System.out.println("Content-disposition:" + filePost.getResponseHeader("Content-disposition").getValue());
            System.out.println(responseBody);

        } catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Release the connection.
            filePost.releaseConnection();
        }
    }

    public static void main(String args[]) {
        String convert_file = "seed-general-report.xml";
        String convert_id = "26";
        ConvertPush cp = new ConvertPush();
        cp.convertPush(convert_file, convert_id);
    }
}
