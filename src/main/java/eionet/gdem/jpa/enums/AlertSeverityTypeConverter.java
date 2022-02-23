package eionet.gdem.jpa.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class AlertSeverityTypeConverter implements AttributeConverter<AlertSeverity, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AlertSeverity jobExecutorType) {
        if (jobExecutorType == null) {
            return null;
        }
        return jobExecutorType.getId();
    }

    @Override
    public AlertSeverity convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(AlertSeverity.values())
                .filter(alertSeverity -> alertSeverity.getId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
