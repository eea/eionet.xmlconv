<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Add Stylesheet" level="3"/>
<h1><spring:message code="label.stylesheet.add"/></h1>

<form:form servletRelativeAction="/conversions/add" method="post" enctype="multipart/form-data" modelAttribute="form">
  <table class="datatable" style="width:100%">
    <col class="labelcol"/>
    <col class="entrycol"/>
    <tr>
      <th scope="row" class="scope-row">
        <label class="question" for="txtSchemaUrl">
          <spring:message code="label.stylesheet.schema"/>
        </label>
      </th>
      <td>
        <div id="newSchemasContainer">
          <div class="newSchemaContainer">
            <c:choose>
              <c:when test="${!empty schema}">
                <input type="url" name="newSchemas" value="${schema}" style="width:400px" class="newSchema" id="txtSchemaUrl"/>
              </c:when>
              <c:otherwise>
                <input type="url" name="newSchemas" maxlength="255" style="width:400px" class="newSchema" id="txtSchemaUrl"/>
              </c:otherwise>
            </c:choose>
            <a href='#' class="delNewSchemaLink">
              <img style='border:0' src="/images/button_remove.gif" alt='Remove'/></a><br/>
          </div>
        </div>
        <jsp:include page="ManageStylesheetSchemas.jsp"/>
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <label class="question" for="selOutputType">
          <spring:message code="label.stylesheet.outputtype"/>
        </label>
      </th>
      <td>
        <select name="outputtype" style="width:100px;" id="selOutputType">
            <%--id="opt" name="stylesheet.outputtype" scope="session" property="convTypes" type="ConvType">--%>
          <c:forEach items="${sessionScope['stylesheet.outputtype']}" var="opt">
            <c:set var="selected">
              <c:if test="${opt.convType == 'HTML'}">selected="selected"</c:if>
            </c:set>
            <option value="${opt.convType}" ${selected}>
              ${opt.convType}
            </option>
          </c:forEach>
        </select>
      </td>
    </tr>

    <c:if test="${schemaInfo}">
      <c:if test="${schemaInfo.schemaLang == 'EXCEL'}">
        <tr>
          <th scope="row" class="scope-row">
            <label class="question" for="chkDepends">
              <spring:message code="label.stylesheet.dependsOn"/>
            </label>
          </th>
          <td>
            <select name="dependsOn" id="chkDepends">
              <option value="" selected="selected">--</option>
                <%--iterate id="st" scope="request" name="existingStylesheets">--%>
              <c:forEach items="${existingStylesheets}" var="st">
                <option value="${st.convId}">
                  ${st.xslFileName}
                </option>
              </c:forEach>
            </select>
          </td>
        </tr>
      </c:if>
    </c:if>
    <tr>
      <th scope="row" class="scope-row">
        <label class="question" for="txtDescription">
          <spring:message code="label.stylesheet.description"/>
        </label>
      </th>
      <td>
        <input type="text" name="description" style="width:400px" id="txtDescription"/>
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <label class="question" for="fileXsl">
          <spring:message code="label.stylesheet.xslfile"/>
        </label>
      </th>
      <td>
        <input type="file" name="xslfile" style="width:400px" size="64" id="fileXsl"/>
      </td>
    </tr>
    <tr>
      <td>&#160;</td>
      <td>
        <button type="submit" class="button" value="save">
          <spring:message code="label.xsl.save"/>
        </button>
        <%--<html:cancel styleClass="button">
          <spring:message code="label.stylesheet.cancel"/>
        </html:cancel>--%>
      </td>
    </tr>
  </table>
</form:form>
