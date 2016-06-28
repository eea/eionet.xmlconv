<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

    <div style="width:100%;">
    <div id="tabbedmenu">
        <ul>

            <li id="currenttab"><span style="color: black; text-decoration: none;" title='<bean:message key="label.config.system"/>'><bean:message key="label.qascript.tab.title" /></span></li>
            <li>
                <html:link page="/do/qaScriptHistory"  paramId="script_id" paramName="QAScriptForm" paramProperty="scriptId"  titleKey="label.qascript.history"    style="color: black; text-decoration: none;">
                    <bean:message key="label.qascript.history" />
                </html:link>
            </li>
        </ul>
    </div>
        <ed:breadcrumbs-push label="View QA script" level="3" />

        <div id="operations">
              <ul>
                   <li>
                    <logic:equal name="qsuPrm" value="true"  name="qascript.permissions" property="qsuPrm" >
                        <%--  If scriptType is NOT 'FME' --%>
                        <logic:notEqual name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                            <html:link page="/do/editQAScriptInSandbox?reset=true" paramId="scriptId" paramName="QAScriptForm" paramProperty="scriptId" titleKey="label.qasandbox.label.qasandbox.editScript">
                                <bean:message key="label.qascript.run" />
                            </html:link>
                        </logic:notEqual>
                        <%--  If scriptType is 'FME' --%>
                        <logic:equal name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                            <a href="openQAServiceInSandbox?scriptId=<bean:write name="QAScriptForm" property="scriptId" />&amp;schemaId=<bean:write name="QAScriptForm" property="schemaId" />" title="<bean:message key="label.qascript.runservice.title" />">
                                <bean:message key="label.qascript.run" />
                            </a>
                        </logic:equal>
                    </logic:equal>
                    <logic:notEqual name="qsuPrm" value="true"  name="qascript.permissions" property="qsuPrm" >
                        <a href="openQAServiceInSandbox?scriptId=<bean:write name="QAScriptForm" property="scriptId" />&amp;schemaId=<bean:write name="QAScriptForm" property="schemaId" />" title="<bean:message key="label.qascript.runservice.title" />">
                            <bean:message key="label.qascript.run" />
                        </a>
                    </logic:notEqual>

                </li>
                <logic:equal name="ssdPrm" value="true"  name="qascript.permissions" property="ssdPrm" >
                    <li>
                        <html:link page="/do/editQAScriptForm" paramId="scriptId" paramName="QAScriptForm" paramProperty="scriptId" title="edit QA Script" >
                            <bean:message key="label.qascript.edit" />
                        </html:link>
                    </li>
                       <li>
                        <a href="deleteQAScript?scriptId=<bean:write name="QAScriptForm" property="scriptId" />&amp;schemaId=<bean:write name="QAScriptForm" property="schemaId" />" title="delete QA script" onclick='return qaScriptDelete("<bean:write name="QAScriptForm" property="fileName" />");'>
                            <bean:message key="label.qascript.delete" />
                        </a>
                    </li>
                </logic:equal>
            </ul>
        </div>


        <h1><bean:message key="label.qascript.view"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />

          <table class="datatable">
            <col class="labelcol"/>
            <col class="entrycol"/>
            <tr>
                <th scope="row" class="scope-row">
                    <bean:message key="label.qascript.schema"/>
                </th>
                  <td>
                    <a  href="<bean:write name="QAScriptForm" property="schema" />" title="<bean:write name="QAScriptForm" property="schema" />">
                        <bean:write name="QAScriptForm" property="schema" />
                    </a>&#160;
                </td>
            </tr>
            <tr>
                <th scope="row" class="scope-row">
                    <bean:message key="label.qascript.shortname"/>
                </th>
              <td>
                <bean:write name="QAScriptForm" property="shortName" />
              </td>
            </tr>
            <tr>
                <th scope="row" class="scope-row">
                      <bean:message key="label.qascript.description"/>
                </th>
              <td>
                  <bean:write name="QAScriptForm" property="description"/>
              </td>
            </tr>
            <tr>
                <th scope="row" class="scope-row">
                    <bean:message key="label.qascript.resulttype"/>
                </th>
              <td>
                <bean:write name="QAScriptForm" property="resultType" />
              </td>
            </tr>
            <tr>
                <th scope="row" class="scope-row">
                    <bean:message key="label.qascript.scripttype"/>
                </th>
              <td>
                <bean:write name="QAScriptForm" property="scriptType" />
              </td>
            </tr>
            <tr>
            <th scope="row" class="scope-row">
                <bean:message key="label.qascript.upperlimit"/>
            </th>
          <td>
            <bean:write name="QAScriptForm" property="upperLimit" />
          </td>
        </tr>
        <tr>
            <th scope="row" class="scope-row">
                <bean:message key="label.qascript.isActive"/>
            </th>
            <td>
                <logic:equal name="QAScriptForm" property="active" value="true">
                    <input type="checkbox" checked="checked" disabled/>
                </logic:equal>
                <logic:notEqual name="QAScriptForm" property="active" value="true">
                    <input type="checkbox" disabled/>
                </logic:notEqual>
            </td>
        </tr>

            <tr>
                <th scope="row" class="scope-row">
                    <bean:message key="label.qascript.fileName"/>
                </th>
              <td>
                <%--  If scriptType is 'FME' don't show the link to the local script file --%>
                <logic:notEqual name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                    <a  href="<bean:write name="webRoot"/>/<bean:write property="filePath" name="QAScriptForm"/>" title="<bean:write property="filePath" name="QAScriptForm"/>">
                                <bean:write property="fileName" name="QAScriptForm"/>
                    </a>
                    &#160;&#160;&#160;&#160;&#160;&#160;(<bean:message key="label.lastmodified"/>:
                    <logic:present name="QAScriptForm" property="modified">
                        <bean:write property="modified" name="QAScriptForm"/>
                    </logic:present>
                    <logic:notPresent name="QAScriptForm" property="modified">
                        <span style="color:red"><bean:message key="label.fileNotFound"/></span>
                    </logic:notPresent>
                    )
                </logic:notEqual>
                <logic:equal name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                    <bean:write property="fileName" name="QAScriptForm"/>
                </logic:equal>
              </td>
            </tr>
            <%--  If scriptType is 'FME' don't show the link to the remote script file --%>
            <logic:notEqual name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
            <tr>
              <th scope="row" class="scope-row"><bean:message
                  key="label.qascript.url" /></th>
              <td>
              <logic:notEmpty name="QAScriptForm" property="url">
                  <a href="<bean:write property="url" name="QAScriptForm"/>"
                      title="<bean:write property="url" name="QAScriptForm"/>">View</a>
              </logic:notEmpty>
              </td>
            </tr>
            </logic:notEqual>

  </table>
        <%--  If scriptType is 'FME' don't show the script content --%>
        <logic:notEqual name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
            <logic:present name="QAScriptForm" property="fileName">
                  <pre><bean:write name="QAScriptForm" property="scriptContent"/></pre>
            </logic:present>
        </logic:notEqual>


</div>
