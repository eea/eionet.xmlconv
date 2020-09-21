package eionet.gdem.services.fme;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class ApacheHttpClientUtils {


    public static Map<String, String> convertHttpEntityToMap(HttpEntity entity) throws IOException {
        String jsonStr = EntityUtils.toString(entity);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(jsonStr, Map.class);
        return map;
    }
}
