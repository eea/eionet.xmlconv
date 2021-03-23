package eionet.gdem.services.fme.request;

import eionet.gdem.Properties;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetResultFilesFromFolderAsZipRequest extends FMERequest {
    private String fmeResultFolderProperty = Properties.fmeResultFolder;
    private String folderName;


    public GetResultFilesFromFolderAsZipRequest(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public String buildBody() {
        JSONObject folderObj=new JSONObject();
        folderObj.put(NAME_KEY, FOLDER_VALUE);
        folderObj.put(VALUE_KEY, this.fmeResultFolderProperty + "/" +folderName);

        JSONObject envelObj=new JSONObject();
        envelObj.put(NAME_KEY, ENVELOPE_VALUE_PARAM);
        envelObj.put(VALUE_KEY, "xmlSourceFile");

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(folderObj);
        jsonArray.put(envelObj);

        JSONObject result = new JSONObject();
        result.put("publishedParameters",jsonArray);
        return  result.toString();
    }
}
