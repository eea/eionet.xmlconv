<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:set var="permissions" scope="page" value="${sessionScope['qascript.permissions']}"/>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li id="currenttab">
        <span style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.tab.title"/>
        </span>
      </li>
      <li>
        <a href="/new/scripts/history/${form.scriptId}" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.history"/>
        </a>
      </li>
      <li>
        <a href="/new/scripts/${form.scriptId}/executionHistory" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.executionHistory"/>
        </a>
      </li>
    </ul>
  </div>
  <ed:breadcrumbs-push label="View QA script" level="3"/>

  <div id="operations">
    <ul>
      <li>
        <c:choose>
          <c:when test="${permissions.qsuPrm}">
            <c:choose>
              <c:when test="${form.scriptType != 'fme'}">
                <a href="/qaSandbox/${form.scriptId}/edit" title="label.qasandbox.label.qasandbox.editScript">
                  <spring:message code="label.qascript.run"/>
                </a>
              </c:when>
              <c:otherwise>
                <spring:message code="label.qascript.runservice.title" var="title"/>
                <a href="/qaSandbox?schemaId=${form.schemaId}" title="${title}">
                  <spring:message code="label.qascript.run"/>
                </a>
              </c:otherwise>
            </c:choose>
          </c:when>
          <c:otherwise>
            <a href="/qaSandbox?schemaId=${form.schemaId}" title="${title}">
              <spring:message code="label.qascript.run"/>
            </a>
          </c:otherwise>
        </c:choose>
      </li>
      <c:if test="${permissions.ssdPrm}">
        <li>
          <a href="/scripts/${form.scriptId}/edit" title="edit QA Script">
            <spring:message code="label.qascript.edit"/>
          </a>
        </li>
        <li>
          <a href="/scripts/${form.scriptId}/delete" title="delete QA script" onclick="return confirm('Are you sure you want to delete this script?')">
            <spring:message code="label.qascript.delete"/>
          </a>
        </li>
      </c:if>
    </ul>
  </div>

  <h1><spring:message code="label.qascript.view"/></h1>
  <table class="datatable results">
    <col class="labelcol"/>
    <col class="entrycol"/>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.schema"/>
      </th>
      <td>
        <a href="${form.schema}" title="${form.schema}">
          ${form.schema}
        </a>&#160;
        <br><a href="/schemas/${form.schemaId}">
          Go to Converters schema page
        </a>&#160;
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.shortname"/>
      </th>
      <td>
        ${form.shortName}
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.description"/>
      </th>
      <td>
        ${form.description}
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.resulttype"/>
      </th>
      <td>
        ${form.resultType}
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.scripttype"/>
      </th>
      <td>
        ${form.scriptType}
      </td>
    </tr>
    <tr>
    <th scope="row" class="scope-row">
      <spring:message code="label.qascript.asynchronous"/>
    </th>
    <td>
      ${form.asynchronousExecution}
    </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.heavy"/>
      </th>
      <td>
        ${form.markedHeavy}
      </td>
    </tr>
    <c:choose>
      <c:when test="${form.markedHeavy}">
        <tr>
          <th scope="row" class="scope-row">
            <spring:message code="label.qascript.heavy.reason"/>
          </th>
          <td>
              ${form.markedHeavyReasonOther}
          </td>
        </tr>
      </c:when>
    </c:choose>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.upperlimit"/>
      </th>
      <td>
        ${form.upperLimit}
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.qascript.isActive"/>
      </th>
      <td>
        <c:choose>
          <c:when test="${form.active}">
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
        <c:if test="${form.scriptType != 'fme'}">
          <a href="/${form.filePath}" title="${form.filePath}">${form.fileName}</a>
          &#160;&#160;&#160;&#160;&#160;&#160;(<spring:message code="label.lastmodified"/>:
          <c:choose>
            <c:when test="${!empty form.modified}">
              ${form.modified}
            </c:when>
            <c:otherwise>
              <span style="color:red"><spring:message code="label.fileNotFound"/></span>
            </c:otherwise>
          </c:choose>)
        </c:if>
        <c:if test="${form.scriptType == 'fme'}">
          ${form.fileName}
        </c:if>
      </td>
    </tr>
    <%--  If scriptType is 'FME' don't show the link to the remote script file --%>
    <c:if test="${form.scriptType != 'fme'}">
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.qascript.url"/>
        </th>
        <td>
          <c:if test="${!empty form.url}">
            <a href="${form.url}" title="${form.url}">View</a>
          </c:if>
        </td>
      </tr>
    </c:if>

  </table>
  <%--  If scriptType is 'FME' don't show the script content --%>
  <c:if test="${form.scriptType != 'fme'}">
    <c:if test="${form.fileName}">
      <pre>${form.scriptContent}</pre>
    </c:if>
  </c:if>

</div>
