<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties" %>--%>
<%--<%@ page import="eionet.gdem.utils.Utils,java.util.Date" %>--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%--<html:xhtml/>--%>
<div style="width:100%;">

  <ed:breadcrumbs-push label="Validate XML" level="1"/>
  <h1><spring:message code="label.conversion.validate.title"/></h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>
  <c:if present name="conversion.valid" scope="request">
    <bean:size id="countErrors" name="conversion.valid"/>

    <c:if equal name="countErrors" value="0">
      <c:if notEmpty name="conversion.originalSchema">
        <div class="ok-msg">The file is valid XML (<%=Utils.getDateTime(new Date())%>)
          <p><spring:message code="label.conversion.originalSchema"/>&#160; <a
                  href="<bean:write name="conversion.originalSchema"/>"><bean:write
                  name="conversion.originalSchema"/></a></p>
          <c:if present name="conversion.validatedSchema">
            <p><spring:message code="label.conversion.validatedSchema"/>&#160;
              <a href="<bean:write name="conversion.validatedSchema"/>"><bean:write
                      name="conversion.validatedSchema"/></a></p>
          </c:if present></div>
      </c:if notEmpty>
      <c:if empty name="conversion.originalSchema">
        <div class="error-msg">Could not validate XML.
          <p><spring:message code="label.conversion.schema.not.found"/></p>
        </div>
      </c:if empty>
    </c:if equal>
    <c:if notEqual name="countErrors" value="0">
      <div class="error-msg">The file is not valid XML <c:if notEmpty
              name="conversion.originalSchema">
        <p><spring:message code="label.conversion.originalSchema"/>&#160; <a
                href="<bean:write name="conversion.originalSchema"/>"><bean:write
                name="conversion.originalSchema"/></a></p>
        <c:if present name="conversion.validatedSchema">
          <p><spring:message code="label.conversion.validatedSchema"/>&#160;
            <a href="<bean:write name="conversion.validatedSchema"/>"><bean:write
                    name="conversion.validatedSchema"/></a></p>
        </c:if present>
      </c:if notEmpty> <c:if empty name="conversion.originalSchema">
        <p><spring:message code="label.conversion.schema.not.found"/></p>
      </c:if empty></div>
    </c:if notEqual>
    <c:if notEmpty name="conversion.warningMessage">
      <div class="error-msg">
        <bean:write name="conversion.warningMessage"/>
      </div>
    </c:if notEmpty>
  </c:if present>


  <form:form action="/old/validation" method="post" modelAttribute="form">
    <table class="datatable">
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.url"/>
        </th>
      </tr>
      <tr>
        <td>
          <form:input path="xmlUrl" type="text" style="width: 40em;"/>
        </td>
      </tr>
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.xmlSchema.optional"/>
        </th>
      </tr>
      <tr>
        <td>
          <spring:message code="label.conversion.validate.note"/>
        </td>
      </tr>
      <tr>
        <td>
          <input type="text" property="schemaUrl" style="width: 40em;"/>
        </td>
      </tr>
        <%--<c:if equal name="form" property="showSchemaSelection" value="true">--%>
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.xmlSchema"/>
        </th>
      </tr>
      <tr>
        <td>
          <spring:message code="label.conversion.selectSchema"/>
        </td>
      </tr>
      <tr>
        <td>

            <%--                    <form:select path="showSchemaSelection" name="showSchemaSelection" property="schemaUrl"  size="10">
                                    <form:option value="">--</form:option>
                                    <form:options collection="conversion.schemas" property="schema" labelProperty="label" />
                                </form:select>--%>
        </td>
      </tr>
        <%--</c:if equal>--%>
      <tr>
        <td align="center">
          <input type="submit" styleClass="button">
          <spring:message code="label.conversion.validate"/>
          </input>
        </td>
      </tr>
    </table>
  </form:form>
  <c:if present name="conversion.valid" scope="request">
    <bean:size id="countErrors" name="conversion.valid"/>
    <c:if notEqual name="countErrors" value="0">
      <table class="datatable" align="center" width="100%">
        <col style="width:8%"/>
        <col style="width:8%"/>
        <col style="width:8%"/>
        <col/>
        <thead>
        <tr>
          <th scope="col"><span title="Error"><spring:message code="label.table.conversion.type"/></span></th>
          <th scope="col"><span title="PositionLine"><spring:message code="label.table.conversion.line"/></span></th>
          <th scope="col"><span title="PositionCol"><spring:message code="label.table.conversion.col"/></span></th>
          <th scope="col"><span title="Message"><spring:message code="label.table.conversion.message"/></span></th>
        </tr>
        </thead>
        <tbody>
        <c:if iterate indexId="index" id="valid" name="conversion.valid" scope="request" type="ValidateDto">
          <tr <%=(index.intValue() % 2 == 1) ? "class=\"zebraeven\""
                  : "class=\"zebraodd\""%>>
            <td>
              <bean:write name="valid" property="type"/>
            </td>
            <td>
              <bean:write name="valid" property="line"/>
            </td>
            <td>
              <bean:write name="valid" property="column"/>
            </td>
            <td>
              <bean:write name="valid" property="description"/>
            </td>
          </tr>
        </c:if iterate>
        </tbody>
      </table>
    </c:if notEqual>
  </c:if present>
</div>
