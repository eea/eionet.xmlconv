<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Add Stylesheet" level="3"/>
<%--<h1><spring:message code="label.stylesheet.add"/></h1>--%>

<form:form servletRelativeAction="/conversions" method="post" enctype="multipart/form-data" modelAttribute="form">
  <form:errors path="*" cssClass="error-msg" element="div" />
  <fieldset class="fieldset">
    <legend><spring:message code="label.stylesheet.add"/></legend>
    <div class="row">
      <div class="columns small-4">
        <label class="" for="txtSchemaUrl">
          <spring:message code="label.stylesheet.schema"/>
        </label>
      </div>
      <div class="columns small-8">
        <div id="newSchemasContainer">
          <div class="newSchemaContainer">
            <c:choose>
              <c:when test="${!empty schemaUrl}">
                <input type="url" name="newSchemas" value="${schemaUrl}" class="newSchema" id="txtSchemaUrl"/>
              </c:when>
              <c:otherwise>
                <input type="url" name="newSchemas" maxlength="255" class="newSchema" id="txtSchemaUrl"/>
              </c:otherwise>
            </c:choose>
            <a href='#' class="delNewSchemaLink"><img style='border:0' src='<c:url value="/static/images/button_remove.gif" />' alt='Remove' /></a><br/>
          </div>
        </div>
        <jsp:include page="ManageStylesheetSchemas.jsp"/>
      </div>
    </div>

    <div class="row">
      <div class="columns small-4">
        <label class="" for="selOutputType">
          <spring:message code="label.stylesheet.outputtype"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:select path="outputtype" id="selOutputType">
          <c:forEach items="${outputtypes.convTypes}" var="type">
            <form:option value="${type.convType}">
              ${type.convType}
            </form:option>
          </c:forEach>
        </form:select>
      </div>
    </div>

    <c:if test="${!empty schemaInfo}">
      <c:if test="${schemaInfo.schemaLang == 'EXCEL'}">
        <div class="row">
          <div class="columns small-4">
            <label class="" for="chkDepends">
              <spring:message code="label.stylesheet.dependsOn"/>
            </label>
          </div>
          <div class="columns small-4">
            <select name="dependsOn" id="chkDepends">
              <option value="" selected="selected">--</option>
              <c:forEach items="${existingStylesheets}" var="st">
                <option value="${st.convId}">
                    ${st.xslFileName}
                </option>
              </c:forEach>
            </select>
          </div>
        </div>
      </c:if>
    </c:if>

    <div class="row">
      <div class="columns small-4">
        <label class="" for="txtDescription">
          <spring:message code="label.stylesheet.description"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input  path="description" id="txtDescription"/>
      </div>
    </div>

    <div class="row">
      <div class="columns small-4">
        <label class="" for="fileXsl">
          <spring:message code="label.stylesheet.xslfile"/>
        </label>
      </div>
      <div class="columns small-8">
        <input type="file" name="xslfile" size="64" id="fileXsl"/>
      </div>
    </div>
  </fieldset>
  <div class="row">
    <div class="columns small-4">
      <label class=""></label>
    </div>
    <div class="columns small-8">
      <button type="submit" class="button" name="add">
        <spring:message code="label.xsl.save"/>
      </button>
    </div>
  </div>
  </fieldset>
</form:form>
