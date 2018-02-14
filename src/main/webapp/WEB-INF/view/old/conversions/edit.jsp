<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Edit Stylesheet" level="3"/>
<h1><spring:message code="label.stylesheet.edit"/></h1>

<form:form servletRelativeAction="/conversions" method="post" enctype="multipart/form-data" modelAttribute="form">
  <form:errors path="*" cssClass="error-msg" element="div" />
  <fieldset class="fieldset">
    <legend><spring:message code="label.stylesheet.edit"/></legend>
    <div class="row">
      <div class="columns small-4">
        <label class="question">
          <spring:message code="label.stylesheet.schema"/>
        </label>
      </div>
      <div class="columns small-8">
        <c:if test="${!empty form.schemas}">
          <c:forEach varStatus="index" items="${form.schemas}" var="schema">
            <div class="schemaContainer">
              <a href="/schemas/${schema.id}" title="View XML Schema properties">
                  ${schema.schema}
              </a>
              <%--<a href='#' class="delSchemaLink" title="Delete XML Schema relation">
                <img style='border:0' src="/images/button_remove.gif" alt='Remove'/>
              </a><br/>
              <input type="hidden" name="schemaIds" value="${relatedSchema.id}">--%>
            </div>
          </c:forEach>
        </c:if>
        <%--<div id="newSchemasContainer">
          <c:forEach items="${form.newSchemas}" var="newSchema">
            <div class="newSchemaContainer">
              <input type="url" name="newSchemas" style="width:400px;" class="newSchema" value="${newSchema}">
              <a href='#' class="delNewSchemaLink">
                <img style='border:0' src="/images/button_remove.gif" alt='Remove'/>
              </a><br/>
            </div>
          </c:forEach>
        </div>--%>
        <br/>
          <%--&lt;%&ndash; TODO CHECK FOR REMOVAL &ndash;%&gt;--%>
          <%--<jsp:include page="ManageStylesheetSchemas.jsp"/>--%>
      </div>
    </div>
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
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="selOutputType">
          <spring:message code="label.stylesheet.outputtype"/>
        </label>
      </div>
      <div class="columns small-8">
        <select name="outputtype" style="width:100px" id="selOutputType">
          <c:forEach items="${outputtypes.convTypes}" var="opt">
            <c:choose>
              <c:when test="${opt.convType == form.outputtype}">
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
      </div>
    </div>
    <c:if test="${form.showDependsOnInfo}">
      <%--<bean:define id="depOn" name="form" property="dependsOn" scope="request" type="java.lang.String"/>--%>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="selDependsOn">
            <spring:message code="label.stylesheet.dependsOn"/>
          </label>
        </div>
        <div class="columns small-8">
          <select name="dependsOn" id="selDependsOn">
            <c:choose>
              <c:when test="${form.dependsOn}">
                <option value="" selected="selected">--</option>
              </c:when>
              <c:otherwise>
                <option value="">--</option>
              </c:otherwise>
            </c:choose>
              <%--id="st" scope="request" name="form" property="existingStylesheets">--%>
            <c:forEach items="${form.existingStylesheets}">
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
        </div>
      </div>
    </c:if>

    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtDescription">
          <spring:message code="label.stylesheet.description"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input path="description" type="text" style="width:500px" styleId="txtDescription"/>
        <form:hidden path="stylesheetId"/>
      </div>
    </div>
  </fieldset>
  <fieldset class="fieldset">
    <legend>XSL file properties</legend>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtXsl">
          <spring:message code="label.stylesheet.xslfile"/>
        </label>
      </div>
      <div class="columns small-8">
        <a href="${webRoot}/${xsl.stylesheetForm}" title="${xsl.stylesheetForm}" class="link-xsl">
            ${xslFileName.stylesheetForm}
        </a>
        <span style="margin-left:10px">(<spring:message code="label.lastmodified"/>:
                <c:choose>
                  <c:when test="${!empty form.modified}">
                    ${form.modified}
                  </c:when>
                  <c:otherwise>
                    <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                  </c:otherwise>
                </c:choose>)</span>
        <div>
          <input type="file" name="xslfile" size="68"/>
          <button type="submit" class="button" name="upload">
            <spring:message code="label.stylesheet.upload"/>
          </button>
        </div>
      </div>
    </div>
    <c:if test="${!empty form.xslFileName}">
      <div class="row">
        <%--style="width: 98%;" rows="20" cols="55" id="txtXsl"--%>
        <form:textarea rows="10" path="xslContent" />
      </div>
    </c:if>
  </fieldset>
  <div class="row">
    <button type="submit" class="button" name="save">
      <spring:message code="label.stylesheet.save"/>
    </button>
    <form:hidden path="xslFileName"/>
    <form:hidden path="checksum" name="form"/>
  </div>
</form:form>
