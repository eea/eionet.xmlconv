package eionet.gdem.services.fme.request;

public abstract class FMERequest {
    public static final String NAME_KEY="name";
    public static final String VALUE_KEY="value";
    public static final String FOLDER_VALUE="folder";
    public static final String ENVELOPE_VALUE_PARAM="envelopepath";
    public static final String ENCODING_ENTITY_TYPE="UTF-8";

    public abstract String buildBody();

}
