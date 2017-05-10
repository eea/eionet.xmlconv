<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties" %>--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>

<%--<html:xhtml/>--%>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>

      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<spring:message code="label.config.system"/>'><spring:message
              code="label.qascript.tab.title"/></span></li>
      <li>
        <%--paramId="script_id" paramName="QAScriptForm" paramProperty="scriptId"  titleKey="label.qascript.history" --%>
        <a href="/old/qaScripts/${QAScriptForm.scriptId}/history" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.history"/>
        </a>
      </li>
    </ul>
  </div>
  <ed:breadcrumbs-push label="Edit QA script" level="3"/>

  <h1><spring:message code="label.qascript.edit"/></h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <form:form action="/editQAScript" method="post" enctype="multipart/form-data">
    <table class="formtable">
      <col class="labelcol"/>
      <col class="entrycol"/>
      <tr class="zebraeven">
        <td>
          <label class="question">
            <spring:message code="label.qascript.schema"/>
          </label>
        </td>
        <td>
          <bean:write name="QAScriptForm" property="schema"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="txtShortName">
            <spring:message code="label.qascript.shortname"/>
          </label>
        </td>
        <td>
          <html:text name="QAScriptForm" property="shortName" styleId="txtShortName" size="64"/>
        </td>
      </tr>
      <tr class="zebraeven">
        <td>
          <label class="question" for="txtDescription">
            <spring:message code="label.qascript.description"/>
          </label>
        </td>
        <td>
          <html:textarea property="description" rows="2" cols="30" style="width:400px" styleId="txtDescription"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="selContentType">
            <spring:message code="label.qascript.resulttype"/>
          </label>
        </td>
        <td>
          <html:select name="QAScriptForm" property="resultType" styleId="selContentType">
            <html:options collection="qascript.resulttypes" property="convType"/>
          </html:select>
        </td>
      </tr>
      <tr class="zebraeven">
        <td>
          <label class="question" for="selScriptType">
            <spring:message code="label.qascript.scripttype"/>
          </label>
        </td>
        <td>
          <html:select name="QAScriptForm" property="scriptType" styleId="selScriptType" disabled="false">
            <html:options collection="qascript.scriptlangs" property="convType"/>
          </html:select>
          <html:hidden name="QAScriptForm" property="scriptType"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="txtUpperLimit">
            <spring:message code="label.qascript.upperlimit"/>
          </label>
        </td>
        <td>
          <html:text styleId="txtUpperLimit" size="3" property="upperLimit"/>
        </td>
      </tr>

      <tr class="zebraeven">
        <td>
          <label class="question" for="txtFile">
            <spring:message code="label.qascript.fileName"/>
          </label>
        </td>
        <td>
            <%--  If scriptType is 'FME' don't show the link to the local script file --%>
          <c:if notEqual name="QAScriptForm" property="scriptType"
                          value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
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
            <%--  If scriptType is 'FME' don't show the link to the local script file --%>
          <c:if equal name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
            <bean:write property="fileName" name="QAScriptForm"/>
          </c:if equal>
        </td>
      </tr>

        <%--  If scriptType is 'FME' don't show the FileUpload --%>
      <c:if notEqual name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
        <tr class="zebraeven">
          <td>&#160;</td>
          <td>
            <html:submit styleClass="button" property="action">
              <spring:message code="label.qascript.upload"/>
            </html:submit>
            <html:file property="scriptFile" style="width:400px" size="64"/>
          </td>
        </tr>
      </c:if notEqual>

      <tr>
        <td>
          <label class="question" for="txtUrl">
            <spring:message code="label.qascript.url"/>
          </label>
        </td>
        <td>
          <html:text styleId="txtUrl" property="url" size="107"/>

        </td>
      </tr>
      <tr class="zebraeven">
        <td>
          <label class="question" for="isActive">
            <spring:message code="label.qascript.isActive"/>
          </label>
        </td>
        <td>
          <html:checkbox name="QAScriptForm" property="active" styleId="isActive"/>
          <html:hidden property="active" name="QAScriptForm" value="false"/>
        </td>
      </tr>
        <%--  If scriptType is 'FME' don't show the 'Check for updates' --%>
      <c:if notEqual name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
        <tr>
          <td></td>
          <td>
            <c:if notEmpty name="QAScriptForm" property="fileName">
              <input type="button" class="button" value="<spring:message code="label.qascript.checkupdates"/>"
                     onclick="return submitAction(1,'diffUplScripts');"/>
            </c:if notEmpty>
          </td>
        </tr>
      </c:if notEqual>
      <c:if present name="QAScriptForm" property="fileName">
        <%--  If scriptType is 'FME' don't show the script content --%>
        <c:if notEqual name="QAScriptForm" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
          <tr>
            <td colspan="2">
              <label class="question" for="txtUrl">
                <spring:message code="label.qascript.source"/>
              </label>
              <html:textarea property="scriptContent" style="width: 98%;" rows="20" cols="55" styleId="txtFile"/>
            </td>
          </tr>
        </c:if notEqual>
        <tr>
          <td>&#160;</td>
          <td>
            <html:submit styleClass="button" property="action">
              <spring:message code="label.qascript.save"/>
            </html:submit>
            <html:hidden property="fileName"/>
            <html:hidden property="checksum" name="QAScriptForm"/>
            <html:hidden property="scriptId" name="QAScriptForm"/>
            <html:hidden property="schemaId" name="QAScriptForm"/>
            <html:hidden property="active" name="QAScriptForm"/>
          </td>
        </tr>
        <tr>
          <td colspan="2">&#160;</td>
        </tr>
        <!-- tr>
        <td>&#160;</td>
        <td>
        <html:file property="scriptFile" style="width:400px" size="64"/>
        </td>
        </tr-->
      </c:if present>
    </table>
  </form:form>
</div>

