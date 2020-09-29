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

    private String fmeResultFolderProperty = Properties.fmeResultFolder;

    public SynchronousSubmitJobRequest(String xmlSourceFile, String folderName) {
        super(xmlSourceFile);
        this.xmlSourceFile = xmlSourceFile;
        this.folderName = folderName;
    }


    @Override
    public String buildBody() {
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
