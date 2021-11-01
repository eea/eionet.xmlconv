/*
 * To change this license header   choose License Headers in Project Properties.
 * To change this template file  choose Tools | Templates
 * and open the template in the editor.
 */
package eionet.gdem.qa;

import java.io.Serializable;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public  final  class QaScriptView implements Serializable {

    public static final  String QUERY_ID = "id";
    public static final  String SHORT_NAME = "name";
    public static final  String QUERY = "query";
    public static final  String DESCRIPTION = "description";
    public static final  String SCHEMA_ID = "schemaId";
    public static final  String XML_SCHEMA = "schemaUrl";
    public static final  String CONTENT_TYPE_ID = "outputType";
    public static final  String CONTENT_TYPE = "contentType";
    public static final  String CONTENT_TYPE_OUT = "contentTypeOut";
    public static final  String QUERY_AS_URL="url";
    public static final  String SCRIPT_TYPE = "scriptType";
    public static final  String UPPER_LIMIT = "runOnDemandMaxFileSizeMB";
    public static final  String IS_ACTIVE = "isActive";
    public static final  String TYPE = "type";
    public static final String RESULT_TYPE="resultType";
    public static final String META_TYPE="metaType";
    public static final String URL = "url";
    public static final String ASYNCHRONOUS_EXECUTION = "asynchronousExecution";
    public static final String MARKED_HEAVY = "markedHeavy";
    public static final String MARKED_HEAVY_REASON = "markedHeavyReason";
    public static final String MARKED_HEAVY_REASON_OTHER = "markedHeavyReasonOther";

    private QaScriptView() {
    }

}
