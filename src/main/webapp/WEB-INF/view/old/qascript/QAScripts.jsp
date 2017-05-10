<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="All QA Scripts" level="1"/>

<c:if test="${qascript.permissions == 'ssiPrm'}">
  <div id="operations">
    <ul>
      <li>
        <html:link page="/old/qaScripts/add"><spring:message code="label.qascript.add"/></html:link>
      </li>
    </ul>
  </div>
</c:if>

<h1 class="documentFirstHeading">
  <spring:message code="label.qascript.title"/>
</h1>

<%-- include Error display --%>
<tiles:insertDefinition name="Error"/>

<div class="visualClear">&nbsp;</div>

<c:if test="${qascript.qascriptList == 'qascripts'}">
  <div style="width: 97%">
    <table class="datatable" width="100%">
      <col/>
      <col/>
      <thead>
      <tr>
        <th scope="col" class="scope-col"><spring:message code="label.table.qascript.xmlschema"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.qascript.qascripts"/></th>
      </tr>
      </thead>
      <tbody>
  <%--property="qascripts" type="Schema">--%>
      <c:forEach varStatus="index" items="qascript.qascriptList.qascripts">
        <tr class="${index.intValue() % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
          <td title="<bean:write name="schema" property="schema"/>">
            <html:link page="/old/schemas/${schema.id}/qaScripts" title="view QA scripts for this XML Schema">
              <bean:write name="schema" property="schema"/>
            </html:link>
          </td>
          <td>
            <c:if test="${schema == 'qascripts'}">
              <%--id="qascript" name="schema" scope="page" property="qascripts" type="QAScript">--%>
              <c:forEach items="schema.qascripts">
                <html:link page="/old/qaScripts/${schema.id}" titleKey="label.qascript.tab.title">
                  <bean:write name="qascript" property="shortName"/>
                </html:link>
                &#160;
              </c:forEach>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td valign="top" colspan="3">
        </td>
      </tr>
      </tbody>
    </table>
  </div>

  <div class="visualClear">&nbsp;</div>

</c:if>



