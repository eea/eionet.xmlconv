<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>

      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<spring:message code="label.config.system"/>'><spring:message
              code="label.qascript.tab.title"/></span></li>
      <li>
        <%--paramId="script_id" paramName="QAScriptForm" paramProperty="scriptId"  titleKey="label.qascript.history" --%>
        <a href="/qaScripts/${QAScriptForm.scriptId}/history" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.history"/>
        </a>
      </li>
    </ul>
  </div>
  <ed:breadcrumbs-push label="Edit QA script" level="3"/>

  <h1><spring:message code="label.qascript.edit"/></h1>




  <form:form action="/editQAScript" method="post" enctype="multipart/form-data" modelAttribute="${QAScriptForm}">
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
          ${QAScriptForm.schema}
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="txtShortName">
            <spring:message code="label.qascript.shortname"/>
          </label>
        </td>
        <td>
          <form:input name="QAScriptForm" path="shortName" id="txtShortName" size="64"/>
        </td>
      </tr>
      <tr class="zebraeven">
        <td>
          <label class="question" for="txtDescription">
            <spring:message code="label.qascript.description"/>
          </label>
        </td>
        <td>
          <form:textarea path="description" rows="2" cols="30" style="width:400px" styleId="txtDescription"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="selContentType">
            <spring:message code="label.qascript.resulttype"/>
          </label>
        </td>
        <td>
          <form:select name="QAScriptForm" path="resultType" styleId="selContentType">
            <form:options collection="qascript.resulttypes" property="convType"/>
          </form:select>
        </td>
      </tr>
      <tr class="zebraeven">
        <td>
          <label class="question" for="selScriptType">
            <spring:message code="label.qascript.scripttype"/>
          </label>
        </td>
        <td>
          <form:select name="QAScriptForm" path="scriptType" styleId="selScriptType" disabled="false">
            <form:options items="${qascript.scriptlangs}" property="convType"/>
          </form:select>
          <form:hidden path="QAScriptForm" property="scriptType"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="txtUpperLimit">
            <spring:message code="label.qascript.upperlimit"/>
          </label>
        </td>
        <td>
          <form:input id="txtUpperLimit" size="3" path="upperLimit"/>
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
          <c:if test="${QAScriptForm.scriptType == 'fme'}">
            <a href="${webRoot}/${QAScriptForm.filePath}" title="${QAScriptForm.filePath}">
                ${QAScriptForm.fileName}
            </a>
            &#160;&#160;&#160;&#160;&#160;&#160;(<spring:message code="label.lastmodified"/>:
            <c:choose>
              <c:when test="${QAScriptForm.modified}">
                ${QAScriptForm.modified}
              </c:when>
              <c:otherwise>
                <span style="color:red"><spring:message code="label.fileNotFound"/></span>
              </c:otherwise>
            </c:choose>
            )
          </c:if>
            <%--  If scriptType is 'FME' don't show the link to the local script file --%>
          <c:if test="${QAScriptForm.scriptType == 'fme'}">
            ${QAScriptForm.fileName}
          </c:if>
        </td>
      </tr>

        <%--  If scriptType is 'FME' don't show the FileUpload --%>
      <c:if test="${QAScriptForm.scriptType != 'fme'}">
        <tr class="zebraeven">
          <td>&#160;</td>
          <td>
            <button type="submit" class="button" name="action" value="upload">
              <spring:message code="label.qascript.upload"/>
            </button>
            <input type="file" name="scriptFile" style="width:400px" size="64"/>
          </td>
        </tr>
      </c:if>

      <tr>
        <td>
          <label class="question" for="txtUrl">
            <spring:message code="label.qascript.url"/>
          </label>
        </td>
        <td>
          <form:input id="txtUrl" path="url" size="107"/>
        </td>
      </tr>
      <tr class="zebraeven">
        <td>
          <label class="question" for="isActive">
            <spring:message code="label.qascript.isActive"/>
          </label>
        </td>
        <td>
          <form:checkbox path="active" id="isActive"/>
          <form:hidden path="active" value="false"/>
        </td>
      </tr>
        <%--  If scriptType is 'FME' don't show the 'Check for updates' --%>
      <c:if test="${QAScriptForm.scriptType == 'fme'}">
        <tr>
          <td></td>
          <td>
            <c:if test="${!empty QAScriptForm.fileName}">
              <button type="submit" clas="button" name="action" value="diff">
                <spring:message code="label.qascript.checkupdates"/>"
              </button>
            </c:if>
          </td>
        </tr>
      </c:if>
      <c:if test="${!empty QAScriptForm.fileName}">
        <c:if test="${QAScriptForm.scriptType != 'fme'}">
          <tr>
            <td colspan="2">
              <label class="question" for="txtUrl">
                <spring:message code="label.qascript.source"/>
              </label>
              <form:textarea path="scriptContent" style="width: 98%;" rows="20" cols="55" id="txtFile"/>
            </td>
          </tr>
        </c:if>
        <tr>
          <td>&#160;</td>
          <td>
            <button type="submit" class="button" name="action" value="save">
              <spring:message code="label.qascript.save"/>
            </button>
            <form:hidden path="fileName"/>
            <form:hidden path="checksum" name="QAScriptForm"/>
            <form:hidden path="scriptId" name="QAScriptForm"/>
            <form:hidden path="schemaId" name="QAScriptForm"/>
            <form:hidden path="active" name="QAScriptForm"/>
          </td>
        </tr>
        <tr>
          <td colspan="2">&#160;</td>
        </tr>
        <!-- tr>
        <td>&#160;</td>
        <td>
        <input type="file" name="scriptFile" style="width:400px" size="64"/>
        </td>
        </tr-->
      </c:if>
    </table>
  </form:form>
</div>

