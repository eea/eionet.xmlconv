package eionet.gdem.data.scripts;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 */
@Converter(autoApply = true)
public class ScriptTypeConverter implements AttributeConverter<ScriptType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ScriptType scriptType) {
        switch (scriptType) {
            case EMPTY:
                return 1;
            case XQUERY:
                return 2;
            case FME:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown" + scriptType);
        }
    }

    @Override
    public ScriptType convertToEntityAttribute(Integer i) {
        switch (i) {
            case 1:
                return ScriptType.EMPTY;
            case 2:
                return ScriptType.XQUERY;
            case 3:
                return ScriptType.FME;
            default:
                throw new IllegalArgumentException("Unknown" + i);
        }
    }
}
