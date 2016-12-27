package eionet.gdem.data.transformations;

import javax.persistence.AttributeConverter;

/**
 *
 */
public class TransformationTypeConverter implements AttributeConverter<TransformationType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TransformationType transformationType) {
        switch (transformationType) {
            case EMPTY:
                return 1;
            case HTML:
                return 2;
            case XML:
                return 3;
            case EXCEL:
                return 4;
            case RDF:
                return 5;
            case KML:
                return 6;
            default:
                throw new IllegalArgumentException("Unknown" + transformationType);
        }
    }

    @Override
    public TransformationType convertToEntityAttribute(Integer integer) {
        switch (integer) {
            case 1:
                return TransformationType.EMPTY;
            case 2:
                return TransformationType.HTML;
            case 3:
                return TransformationType.XML;
            case 4:
                return TransformationType.EXCEL;
            case 5:
                return TransformationType.RDF;
            case 6:
                return TransformationType.KML;
            default:
                throw new IllegalArgumentException("Unknown" + integer);
        }
    }
}
