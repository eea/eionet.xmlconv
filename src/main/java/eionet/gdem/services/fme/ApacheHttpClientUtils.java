package eionet.gdem.services.fme;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class ApacheHttpClientUtils {


    public static JSONObject getJsonFromResponseEntity(HttpEntity entity) throws IOException ,JSONException {
        String jsonStr = EntityUtils.toString(entity);
        JSONObject jsonResponse = new org.json.JSONObject(jsonStr);
        return jsonResponse;
    }
}
