package eionet.gdem.jpa.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class PropertiesEntryTypeConverter implements AttributeConverter<PropertiesEntryType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PropertiesEntryType propertiesEntryType) {
        if (propertiesEntryType == null) {
            return null;
        }
        return propertiesEntryType.getId();
    }

    @Override
    public PropertiesEntryType convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(PropertiesEntryType.values())
                .filter(jobExecutorType -> jobExecutorType.getId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
