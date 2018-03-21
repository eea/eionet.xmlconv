<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li id="currenttab"><span style="color: black; text-decoration: none;"> </span>
        <spring:message code="label.qascript.tab.title"/>
      </li>
      <li>
        <a href="/scripts/${form.scriptId}/history" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.history"/>
        </a>
      </li>
    </ul>
  </div>
  <ed:breadcrumbs-push label="Edit QA script" level="3"/>

  <%--<h1><spring:message code="label.qascript.edit"/></h1>--%>

  <form:form action="/scripts" method="post" enctype="multipart/form-data" modelAttribute="form">
    <form:errors path="*" cssClass="error-msg" element="div"/>
    <fieldset class="fieldset">
      <legend><spring:message code="label.qascript.edit"/></legend>
      <div class="row">
        <div class="columns small-4">
          <label class="question">
            <spring:message code="label.qascript.schema"/>
          </label>
        </div>
        <div class="columns small-8">
            ${form.schema}
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="txtShortName">
            <spring:message code="label.qascript.shortname"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:input name="form" path="shortName" id="txtShortName" size="64"/>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="txtDescription">
            <spring:message code="label.qascript.description"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:textarea path="description" rows="2" cols="30" style="width:400px" id="txtDescription"/>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="selContentType">
            <spring:message code="label.qascript.resulttype"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:select name="form" path="resultType" id="selContentType">
            <form:options items="${resulttypes}" itemValue="convType" itemLabel="convType"/>
          </form:select>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="selScriptType">
            <spring:message code="label.qascript.scripttype"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:select name="form" path="scriptType" id="selScriptType" disabled="false">
            <form:options items="${scriptlangs}" itemValue="convType" itemLabel="convType"/>
          </form:select>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="txtUpperLimit">
            <spring:message code="label.qascript.upperlimit"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:input id="txtUpperLimit" size="3" path="upperLimit"/>
        </div>
      </div>
    </fieldset>

    <fieldset class="fieldset">
      <legend>Script file properties</legend>

      <div class="row">
        <div class="columns small-4">
          <label class="question" for="txtFile">
            <spring:message code="label.qascript.fileName"/>
          </label>
        </div>
        <div class="columns small-8">
            <%--  If scriptType is 'FME' don't show the link to the local script file --%>
          <c:if test="${form.scriptType == 'fme'}">
            <a href="/${form.filePath}" title="${form.filePath}">
                ${form.fileName}
            </a>
            &#160;&#160;&#160;&#160;&#160;&#160;(<spring:message code="label.lastmodified"/>:
            <c:choose>
              <c:when test="${form.modified}">
                ${form.modified}
              </c:when>
              <c:otherwise>
                <span style="color:red"><spring:message code="label.fileNotFound"/></span>
              </c:otherwise>
            </c:choose>
            )
          </c:if>
            <%--  If scriptType is 'FME' don't show the link to the local script file --%>
          <c:if test="${form.scriptType == 'fme'}">
            ${form.fileName}
          </c:if>
        </div>
      </div>
        <%--  If scriptType is 'FME' don't show the FileUpload --%>
      <c:if test="${form.scriptType != 'fme'}">
        <div class="row">
          <div class="columns small-4">
            &#160;
          </div>
          <div class="columns small-8">
            <button type="submit" class="button" name="upload">
              <spring:message code="label.qascript.upload"/>
            </button>
            <input type="file" name="scriptFile" style="width:400px" size="64"/>
          </div>
        </div>
      </c:if>

      <div class="row">
        <div class="columns small-4">
          <label class="question" for="txtUrl">
            <spring:message code="label.qascript.url"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:input id="txtUrl" path="url" size="107"/>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="isActive">
            <spring:message code="label.qascript.isActive"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:checkbox path="active" id="isActive"/>
          <%--<form:hidden path="active" value="false"/>--%>
        </div>
      </div>

        <%--  If scriptType is 'FME' don't show the 'Check for updates' --%>
      <c:if test="${form.scriptType != 'fme'}">
        <div class="row">
          <div class="columns small-4">

          </div>
          <div class="columns small-8">
            <c:if test="${!empty form.fileName}">
              <button type="submit" class="button" name="diff">
                <spring:message code="label.qascript.checkupdates"/>
              </button>
            </c:if>
          </div>
        </div>
      </c:if>
      <c:if test="${!empty form.fileName}">
        <c:if test="${form.scriptType != 'fme'}">
          <div class="row">
            <label class="question" for="txtUrl">
              <spring:message code="label.qascript.source"/>
            </label>
            <form:textarea path="scriptContent" style="width: 98%;" rows="20" cols="55" id="txtFile"/>
          </div>
        </c:if>

        <button type="submit" class="button" name="update">
          <spring:message code="label.qascript.save"/>
        </button>
        <form:hidden path="fileName"/>
        <form:hidden path="checksum" />
        <form:hidden path="scriptId" />
        <form:hidden path="schemaId" />
        <%--<form:hidden path="active" />--%>

        <%--<input type="file" name="scriptFile" style="width:400px" size="64"/>--%>
      </c:if>
    </fieldset>
  </form:form>
</div>

