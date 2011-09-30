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
                    <logic:equal name="qsuPrm" value="true"  name="qascript.permissions" scope="session" property="qsuPrm" >
                        <html:link page="/do/editQAScriptInSandbox?reset=true" paramId="scriptId" paramName="QAScriptForm" paramProperty="scriptId" titleKey="label.qasandbox.label.qasandbox.editScript">
                            <bean:message key="label.qascript.run" />
                        </html:link>
                    </logic:equal>
                    <logic:notEqual name="qsuPrm" value="true"  name="qascript.permissions" scope="session" property="qsuPrm" >
                        <a href="openQAServiceInSandbox?scriptId=<bean:write name="QAScriptForm" property="scriptId" />&amp;schemaId=<bean:write name="QAScriptForm" property="schemaId" />" title="<bean:message key="label.qascript.runservice.title" />">
                            <bean:message key="label.qascript.run" />
                        </a>
                    </logic:notEqual>

                </li>
                <logic:equal name="ssdPrm" value="true"  name="qascript.permissions" scope="session" property="ssdPrm" >
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
                    <bean:message key="label.qascript.fileName"/>
                </th>
              <td>
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
              </td>
            </tr>
          </table>
          <logic:present name="QAScriptForm" property="fileName">
              <pre><bean:write name="QAScriptForm" property="scriptContent"/></pre>
        </logic:present>


</div>
