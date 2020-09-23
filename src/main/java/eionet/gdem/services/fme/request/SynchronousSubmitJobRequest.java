package eionet.gdem.services.fme.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eionet.gdem.Properties;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SynchronousSubmitJobRequest {

   private String xmlSourceFile;
    private String folderName;
    ArrayList<NameValuePair> postParameters;
    public static final String NAME_KEY="name";
    public static final String VALUE_KEY="value";
    public static final String FOLDER_VALUE="folder";
    public static final String ENVELOPE_VALUE_PARAM="envelopepath";
    public static final String ENCODING_ENTITY_TYPE="UTF-8";
    private String fmeResultFolderProperty = Properties.fmeResultFolder;

    public SynchronousSubmitJobRequest(String xmlSourceFile, String folderName) {
        this.xmlSourceFile = xmlSourceFile;
        this.folderName = folderName;
    }

    public UrlEncodedFormEntity build() throws UnsupportedEncodingException {
        postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair(NAME_KEY, FOLDER_VALUE));
        postParameters.add(new BasicNameValuePair(VALUE_KEY, this.fmeResultFolderProperty + "/" +folderName));
        postParameters.add(new BasicNameValuePair(NAME_KEY, ENVELOPE_VALUE_PARAM));
        postParameters.add(new BasicNameValuePair(VALUE_KEY, xmlSourceFile));
        ArrayList<NameValuePair> postParametersfINALL = new ArrayList<>();
   //     postParametersfINALL.add(new BasicNameValuePair("publishedParameters",postParameters));
    return new UrlEncodedFormEntity(postParameters, ENCODING_ENTITY_TYPE);
    }

    public String buildJsonString() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode internalNode = mapper.createObjectNode();
        internalNode.put(NAME_KEY, FOLDER_VALUE);
        internalNode.put(VALUE_KEY, this.fmeResultFolderProperty + "/" +folderName);
        internalNode.put(NAME_KEY, ENVELOPE_VALUE_PARAM);
        internalNode.put(VALUE_KEY, xmlSourceFile);
        ObjectNode publishedParametersNode = mapper.createObjectNode();
        publishedParametersNode.set("publishedParameters",internalNode);
        return mapper.writeValueAsString(publishedParametersNode);
    }
}
