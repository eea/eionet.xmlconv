<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Edit Stylesheet" level="3"/>
<h1><spring:message code="label.stylesheet.edit"/></h1>

<%-- include Error display --%>
<%--<tiles:insertDefinition name="Error"/>--%>

<form:form action="/stylesheetEdit" method="post" enctype="multipart/form-data" styleClass="cmxform">
  <table class="datatable" style="width:100%">
    <col class="labelcol"/>
    <col class="entrycol"/>
    <tr>
      <th scope="row" class="scope-row">
        <label class="question">
          <spring:message code="label.stylesheet.schema"/>
        </label>
      </th>
      <td>
        <c:if test="${stylesheetForm.schemas}">
          <%--id="relatedSchema" name="stylesheetForm" property="schemas" type="Schema">--%>
          <c:forEach varStatus="index" items="stylsheetForm.schemas" var="relatedSchema">
            <div class="schemaContainer">
              <a href="viewSchemaForm?schemaId=${relatedSchema.id} title="view XML Schema properties">
                ${relatedSchema.schema}
              </a>
              <a href='#' class="delSchemaLink" title="Delete XML Schema relation">
                <img style='border:0' src='<c:url value="/images/button_remove.gif" />' alt='Remove'/>
              </a><br/>
              <input type="hidden" name="schemaIds" value="${relatedSchema.id}">
            </div>
          </c:forEach>
        </c:if>
        <div id="newSchemasContainer">
  <%--id="newSchema" name="stylesheetForm" property="newSchemas">--%>
          <c:forEach items="${stylsheetForm.newSchemas}" var="newSchema">
            <div class="newSchemaContainer">
              <input type="url" name="newSchemas" style="width:400px;" class="newSchema" value="${newSchema.schema_1}">
              <a href='#' class="delNewSchemaLink">
                <img style='border:0' src='<c:url value="/images/button_remove.gif"/>' alt='Remove'/>
              </a><br/>
            </div>
          </c:forEach>
        </div>
        <br/>
        <%-- TODO CHECK FOR REMOVAL --%>
        <jsp:include page="ManageStylesheetSchemas.jsp"/>
      </td>
    </tr>
      <%-- /*
      <tr>
          <td>
              <label class="question" for="selDDSchema">
                  <spring:message code="label.stylesheet.selectDDSchema"/>
              </label>
        </td>
        <td>
            <select name="xmlSchema"  size="10" onchange="setSchema()" style="width:98%" id="selDDSchema">
                  <option selected="selected" value="">
                      --
                  </option>
                  <c:if iterate id="schema" name="stylesheet.DDSchemas"  type="Schema">
                      <option value="<bean:write name="schema" property="schema" />">
                          <bean:write name="schema" property="schema" />
                          <c:if notEqual name="schema" property="table" value="">
                              &nbsp;-&nbsp;
                              <bean:write name="schema" property="table" />&nbsp;(
                              <bean:write name="schema" property="dataset" /> -
                              <bean:write name="schema" property="datasetReleased" format="<%= Properties.dateFormatPattern%>" />)
                          </c:if notEqual>
                      </option>
                  </c:if iterate>
              </select>
             </td>
          </tr>
          */ --%>
    <tr>
      <th scope="row" class="scope-row">
        <label class="question" for="selOutputType">
          <spring:message code="label.stylesheet.outputtype"/>
        </label>
      </th>
      <td>
        <select name="outputtype" style="width:100px" id="selOutputType">
          <c:forEach items="${stylesheet.outputtype.convTypes}" var="opt">
            <c:choose>
              <c:when test="${opt.convType == stylesheetForm.outputtype}">
                <option selected="selected" value="${opt.convType}">
                  ${opt.convType}
                </option>
              </c:when>
              <c:otherwise>
                <option value="${opt.convType}">
                  ${opt.convType}
                </option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>

    <c:if test="${stylesheetForm.showDependsOnInfo == 'true'}">
      <%--<bean:define id="depOn" name="stylesheetForm" property="dependsOn" scope="request" type="java.lang.String"/>--%>
      <tr>
        <th scope="row" class="scope-row">
          <label class="question" for="selDependsOn">
            <spring:message code="label.stylesheet.dependsOn"/>
          </label>
        </th>
        <td>
          <select name="dependsOn" id="selDependsOn">
            <c:choose>
              <c:when test="${stylesheetForm.dependsOn}">
                <option value="" selected="selected">--</option>
              </c:when>
              <c:otherwise>
                <option value="">--</option>
              </c:otherwise>
            </c:choose>
      <%--id="st" scope="request" name="stylesheetForm" property="existingStylesheets">--%>
            <c:forEach items="${stylesheetForm.existingStylesheets}">
              <c:choose>
                <c:when test="${st.convId == depOn}">
                  <option value="${st.convId}" selected="selected">
                    ${st.xslFileName}
                  </option>
                </c:when>
                <c:otherwise>
                  <option value="${st.convId}">
                    ${st.xslFileName}
                  </option>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </select>
        </td>
      </tr>

    </c:if>


    <tr>
      <th scope="row" class="scope-row">
        <label class="question" for="txtDescription">
          <spring:message code="label.stylesheet.description"/>
        </label>
      </th>
      <td>
        <form:input path="description" style="width:500px" styleId="txtDescription"/>
        <form:hidden path="stylesheetId"/>
      </td>
    </tr>
    <tr>
      <th scope="row" class="scope-row">
        <label class="question" for="txtXsl">
          <spring:message code="label.stylesheet.xslfile"/>
        </label>
      </th>
      <td>
        <a href="${webRoot}/${xsl.stylesheetForm}" title="${xsl.stylesheetForm}" class="link-xsl">
          ${xslFileName.stylesheetForm}
        </a>
        <span style="margin-left:10px">(<spring:message code="label.lastmodified"/>:
                <c:choose>
                  <c:when test="${stylesheetForm.modified}">
                    ${stylesheetForm.modified}
                  </c:when>
                  <c:otherwise>
                    <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                  </c:otherwise>
                </c:choose>)</span>
        <div>
          <input type="file" name="xslfile" size="68"/>
          <button type="submit" class="button" name="action" value="upload">
            <spring:message code="label.stylesheet.upload"/>
          </button>
        </div>
      </td>
    </tr>
    <c:if test="${stylesheetForm.xslFileName}">
      <tr>
        <td colspan="2">
          <form:textarea path="xslContent" style="width: 98%;" rows="20" cols="55" id="txtXsl"/>
        </td>
      </tr>
      <tr>
        <td>&#160;</td>
        <td>
          <button type="submit" name="action" value="save">
            <spring:message code="label.stylesheet.save"/>
          </button>
          <form:hidden path="xslFileName"/>
          <form:hidden path="checksum" name="stylesheetForm"/>
        </td>
      </tr>
    </c:if>
  </table>
</form:form>
