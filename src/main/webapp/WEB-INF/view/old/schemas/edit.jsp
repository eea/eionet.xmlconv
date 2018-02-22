<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<tiles:insertDefinition name="SchemaTabs">
  <tiles:putAttribute name="selectedTab" value="schema"/>
</tiles:insertDefinition>

<ed:breadcrumbs-push label="Edit XML Schema or DTD" level="2"/>
<h1><spring:message code="label.schema.edit"/></h1>

<form:form servletRelativeAction="/schemas" method="post" enctype="multipart/form-data"
           modelAttribute="form">
  <form:errors path="*" cssClass="error-msg" element="div"/>
  <fieldset class="fieldset">
    <legend><spring:message code="label.schema.fldset.properties"/></legend>
    <div class="row">
      <div class="columns small-4">
        <label class="question required" for="txtSchemaUrl">
          <spring:message code="label.schema.url"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input path="schema" maxlength="255" style="width:500px" styleId="txtSchemaUrl"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtDescription">
          <spring:message code="label.schema.description"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:textarea path="description" rows="2" cols="30" style="width:500px" id="txtDescription"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtSchemaLang">
          <spring:message code="label.schema.language"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:select path="schemaLang" id="txtSchemaLang">
          <form:options items="${form.schemaLanguages}"/>
        </form:select>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtValidation">
          <spring:message code="label.schema.dovalidation"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:checkbox path="doValidation" id="txtValidation"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtBlockerValidation">
          <spring:message code="label.schema.isBlockerValidation"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:checkbox path="blocker" id="txtBlockerValidation"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtExpireDate">
          <spring:message code="label.schema.expireDate"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input path="expireDate" id="txtExpireDate"/> (dd/MM/yyyy)
      </div>
    </div>
    <c:if test="${form.dtd}">
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="txtDtdId">
            <spring:message code="label.elem.dtdid"/>
          </label>
        </div>
        <div class="columns small-8">
          <form:input path="dtdId" maxlength="50" size="50" id="txtDtdId"/>
        </div>
      </div>
    </c:if>
  </fieldset>
  <c:if test="${rootElements.xsduPrm}">
    <button type="submit" class="button" name="update">
      <spring:message code="label.schema.save"/>
    </button>
    &nbsp;
  </c:if>
  <c:if test="${rootElements.xsddPrm}">
    <button type="submit" class="button" name="delete">
      <spring:message code="label.schema.delete"/>
    </button>
  </c:if>

  <fieldset class="fieldset">
    <legend><spring:message code="label.schema.fldset.localfile"/></legend>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtSchemaFile">
          <spring:message code="label.uplSchema.schemaFile"/>
        </label>
      </div>
      <div class="columns small-8">
        <c:if test="${!empty form.uplSchemaFileName}">
          <a href="${form.uplSchemaFileUrl}"
             title="${form.uplSchemaFileUrl}">${form.uplSchemaFileUrl}</a>&#160;
          <c:if test="${form.lastModified}">
            &#160;&#160;(<spring:message code="label.lastmodified"/>: ${form.lastModified})
          </c:if>
        </c:if>
      </div>
    </div>
    <c:if test="${rootElements.xsduPrm}">
      <div class="row">
        <input type="file" name="schemaFile" size="20" style="width:400px" styleId="txtSchemaFile"/>
      </div>
    </c:if>
    <c:if test="${rootElements.xsduPrm}">
      <button type="submit" class="button" value="edit">
        <spring:message code="label.uplSchema.upload"/>
      </button>
    </c:if>
    <c:if test="${form.uplSchemaFileName}">
      <c:if test="${rootElements.xsddPrm}">
        <button type="submit" name="delete">
          <spring:message code="label.schema.deleteFile"/>
        </button>
      </c:if>
      <c:if test="${rootElements.xsduPrm}">
        <c:if test="${rootElements.schemaIdRemoteUrl}">
          <button type="submit" name="diff">
            <spring:message code="label.uplSchema.checkupdates"/>
          </button>
        </c:if>
      </c:if>
    </c:if>
    <c:if test="${!form.uplSchemaFileName}">
      <c:if test="${rootElements.schemaIdRemoteUrl}">
        <button type="submit" name="diff">
          <spring:message code="label.uplSchema.createcopy"/>
        </button>
      </c:if>
    </c:if>
  </fieldset>

  <%--TODO fix all ifs and UI--%>

  <fieldset>
    <legend><spring:message code="label.schema.fldset.rootelems"/></legend>

    <%--<c:if test="${!empty rootElements.rootElem}">--%>
      <table class="datatable results" width="80%">
        <thead>
        <tr>
          <th scope="col"><span title="Element name"><spring:message code="label.schema.table.element"/></span>
          </th>
          <th scope="col"><span title="Namespace"><spring:message code="label.schema.table.namespace"/></span>
          </th>
          <c:if test="${rootElements.xsduPrm}">
            <th scope="col"></th>
          </c:if>
        </tr>
        </thead>
        <tbody>
        <%--<c:if test="${rootElements.rootElem}">--%>
          <c:forEach varStatus="index" items="${rootElements.rootElem}" var="elem">
            <tr class="${i.index % 2 == 1 ? 'zebraeven' : ''}">
              <td>
                  ${elem.name}
              </td>
              <td>
                  ${elem.namespace}
              </td>
              <c:if test="${rootElements.xsduPrm}">
                <td align="center">
                    <%--onclick='return elementDelete("<bean:write name="elem" property="name"/>");'>--%>
                  <a href="deleteElem?elemId=${elem.elemId}">
                    <img src="/images/delete.gif" altKey="label.delete" title="delete root element"/>
                  </a>
                </td>
              </c:if>
            </tr>
          </c:forEach>
        <%--</c:if>--%>
        </tbody>
      </table>
    <%--</c:if>--%>

    <%--<c:if test="${user}">--%>
      <%--<c:if test="${rootElements.xsduPrm}">--%>
        <table class="formtable">
          <col class="labelcol"/>
          <col class="entrycol"/>
          <tr class="zebraeven">
            <td>
              <label class="question" for="txtElemName">
                <spring:message code="label.schema.table.element"/>
              </label>
            </td>
            <td>
              <form:input path="elemName" maxlength="255" style="width:250px" id="txtElemName"/>
            </td>
          </tr>
          <tr>
            <td>
              <label class="question" for="txtNamespace">
                <spring:message code="label.schema.table.namespace"/>
              </label>
            </td>
            <td>
              <form:input path="namespace" maxlength="255" style="width:250px" id="txtNamespace"/>
            </td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td>
              <button type="submit" class="button" name="elementAdd">
                <spring:message code="label.element.add"/>
              </button>
            </td>
          </tr>
        </table>
      <%--</c:if>--%>
    <%--</c:if>--%>
  </fieldset>
  <div style="display:none">
    <form:hidden path="schemaId"/>
    <form:hidden path="uplSchemaFileName"/>
    <form:hidden path="uplSchemaId"/>
      <%--<form:hidden path="schema"/>--%>
  </div>
</form:form>
