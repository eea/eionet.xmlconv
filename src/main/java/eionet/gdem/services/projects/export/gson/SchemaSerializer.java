package eionet.gdem.services.projects.export.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import eionet.gdem.data.schemata.Schema;

import java.lang.reflect.Type;
import java.util.List;

/**
 *
 *
 */
public class SchemaSerializer implements JsonSerializer<Schema> {

    @Override
    public JsonElement serialize(Schema schema, Type type, JsonSerializationContext jsonSerializationContext) {
        return null;
    }
}
