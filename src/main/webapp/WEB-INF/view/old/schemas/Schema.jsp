<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--<html:xhtml/>--%>

<bean:define id="schemaURL" name="schemaForm" property="schema"/>
<bean:define id="id" name="schemaForm" property="schemaId"/>

<div id="tabbedmenu">

  <ul>
    <li id="currenttab">
    <span style="color: black; text-decoration: none;"
          title='<spring:message code="label.tab.title.schema"/>'><bean:message
            key="label.tab.title.schema"/></span>
    </li>
    <li>
      <html:link page="/old/conversions?schema=${schemaURL}" titleKey="label.tab.title.xsl"
                 onclick="return submitTab(this);" style="color: black; text-decoration: none;">
        <spring:message code="label.tab.title.xsl"/>
      </html:link>
    </li>
    <li>
      <html:link page="/old/qaScripts?schemaId=${id}" titleKey="label.tab.title.scripts"
                 onclick="return submitTab(this);" style="color: black; text-decoration: none;">
        <spring:message code="label.tab.title.scripts"/>
      </html:link>
    </li>
  </ul>
</div>

<ed:breadcrumbs-push label="Edit XML Schema or DTD" level="2"/>


<h1><spring:message code="label.schema.edit"/></h1>

<%-- include Error display --%>
<tiles:insertDefinition name="Error"/>

<form:form action="/schemaUpdate" method="post" enctype="multipart/form-data">
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
            <html:text property="schema" maxlength="255" style="width:500px" styleId="txtSchemaUrl"/>
          </c:when>
          <c:otherwise>
            <a href="<bean:write name="schemaForm" property="schema" />"
               title="<bean:write name="schemaForm" property="schema" />">
              <bean:write name="schemaForm" property="schema"/>
            </a>&#160;
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
              <html:textarea property="description" rows="2" cols="30" style="width:500px"
                             styleId="txtDescription"/>
            </c:when>
            <c:otherwise>
              <bean:write name="schemaForm" property="description"/>
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
              <html:select property="schemaLang" styleId="txtSchemaLang">
                <html:options property="schemaLanguages"/>
              </html:select>
            </c:when>
            <c:otherwise>
              <bean:write name="schemaForm" property="schemaLang"/>
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
              <html:checkbox property="doValidation" styleId="txtValidation"/>
            </c:when>
            <c:otherwise>
              <bean:write name="schemaForm" property="doValidation"/>
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
              <html:checkbox property="blocker" styleId="txtBlockerValidation"/>
            </c:when>
            <c:otherwise>
              <bean:write name="schemaForm" property="blocker"/>
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
          <html:text property="expireDate" styleId="txtExpireDate"/> (dd/MM/yyyy)
        </td>
      </tr>
      <c:if test="${schemaForm == 'dtd'}">
        <tr>
          <td>
            <label class="question" for="txtDtdId">
              <spring:message code="label.elem.dtdid"/>
            </label>
          </td>
          <td align="left">
            <c:choose>
              <c:when test="${user}">
                <html:text property="dtdId" maxlength="50" size="50" styleId="txtDtdId"/>
              </c:when>
              <c:otherwise>
                <bean:write name="schemaForm" property="dtdId"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:if>
      <tr>
        <td></td>
        <td>
          <c:if test="${schema.rootElements == 'xsduPrm'}">
            <input type="button" class="button" value="<spring:message code="label.schema.save"/>"
                   onclick="return submitAction(1,'schemaUpdate');"/>
            &nbsp;
          </c:if>
          <c:if test="${schema.rootElements == 'xsddPrm'}">
            <input type="button" class="button" value="<spring:message code="label.schema.delete"/>"
                   onclick="return submitAction(1,'deleteUplSchema?deleteSchema=true');"/>
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
            <a href="<bean:write name="schemaForm" property="uplSchemaFileUrl" />"
               title="<bean:write name="schemaForm" property="uplSchemaFileUrl" />">
              <bean:write name="schemaForm" property="uplSchemaFileName"/>
            </a>&#160;
            <c:if test="${schemaForm.lastModified}">
              &#160;&#160;(<spring:message code="label.lastmodified"/>: <bean:write property="lastModified"
                                                                                    name="schemaForm"/>)
            </c:if>
          </c:if>
        </td>
      </tr>
      <c:if test="${schema.rootElements == 'xsduPrm'}">
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
          <c:if test="${schema.rootElements == 'xsduPrm'}">
            <input type="button" class="button" value="<spring:message code="label.uplSchema.upload"/>"
                   onclick="return submitAction(1,'editUplSchema');"/>
          </c:if>
          <c:if test="${schemaForm.uplSchemaFileName}">
            <c:if test="${schema.rootElements == 'xsddPrm'}">
              <input type="button" class="button" value="<spring:message code="label.schema.deleteFile"/>"
                     onclick="return submitAction(1,'deleteUplSchema');"/>
            </c:if>
            <c:if test="${schema.rootElements == 'xsduPrm'}">
              <c:if test="${schema.rootElements.schemaIdRemoteUrl}">
                <input type="button" class="button"
                       value="<spring:message code="label.uplSchema.checkupdates"/>"
                       onclick="return submitAction(1,'diffUplSchemas');"/>
              </c:if>
            </c:if>
          </c:if>
          <c:if test="${!schemaForm.uplSchemaFileName}">
            <c:if test="${schema.rootElements == schemaIdRemoteUrl}">
              <input type="button" class="button" value="<spring:message code="label.uplSchema.createcopy"/>"
                     onclick="return submitAction(1,'diffUplSchemas');"/>
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
          <c:if test="${schema.rootElements == 'xsduPrm'}">
            <th scope="col"></th>
          </c:if>
        </tr>
        </thead>
        <tbody>
        <c:if test="${schema.rootElements.rootElem}">
          <%--id="elem" name="schema.rootElements" property="rootElem"          type="RootElem">--%>
          <c:forEach varStatus="index" items="${schema.rootElements.rootElem}">
            <tr class="${i.index % 2 == 1 ? 'zebraeven' : ''}">
              <td>
                <bean:write name="elem" property="name"/>
              </td>
              <td>
                <bean:write name="elem" property="namespace"/>
              </td>
              <c:if test="${schema.rootElements == 'xsduPrm'}">
                <td align="center">
                  <a href="deleteElem?elemId=<bean:write name="elem" property="elemId" />"
                     onclick='return elementDelete("<bean:write name="elem" property="name"/>");'>
                    <html:img page="/images/delete.gif" altKey="label.delete"
                              title="delete root element"/>
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
      <c:if test="${schema.rootElements == 'xsduPrm'}">
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
              <html:text property="elemName" maxlength="255" style="width:250px" styleId="txtElemName"/>
            </td>
          </tr>
          <tr>
            <td>
              <label class="question" for="txtNamespace">
                <spring:message code="label.schema.table.namespace"/>
              </label>
            </td>
            <td>
              <html:text property="namespace" maxlength="255" style="width:250px" styleId="txtNamespace"/>
            </td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td>
              <input type="button" class="button" style="width:50px"
                     value="<spring:message code="label.element.add"/>"
                     onclick="return submitAction(1,'elementAdd');"/>
            </td>
          </tr>
        </table>
      </c:if>
    </c:if>
  </fieldset>
  <div style="display:none">
    <html:hidden property="schemaId"/>
    <html:hidden property="uplSchemaFileName"/>
    <html:hidden property="uplSchemaId"/>
    <html:hidden property="schema"/>
  </div>
</form:form>
