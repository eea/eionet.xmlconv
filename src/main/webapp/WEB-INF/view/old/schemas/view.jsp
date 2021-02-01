<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="XML Schema or DTD" level="2"/>

<tiles:insertDefinition name="SchemaTabs">
  <tiles:putAttribute name="selectedTab" value="schema"/>
</tiles:insertDefinition>

<h1><spring:message code="label.schema.view"/></h1>

<c:if test="${rootElements.xsduPrm}">
  <div id="operations">
    <ul>
      <li><a href="/schemas/${form.schemaId}/edit"><spring:message code="label.schema.edit.button"/></a></li>
    </ul>
  </div>
</c:if>

<c:if test="${!empty form.schema}">

    <table class="datatable results">
      <caption><spring:message code="label.schema.fldset.properties"/></caption>
      <col class="labelcol"/>
      <col class="entrycol"/>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.url"/>
        </th>
        <td align="left">
          <a href="${form.schema}">${form.schema}</a>
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.description"/>
        </th>
        <td align="left">
            ${form.description}
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.language"/>
        </th>
        <td>
            ${form.schemaLang}
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.dovalidation"/>
        </th>
        <td>
            ${form.doValidation}
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.isBlockerValidation"/>
        </th>
        <td>
            ${form.blocker}
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.expireDate"/>
        </th>
        <td>
            ${form.expireDate}
        </td>
      </tr>
      <c:if test="${form.dtd}">
        <tr>
          <th scope="row" class="scope-row">
            <spring:message code="label.elem.dtdid"/>
          </th>
          <td align="left">
              ${form.dtdId}
          </td>
        </tr>
      </c:if>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.uplSchema.schemaFile"/>
        </th>
        <td>
          <c:if test="${!empty form.uplSchemaFileName}">
            <a href="${form.uplSchemaFileUrl}">
                ${form.uplSchemaFileName}
            </a>&#160;
            <c:if test="${!empty form.lastModified}">
              &#160;&#160;(<spring:message code="label.lastmodified"/>: ${form.lastModified})
            </c:if>
          </c:if>
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.maxExecutionTime"/>
        </th>
        <td align="left">
            ${form.maxExecutionTime}
        </td>
      </tr>
    </table>
  <%--</fieldset>--%>

  <c:if test="${rootElements.rootElemsPresent}">
    <fieldset>
      <legend><spring:message code="label.schema.fldset.rootelems"/></legend>

      <table class="datatable results" width="80%">
        <thead>
        <tr>
          <th scope="col"><span title="Element name"><spring:message code="label.schema.table.element"/></span></th>
          <th scope="col"><span title="Namespace"><spring:message code="label.schema.table.namespace"/></span></th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${!empty rootElements.rootElem}">
          <%--id="elem" name="schema.rootElements" property="rootElem" type="RootElem">--%>
          <c:forEach varStatus="i" items="${rootElements.rootElem}" var="elem">
            <tr class="${i.index % 2 == 1 ? 'zebraeven' : ''}">
              <td>
                  ${elem.name}
              </td>
              <td>
                  ${elem.namespace}
              </td>
            </tr>
          </c:forEach>
        </c:if>
        </tbody>
      </table>

    </fieldset>
  </c:if>
</c:if>
