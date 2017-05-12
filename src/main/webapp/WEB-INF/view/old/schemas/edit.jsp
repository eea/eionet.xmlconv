<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="XML Schema or DTD" level="2"/>

<c:if test="${!empty schemaForm.schema}">
  <div id="tabbedmenu">
    <ul>
      <li id="currenttab">
        <span style="color: black; text-decoration: none;"
              title='<spring:message code="label.tab.title.schema"/>'><spring:message
                code="label.tab.title.schema"/></span>
      </li>
      <li>
        <a href="<spring:url value="/old/schemas/{id}/conversions"><spring:param name="id" value="${schemaForm.schemaId}"/></spring:url>"><spring:message
                code="label.tab.title.xsl"/></a>
      </li>
      <li>
        <a href="<spring:url value="/old/schemas/{id}/scripts"><spring:param name="id" value="${schemaForm.schemaId}"/></spring:url>"><spring:message
                code="label.tab.title.scripts"/></a>
      </li>
    </ul>
  </div>
</c:if>

<h1><spring:message code="label.schema.view"/></h1>

<%-- include Error display --%>
<%--<tiles:insertDefinition name="Error"/>--%>

<c:if test="${!empty schemaForm.schema}">

  <c:if test="${rootElements.xsduPrm}">
    <div id="operations">
      <ul>
        <li><a href="<spring:url value="/schemas/{id}/edit">
          <spring:param name="id" value="${schemaForm.schemaId}"/>
          </spring:url>"><spring:message code="label.schema.edit.button"/></a>
        </li>
      </ul>
    </div>
  </c:if>

  <fieldset>
    <legend><spring:message code="label.schema.fldset.properties"/></legend>
    <table class="datatable">
      <col class="labelcol"/>
      <col class="entrycol"/>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.url"/>
        </th>
        <td align="left">
          <a href="${schemaForm.schema}">${schemaForm.schema}</a>
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.description"/>
        </th>
        <td align="left">
            ${schemaForm.description}
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.language"/>
        </th>
        <td>
            ${schemaForm.schemaLang}
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.dovalidation"/>
        </th>
        <td>
            ${schemaForm.doValidation}
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.isBlockerValidation"/>
        </th>
        <td>
            ${schemaForm.blocker}
        </td>
      </tr>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.schema.expireDate"/>
        </th>
        <td>
            ${schemaForm.longExpireDate}
        </td>
      </tr>
      <c:if test="${schemaForm.dtd}">
        <tr>
          <th scope="row" class="scope-row">
            <spring:message code="label.elem.dtdid"/>
          </th>
          <td align="left">
              ${schemaForm.dtdId}
          </td>
        </tr>
      </c:if>
      <tr>
        <th scope="row" class="scope-row">
          <spring:message code="label.uplSchema.schemaFile"/>
        </th>
        <td>
          <c:if test="${!empty schemaForm.uplSchemaFileName}">
            <a href="${schemaForm.uplSchemaFileUrl}">
                ${schemaForm.uplSchemaFileName}
            </a>&#160;
            <c:if test="${!empty schemaForm.lastModified}">
              &#160;&#160;(<spring:message code="label.lastmodified"/>: ${schemaForm.lastModified})
            </c:if>
          </c:if>
        </td>
      </tr>
    </table>
  </fieldset>
  <c:if test="${rootElements.rootElemsPresent}">
    <fieldset>
      <legend><spring:message code="label.schema.fldset.rootelems"/></legend>

      <table class="datatable" width="80%">
        <thead>
        <tr>
          <th scope="col"><span title="Element name"><spring:message code="label.schema.table.element"/></span></th>
          <th scope="col"><span title="Namespace"><spring:message code="label.schema.table.namespace"/></span></th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${!empty rootElements.rootElem}">
          <%--id="elem" name="schema.rootElements" property="rootElem" type="RootElem">--%>
          <c:forEach varStatus="i" items="${schema.rootElements.rootElem}" var="elem">
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
