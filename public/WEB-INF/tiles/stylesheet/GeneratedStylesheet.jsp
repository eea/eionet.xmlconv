<%@ page contentType="text/html; charset=UTF-8"  import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>
<html:xhtml/>

<ed:breadcrumbs-push label="Stylesheets" level="1" />

<logic:present name="stylesheet.generatedList">
    <h1 class="documentFirstHeading">
        <bean:message key="label.stylesheet.generated"/>
    </h1>

    <div class="visualClear">&nbsp;</div>

    <div style="width: 97%">
        <table class="datatable" width="100%">
            <col style="width:7%"/>
            <col style="width:10%"/>
            <col style="width:20%"/>
            <col style="width:10%"/>
            <col style="width:10%"/>
            <col style="width:43%"/>
            <thead>
                <tr>
                    <th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.action"/></th>
                    <th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.table"/></th>
                    <th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.dataset"/></th>
                    <th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.datasetReleased"/></th>
                    <th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.title"/></th>
                    <th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.stylesheets"/></th>
                </tr>
            </thead>
            <tbody>
                <logic:iterate indexId="index" id="schema" name="stylesheet.generatedList" scope="session" property="ddStylesheets" type="Schema">
                <tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
                    <td align="center">
                        <html:link action="/schemaStylesheets" paramId="schema" paramName="schema" paramProperty="schema">
                            <html:img page="/images/properties.gif" altKey="label.table.stylesheet" title="view stylesheets" />
                        </html:link>
                    </td>
                    <td title="<bean:write name="schema" property="table"/>">
                        <bean:write name="schema" property="table" />
                    </td>
                    <td title="<bean:write name="schema" property="dataset"/>">
                        <bean:write name="schema" property="dataset" />
                    </td>
                    <td title="<bean:write name="schema" property="datasetReleased"/>">
                        <bean:write name="schema" property="datasetReleased" format="<%= Properties.dateFormatPattern%>" />
                    </td>
                    <td>
                        <a  href="<bean:write name="schema" property="schema" />" title="<bean:write name="schema" property="schema" />">
                            <bean:write name="schema" property="id" />
                        </a>
                    </td>
                    <td>
                        <logic:iterate id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">
                        <a  href="<bean:write name="stylesheet" property="xsl" />" title="<bean:write name="stylesheet" property="xsl_descr" />">
                            <bean:write name="stylesheet" property="xsl_descr" />
                        </a>&#160;
                        </logic:iterate>
                    </td>
                </tr>
                </logic:iterate>
                <tr>
                    <td valign="top" colspan="5">
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

</logic:present>



