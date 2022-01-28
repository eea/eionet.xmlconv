package eionet.gdem.qa.utils;

import eionet.gdem.data.scripts.HeavyScriptReasonEnum;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.qa.XQScript;

import java.util.Date;

/**
 * QA script utility class.
 * @author George Sofianos
 */

public final class ScriptUtils {

    /**
     * Default constructor
     */
    private ScriptUtils() {
        throw new AssertionError();
    }

    /**
     * Returns file extension from script type.
     * @param scriptType Script type
     * @return File extension
     */
    public static String getExtensionFromScriptType(String scriptType) {
        String extension;
        if (scriptType.equalsIgnoreCase(XQScript.SCRIPT_LANG_XQUERY1) ||  scriptType.equalsIgnoreCase(XQScript.SCRIPT_LANG_XQUERY3)) {
            extension = "xquery";
        } else {
            extension = scriptType;
        }
        return extension;
    }

    public static XQScript createScriptFromJobEntry(JobEntry jobEntry) {
        XQScript script = new XQScript();
        script.setJobId(jobEntry.getId().toString());
        script.setSrcFileUrl(jobEntry.getUrl());
        script.setScriptFileName(jobEntry.getFile());
        script.setStrResultFile(jobEntry.getResultFile());
        script.setScriptType(jobEntry.getScriptType());
        return script;
    }

    public static QueryHistoryEntry createQueryHistoryEntry(String user, String shortName, String schemaId, String resultType, String description, String scriptType,
                                                            String upperLimit, String url, Boolean asynchronousExecution, boolean active, String fileName, Integer version,
                                                                         Boolean markedHeavy, Integer markedHeavyReason, String markedHeavyReasonOther, String ruleMatch) {
        QueryHistoryEntry queryHistoryEntry = new QueryHistoryEntry().setDescription(description).setShortName(shortName).setQueryFileName(fileName)
                .setSchemaId(Integer.parseInt(schemaId)).setResultType(resultType).setScriptType(scriptType).setUpperLimit(Integer.parseInt(upperLimit))
                .setUrl(url).setActive(active).setAsynchronousExecution(asynchronousExecution).setVersion(version).setUser(user).setDateModified(new Date())
                .setMarkedHeavy(markedHeavy).setMarkedHeavyReason(markedHeavyReason).setMarkedHeavyReasonOther(markedHeavyReasonOther).setRuleMatch(ruleMatch);
        return queryHistoryEntry;
    }

    public static String getHeavyScriptReasonTextByCode(Integer code){
        for(HeavyScriptReasonEnum heavyReason: HeavyScriptReasonEnum.values()){
            if(heavyReason.getCode() == code){
                return heavyReason.getText();
            }
        }
        return null;
    }

    public static Integer getHeavyScriptReasonCodeByText(String text){
        for(HeavyScriptReasonEnum heavyReason: HeavyScriptReasonEnum.values()){
            if(heavyReason.getText().equals(text)){
                return heavyReason.getCode();
            }
        }
        return null;
    }
}
