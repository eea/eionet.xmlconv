package eionet.gdem.data.schemata;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


/**
 *
 */
@Converter(autoApply=true)
public class SchemaLanguageConverter implements AttributeConverter<SchemaLanguage, Integer> {
    @Override
    public Integer convertToDatabaseColumn(SchemaLanguage schemaLanguage) {
        switch (schemaLanguage) {
            case EMPTY:
                return 1;
            case XSD:
                return 2;
            case DTD:
                return 3;
            case EXCEL:
                return 4;
            default:
                throw new IllegalArgumentException("Unknown " + schemaLanguage);
        }
    }

    @Override
    public SchemaLanguage convertToEntityAttribute(Integer number) {
        switch (number) {
            case 1:
                return SchemaLanguage.EMPTY;
            case 2:
                return SchemaLanguage.XSD;
            case 3:
                return SchemaLanguage.DTD;
            case 4:
                return SchemaLanguage.EXCEL;
            default:
                throw new IllegalArgumentException("Unknown " + number);
        }

    }
}
