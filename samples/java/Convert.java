import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;



/**
 * Sample java code for calling ConversionService convert method
 * Requires  http-client-3.1 and commons-codec-2.1 libraries 
 *
 * @author Enriko Käsper, TietoEnator Estonia AS
 * Convert
 */

public class Convert {

	private final static String server_url = "http://80.235.29.171:8080/xmlconv/api";

	public static void convert(String convert_url, String convert_id){
		String method_path = "/convert";
		
		// Create an instance of HttpClient.
	    HttpClient client = new HttpClient();

	    // Create a method instance.
	    PostMethod post = new PostMethod(server_url.concat(method_path));
	    try{
	        NameValuePair[] data = {
	          new NameValuePair("url", convert_url),
	          new NameValuePair("convert_id", convert_id)
	        };
	        post.setRequestBody(data);
	        // execute method and handle any error responses.
	        int statusCode = client.executeMethod(post);

	        // handle response.
	        if (statusCode != HttpStatus.SC_OK) {
	          System.err.println("Method failed: " + post.getStatusLine());
	        }

	        // Read the response body.
	        byte[] responseBody = post.getResponseBody();

	        // Deal with the response.
	        System.out.println("Content-type: " + post.getResponseHeader("Content-type").getValue());
	        System.out.println("Content-disposition:" + post.getResponseHeader("Content-disposition").getValue());
	        System.out.println(responseBody);
	        
	        

	        } catch (HttpException e) {
		      System.err.println("Fatal protocol violation: " + e.getMessage());
		      e.printStackTrace();
		    } catch (IOException e) {
		      System.err.println("Fatal transport error: " + e.getMessage());
		      e.printStackTrace();
		    } finally {
		      // Release the connection.
		      post.releaseConnection();
		    }
	}
	public static void main(String args[]) {
		String convert_url = "https://svn.eionet.europa.eu/repositories/Reportnet/Dataflows/HabitatsDirectiveArticle17/xmlfiles/general-instancefile.xml";
		String convert_id = "26";

		Convert.convert(convert_url,convert_id);
	}
}
