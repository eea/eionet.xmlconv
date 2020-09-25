package eionet.gdem.services.fme.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eionet.gdem.Properties;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SynchronousSubmitJobRequest extends SubmitJobRequest {

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
        super(xmlSourceFile);
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

    @Override
    public String buildJsonBody() {
        JSONObject folderObj=new JSONObject();
        folderObj.put(NAME_KEY, FOLDER_VALUE);
        folderObj.put(VALUE_KEY, this.fmeResultFolderProperty + "/" +folderName);

        JSONObject envelObj=new JSONObject();
        envelObj.put(NAME_KEY, ENVELOPE_VALUE_PARAM);
        envelObj.put(VALUE_KEY, xmlSourceFile);

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(folderObj);
        jsonArray.put(envelObj);

        JSONObject result = new JSONObject();
        result.put("publishedParameters",jsonArray);
       return  result.toString();
    }


}
