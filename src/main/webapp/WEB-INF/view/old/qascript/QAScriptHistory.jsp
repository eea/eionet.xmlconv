<%@ page contentType="text/html; charset=UTF-8"  import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>

<%@page import="eionet.gdem.Constants"%>

<html:xhtml/>

<div style="width:100%;">
    <div id="tabbedmenu">
        <ul>
            <li>
                <html:link page="/do/viewQAScriptForm" paramId="scriptId" paramName="script_id" titleKey="label.qascript.tab.title" onclick="return submitTab(this);"     style="color: black; text-decoration: none;">
                    <bean:message key="label.qascript.tab.title" />
                </html:link>
            </li>
            <li id="currenttab"><span style="color: black; text-decoration: none;" title='<bean:message key="label.qascript.history"/>'><bean:message key="label.qascript.history" /></span></li>
        </ul>
    </div>

<ed:breadcrumbs-push label="QA Script History" level="3" />


    <h1 class="documentFirstHeading">
        <bean:message key="label.qascriptHistory.title"/>
    </h1>

    <%-- include Error display --%>
    <tiles:insertDefinition name="Error" />

    <div class="visualClear">&nbsp;</div>

    <logic:present name="qascript.history">
        <div style="width: 97%">
            <table class="datatable" width="100%">
                <col style="width:10%"/>
                <col style="width:52%"/>
                <col style="width:38%"/>
                <thead>
                    <tr>
                        <th scope="col" class="scope-col"><bean:message key="label.table.backup.filename"/></th>
                        <th scope="col" class="scope-col"><bean:message key="label.table.backup.timestamp"/></th>
                        <th scope="col" class="scope-col"><bean:message key="label.table.backup.user"/></th>
                    </tr>
                </thead>
                <tbody>
                    <logic:iterate indexId="index" id="backup" name="qascript.history" type="BackupDto">
                    <tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
                        <td align="center">
                            <a  href="<bean:write name="webRoot"/>/<%=Constants.QUERY_FOLDER%>/<%=Constants.BACKUP_FOLDER_NAME%>/<bean:write name="backup" property="fileName" />" title="<bean:write name="backup" property="fileName" />">
                                <bean:write name="backup" property="fileName" />
                            </a>
                        </td>
                        <td>
                            <bean:write name="backup" property="timestamp" />
                        </td>
                        <td>
                            <bean:write name="backup" property="user" />
                        </td>
                    </tr>
                    </logic:iterate>
                    <tr>
                        <td valign="top" colspan="3">
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="visualClear">&nbsp;</div>
    </logic:present>



</div>
