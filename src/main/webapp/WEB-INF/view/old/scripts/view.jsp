<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li id="currenttab">
        <span style="color: black; text-decoration: none;" title='<spring:message code="label.config.system"/>'>
          <spring:message code="label.qascript.tab.title"/>
        </span>
      </li>
      <li>
        <%--paramId="script_id" paramName="QAScriptForm" paramProperty="scriptId"--%>
        <a href="/scripts/${QAScriptForm.scriptId}/history" titleKey="label.qascript.history" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.history"/>
        </a>
      </li>
    </ul>
  </div>
  <ed:breadcrumbs-push label="View QA script" level="3"/>

  <div id="operations">
    <ul>
      <li>
        <c:if test="${qascript.qsuPrm}">
          <%--  If scriptType is NOT 'FME' --%>
          <c:if test="${QAScriptForm.scriptType != 'fme'}">
            <%--do/editQAScriptInSandbox?reset=true" paramId="scriptId" paramName="QAScriptForm"
            paramProperty="scriptId"--%>
            <a href="/qaSandbox/${QAScriptForm.scriptId}" titleKey="label.qasandbox.label.qasandbox.editScript">
              <spring:message code="label.qascript.run"/>
            </a>
          </c:if>
          <%--  If scriptType is 'FME' --%>
          <c:if test="${QAScriptForm.scriptType == 'fme'}">
            <spring:message code="label.qascript.runservice.title" var="title"/>
            <a href="openQAServiceInSandbox?scriptId=${QAScriptForm.scriptId}&amp;schemaId=${QAScriptForm.schemaId}" title="${title}">
              <spring:message code="label.qascript.run"/>
            </a>
          </c:if>
        </c:if>
        <c:if test="${!qascript.qsuPrm}">
          <a href="openQAServiceInSandbox?scriptId=${QAScriptForm.scriptId}&amp;schemaId=${QAScriptForm.schemaId}"
             title="${title}">
            <spring:message code="label.qascript.run"/>
          </a>
        </c:if>

      </li>
      <c:if test="${qascript.ssdPrm}">
        <li>
            <%--paramId="scriptId" paramName="QAScriptForm" paramProperty="scriptId"--%>
          <a href="/${QAScriptForm.scriptId}/edit" title="edit QA Script">
            <spring:message code="label.qascript.edit"/>
          </a>
        </li>
        <li>
          <a href="deleteQAScript?scriptId=${QAScriptForm.scriptId}&amp;schemaId=${QAScriptForm.schemaId}" title="delete QA script">
            <spring:message code="label.qascript.delete"/>
          </a>
        </li>
      </c:if>
    </ul>
  </div>


  <h1><spring:message code="label.qascript.view"/></h1>




  <table class="datatable">
    <col class="labelcol"/>
    <col class="entrycol"/>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.schema"/>
      </th>
      <td>
        <a href="${QAScriptForm.schema} title="${QAScriptForm.schema}">
          ${QAScriptForm.schema}
        </a>&#160;
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.shortname"/>
      </th>
      <td>
        ${QAScriptForm.shortName}
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.description"/>
      </th>
      <td>
        ${QAScriptForm.description}
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.resulttype"/>
      </th>
      <td>
        ${QAScriptForm.resultType}
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.scripttype"/>
      </th>
      <td>
        ${QAScriptForm.scriptType}
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.upperlimit"/>
      </th>
      <td>
        ${QAScriptForm.upperLimit}
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.isActive"/>
      </th>
      <td>
        <c:choose>
          <c:when test="${QAScriptForm.active}">
            <input type="checkbox" checked="checked" disabled/>
          </c:when>
          <c:otherwise>
            <input type="checkbox" disabled/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>

    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.fileName"/>
      </th>
      <td>
        <%--  If scriptType is 'FME' don't show the link to the local script file --%>
        <c:if test="${QAScriptForm.scriptType != 'fme'}">
          <a href="${webRoot}/${QAScriptForm.filePath} title="${QAScriptForm.filePath}">
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
          </c:choose>)
        </c:if>
        <c:if test="${QAScriptForm.scriptType == 'fme'}">
          ${QAScriptForm.fileName}
        </c:if>
      </td>
    </tr>
    <%--  If scriptType is 'FME' don't show the link to the remote script file --%>
    <c:if test="${QAScriptForm.scriptType != 'fme'}">
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.qascript.url"/>
        </th>
        <td>
          <c:if test="${!empty QAScriptForm.url}">
            <a href="${QAScriptForm.url} title="${QAScriptForm.url}">View</a>
          </c:if>
        </td>
      </tr>
    </c:if>

  </table>
  <%--  If scriptType is 'FME' don't show the script content --%>
  <c:if test="${QAScriptForm.scriptType != 'fme'}">
    <c:if test="${QAScriptForm.fileName}">
      <pre>${QAScriptForm.scriptContent}</pre>
    </c:if>
  </c:if>

</div>
