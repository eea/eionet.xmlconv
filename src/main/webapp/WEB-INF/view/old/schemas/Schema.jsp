<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<%-- TODO check if this file is still used--%>
<div id="tabbedmenu">

  <ul>
    <li id="currenttab">
      <spring:message code="label.tab.title.schema" var="schemaTab"/>
      <span style="color: black; text-decoration: none;" title='${schemaTab}'>
        ${schemaTab}
      </span>
    </li>
    <li>
      <a href="/conversions?schema=${schemaForm.schema}" titleKey="label.tab.title.xsl" style="color: black; text-decoration: none;">
        <spring:message code="label.tab.title.xsl"/>
      </a>
    </li>
    <li>
      <a href="/schemas/${schemaForm.schemaId}" titleKey="label.tab.title.scripts" style="color: black; text-decoration: none;">
        <spring:message code="label.tab.title.scripts"/>
      </a>
    </li>
  </ul>
</div>

<ed:breadcrumbs-push label="Edit XML Schema or DTD" level="2"/>
<h1><spring:message code="label.schema.edit"/></h1>

<form:form servletRelativeAction="/schemas/update" method="post" enctype="multipart/form-data" modelAttribute="schemaForm">
  <fieldset>
    <legend><spring:message code="label.schema.fldset.properties"/></legend>
    <table class="formtable">
      <col class="labelcol"/>
      <col class="entrycol"/>
      <tr class="zebraeven">
        <td>
          <label class="question required" for="txtSchemaUrl">
            <spring:message code="label.schema.url"/>
          </label>
        </td>
        <td align="left">
          <c:choose>
            <c:when test="${user}">
              <form:input path="schema" maxlength="255" style="width:500px" styleId="txtSchemaUrl"/>
            </c:when>
            <c:otherwise>
              <a href="${schemaForm.schema} title="${schemaForm.schema}">${schemaForm.schema}</a>&#160;
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="txtDescription">
            <spring:message code="label.schema.description"/>
          </label>
        </td>
        <td align="left">
          <c:choose>
            <c:when test="${user}">
              <form:textarea path="description" rows="2" cols="30" style="width:500px" id="txtDescription"/>
            </c:when>
            <c:otherwise>
              ${schemaForm.description}
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr class="zebraeven">
        <td>
          <label class="question" for="txtSchemaLang">
            <spring:message code="label.schema.language"/>
          </label>
        </td>
        <td>
          <c:choose>
            <c:when test="${user}">
              <form:select path="schemaLang" id="txtSchemaLang">
                <form:options items="schemaLanguages"/>
              </form:select>
            </c:when>
            <c:otherwise>
              ${schemaForm.schemaLang}
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="txtValidation">
            <spring:message code="label.schema.dovalidation"/>
          </label>
        </td>
        <td>
          <c:choose>
            <c:when test="${user}">
              <form:checkbox path="doValidation" id="txtValidation"/>
            </c:when>
            <c:otherwise>
              ${schemaForm.doValidation}
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="txtBlockerValidation">
            <spring:message code="label.schema.isBlockerValidation"/>
          </label>
        </td>
        <td>
          <c:choose>
            <c:when test="${user}">
              <form:checkbox path="blocker" id="txtBlockerValidation"/>
            </c:when>
            <c:otherwise>
              ${schemaForm.blockerr}
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr class="zebraeven">
        <td>
          <label class="question" for="txtExpireDate">
            <spring:message code="label.schema.expireDate"/>
          </label>
        </td>
        <td>
          <form:input path="expireDate" id="txtExpireDate"/> (dd/MM/yyyy)
        </td>
      </tr>
      <c:if test="${schemaForm.dtd}">
        <tr>
          <td>
            <label class="question" for="txtDtdId">
              <spring:message code="label.elem.dtdid"/>
            </label>
          </td>
          <td align="left">
            <c:choose>
              <c:when test="${user}">
                <form:input path="dtdId" maxlength="50" size="50" id="txtDtdId"/>
              </c:when>
              <c:otherwise>
                ${schemaForm.dtdId}/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:if>
      <tr>
        <td></td>
        <td>
          <c:if test="${schema.rootElements == 'xsduPrm'}">
            <button type="submit" class="button" class="button" name="action" value="schemaUpdate">
              <spring:message code="label.schema.save"/>
            </button>
            &nbsp;
          </c:if>
          <c:if test="${rootElements.xsddPrm}">
            <button type="submit" name="action" class="button" value="delete">
              <spring:message code="label.schema.delete"/>"
            </button>
          </c:if>
        </td>
      </tr>
    </table>
  </fieldset>
  <fieldset>
    <legend><spring:message code="label.schema.fldset.localfile"/></legend>
    <table class="formtable">
      <col class="labelcol"/>
      <col class="entrycol"/>
      <tr class="zebraeven">
        <td>
          <label class="question" for="txtSchemaFile">
            <spring:message code="label.uplSchema.schemaFile"/>
          </label>
        </td>
        <td>
          <c:if test="${schemaForm.uplSchemaFileName}">
            <a href="${schemaForm.uplSchemaFileUrl} title="${schemaForm.uplSchemaFileUrl}">${schemaForm.uplSchemaFileUrl}</a>&#160;
            <c:if test="${schemaForm.lastModified}">
              &#160;&#160;(<spring:message code="label.lastmodified"/>: ${schemaForm.lastModified})
            </c:if>
          </c:if>
        </td>
      </tr>
      <c:if test="${rootElements.xsduPrm}">
        <tr>
          <td></td>
          <td>
            <html:file property="schemaFile" size="20" style="width:400px" styleId="txtSchemaFile"/>
          </td>
        </tr>
      </c:if>
      <tr>
        <td></td>
        <td>
          <c:if test="${rootElements.xsduPrm}">
            <button type="submit" class="button" value="edit">
              <spring:message code="label.uplSchema.upload"/>"
            </button>
          </c:if>
          <c:if test="${schemaForm.uplSchemaFileName}">
            <c:if test="${rootElements.xsddPrm}">
              <button type="submit" name="action" value="delete">
                <spring:message code="label.schema.deleteFile"/>
              </button>
            </c:if>
            <c:if test="${rootElements.xsduPrm}">
              <c:if test="${rootElements.schemaIdRemoteUrl}">
                <button type="submit" name="action" value="diff">
                  <spring:message code="label.uplSchema.checkupdates"/>
                </button>
              </c:if>
            </c:if>
          </c:if>
          <c:if test="${!schemaForm.uplSchemaFileName}">
            <c:if test="${rootElements.schemaIdRemoteUrl}">
              <button type="submit" name="action" value="diff">
                <spring:message code="label.uplSchema.createcopy"/>
              </button>
            </c:if>
          </c:if>
        </td>
      </tr>
    </table>
  </fieldset>
  <fieldset>
    <legend><spring:message code="label.schema.fldset.rootelems"/></legend>

    <c:if test="${schema.rootElements.rootElemsPresent == true}">
      <table class="datatable" width="80%">
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
        <c:if test="${schema.rootElements.rootElem}">
          <%--id="elem" name="schema.rootElements" property="rootElem"          type="RootElem">--%>
          <c:forEach varStatus="index" items="${rootElements}" var="elem">
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
        </c:if>
        </tbody>
      </table>
    </c:if>
    <c:if test="${user}">
      <c:if test="${rootElements.xsduPrm}">
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
              <button type="submit" class="button" name="action" value="elementAdd">
                <spring:message code="label.element.add"/>
              </button>
            </td>
          </tr>
        </table>
      </c:if>
    </c:if>
  </fieldset>
  <div style="display:none">
    <form:hidden path="schemaId"/>
    <form:hidden path="uplSchemaFileName"/>
    <form:hidden path="uplSchemaId"/>
    <form:hidden path="schema"/>
  </div>
</form:form>
