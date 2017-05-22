package eionet.gdem.data.schemata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 *
 *
 */
public class SchemaFormatter implements Formatter<Schema> {

    @Autowired
    private SchemaService schemaService;

    @Override
    public Schema parse(String s, Locale locale) throws ParseException {
        return schemaService.findById(Integer.parseInt(s));
    }

    @Override
    public String print(Schema schema, Locale locale) {
        return schema.getId().toString();
    }
}
