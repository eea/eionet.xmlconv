<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties" %>--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>

<%--<html:xhtml/>--%>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<spring:message code="label.config.system"/>'><bean:message
              key="label.qascript.tab.title"/></span></li>
      <li>
        <%--paramId="script_id" paramName="QAScriptForm" paramProperty="scriptId"--%>
        <html:link page="/old/qaScripts/${QAScriptForm.scriptId}/history"
                   titleKey="label.qascript.history" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.history"/>
        </html:link>
      </li>
    </ul>
  </div>
  <ed:breadcrumbs-push label="View QA script" level="3"/>

  <div id="operations">
    <ul>
      <li>
        <c:if equal value="true" name="qascript.permissions" property="qsuPrm">
          <%--  If scriptType is NOT 'FME' --%>
          <c:if notEqual name="QAScriptForm" property="scriptType"
                          value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
            <%--do/editQAScriptInSandbox?reset=true" paramId="scriptId" paramName="QAScriptForm"
            paramProperty="scriptId"--%>
            <html:link page="/old/qaSandbox/${QAScriptForm.scriptId}"
                       titleKey="label.qasandbox.label.qasandbox.editScript">
              <spring:message code="label.qascript.run"/>
            </html:link>
          </c:if notEqual>
          <%--  If scriptType is 'FME' --%>
          <c:if equal name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
            <a href="openQAServiceInSandbox?scriptId=<bean:write name="QAScriptForm" property="scriptId" />&amp;schemaId=<bean:write name="QAScriptForm" property="schemaId" />"
               title="<spring:message code="label.qascript.runservice.title" />">
              <spring:message code="label.qascript.run"/>
            </a>
          </c:if equal>
        </c:if equal>
        <c:if notEqual value="true" name="qascript.permissions" property="qsuPrm">
          <a href="openQAServiceInSandbox?scriptId=<bean:write name="QAScriptForm" property="scriptId" />&amp;schemaId=<bean:write name="QAScriptForm" property="schemaId" />"
             title="<spring:message code="label.qascript.runservice.title" />">
            <spring:message code="label.qascript.run"/>
          </a>
        </c:if notEqual>

      </li>
      <c:if equal value="true" name="qascript.permissions" property="ssdPrm">
        <li>
            <%--paramId="scriptId" paramName="QAScriptForm" paramProperty="scriptId"--%>
          <html:link page="/old/${QAScriptForm.scriptId}/edit" title="edit QA Script">
            <spring:message code="label.qascript.edit"/>
          </html:link>
        </li>
        <li>
          <a href="deleteQAScript?scriptId=<bean:write name="QAScriptForm" property="scriptId" />&amp;schemaId=<bean:write name="QAScriptForm" property="schemaId" />"
             title="delete QA script"
             onclick='return qaScriptDelete("<bean:write name="QAScriptForm" property="fileName"/>");'>
            <spring:message code="label.qascript.delete"/>
          </a>
        </li>
      </c:if equal>
    </ul>
  </div>


  <h1><spring:message code="label.qascript.view"/></h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <table class="datatable">
    <col class="labelcol"/>
    <col class="entrycol"/>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.schema"/>
      </th>
      <td>
        <a href="<bean:write name="QAScriptForm" property="schema" />"
           title="<bean:write name="QAScriptForm" property="schema" />">
          <bean:write name="QAScriptForm" property="schema"/>
        </a>&#160;
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.shortname"/>
      </th>
      <td>
        <bean:write name="QAScriptForm" property="shortName"/>
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.description"/>
      </th>
      <td>
        <bean:write name="QAScriptForm" property="description"/>
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.resulttype"/>
      </th>
      <td>
        <bean:write name="QAScriptForm" property="resultType"/>
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.scripttype"/>
      </th>
      <td>
        <bean:write name="QAScriptForm" property="scriptType"/>
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.upperlimit"/>
      </th>
      <td>
        <bean:write name="QAScriptForm" property="upperLimit"/>
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.isActive"/>
      </th>
      <td>
        <c:if equal name="QAScriptForm" property="active" value="true">
          <input type="checkbox" checked="checked" disabled/>
        </c:if equal>
        <c:if notEqual name="QAScriptForm" property="active" value="true">
          <input type="checkbox" disabled/>
        </c:if notEqual>
      </td>
    </tr>

    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.fileName"/>
      </th>
      <td>
        <%--  If scriptType is 'FME' don't show the link to the local script file --%>
        <c:if notEqual name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
          <a href="<bean:write name="webRoot"/>/<bean:write property="filePath" name="QAScriptForm"/>"
             title="<bean:write property="filePath" name="QAScriptForm"/>">
            <bean:write property="fileName" name="QAScriptForm"/>
          </a>
          &#160;&#160;&#160;&#160;&#160;&#160;(<spring:message code="label.lastmodified"/>:
          <c:if present name="QAScriptForm" property="modified">
            <bean:write property="modified" name="QAScriptForm"/>
          </c:if present>
          <c:if notPresent name="QAScriptForm" property="modified">
            <span style="color:red"><spring:message code="label.fileNotFound"/></span>
          </c:if notPresent>
          )
        </c:if notEqual>
        <c:if equal name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
          <bean:write property="fileName" name="QAScriptForm"/>
        </c:if equal>
      </td>
    </tr>
    <%--  If scriptType is 'FME' don't show the link to the remote script file --%>
    <c:if notEqual name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
      <tr>
        <th scope="row" class="scope-row"><bean:message
                key="label.qascript.url"/></th>
        <td>
          <c:if notEmpty name="QAScriptForm" property="url">
            <a href="<bean:write property="url" name="QAScriptForm"/>"
               title="<bean:write property="url" name="QAScriptForm"/>">View</a>
          </c:if notEmpty>
        </td>
      </tr>
    </c:if notEqual>

  </table>
  <%--  If scriptType is 'FME' don't show the script content --%>
  <c:if notEqual name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
    <c:if present name="QAScriptForm" property="fileName">
      <pre><bean:write name="QAScriptForm" property="scriptContent"/></pre>
    </c:if present>
  </c:if notEqual>

</div>
