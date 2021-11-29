package eionet.gdem.jpa.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class JobExecutorTypeConverter implements AttributeConverter<JobExecutorType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(JobExecutorType jobExecutorType) {
        if (jobExecutorType == null) {
            return null;
        }
        return jobExecutorType.getId();
    }

    @Override
    public JobExecutorType convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(JobExecutorType.values())
                .filter(jobExecutorType -> jobExecutorType.getId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
