<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties" %>--%>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="Stylesheets" level="1"/>

<c:if test="${stylesheet.generatedListHolder}">
  <h1 class="documentFirstHeading">
    <spring:message code="label.stylesheet.generated"/>
  </h1>

  <div class="visualClear">&nbsp;</div>

  <div style="width: 97%">
    <table class="datatable" width="100%">
      <col style="width:7%"/>
      <col style="width:10%"/>
      <col style="width:20%"/>
      <col style="width:10%"/>
      <col style="width:10%"/>
      <col style="width:43%"/>
      <thead>
      <tr>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.action"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.table"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.dataset"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.datasetReleased"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.xmlschema"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.stylesheets"/></th>
      </tr>
      </thead>
      <tbody>
      <%--id="schema" name="stylesheet.generatedListHolder" property="ddStylesheets" type="Schema">--%>
      <c:forEach varStatus="index" items="${stylesheet.generatedListHolder.ddStylesheets}">

        <tr class="${index.intValue() % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
          <td align="center">
              <%--paramId="schema" paramName="schema" paramProperty="schema">--%>
            <a href="/old/schema/conversions?schema=${schema.schema}">
              <html:img page="/images/properties.gif" altKey="label.table.stylesheet" title="view stylesheets"/>
            </a>
          </td>
          <td title="<bean:write name="schema" property="table"/>">
            <bean:write name="schema" property="table"/>
          </td>
          <td title="<bean:write name="schema" property="dataset"/>">
            <bean:write name="schema" property="dataset"/>
          </td>
          <td title="<bean:write name="schema" property="datasetReleased"/>">
            <bean:write name="schema" property="datasetReleased" format="${Properties.dateFormatPattern}"/>
          </td>
          <td>
            <a href="<bean:write name="schema" property="schema" />"
               title="<bean:write name="schema" property="schema" />">
              <bean:write name="schema" property="id"/>
            </a>
          </td>
          <td>
        <%--id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">--%>
            <c:forEach items="${schema.stylesheets}">
              <a href="<bean:write name="stylesheet" property="xsl" />"
                 title="<bean:write name="stylesheet" property="description" />">
                <bean:write name="stylesheet" property="description"/>
              </a>&#160;
            </c:forEach>
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td valign="top" colspan="5">
        </td>
      </tr>
      </tbody>
    </table>
  </div>

</c:if>



