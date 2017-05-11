<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>--%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--<%@page import="eionet.gdem.Constants" %>--%>

<%--<html:xhtml/>--%>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li>
        <%--paramId="scriptId" paramName="script_id"--%>
        <html:link page="/old/qaScripts/${scriptId}" titleKey="label.qascript.tab.title"
                   onclick="return submitTab(this);" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.tab.title"/>
        </html:link>
      </li>
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<spring:message code="label.qascript.history"/>'><spring:message
              code="label.qascript.history"/></span></li>
    </ul>
  </div>

  <ed:breadcrumbs-push label="QA Script History" level="3"/>


  <h1 class="documentFirstHeading">
    <spring:message code="label.qascriptHistory.title"/>
  </h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <div class="visualClear">&nbsp;</div>

  <c:if test="${qascript.history}">
    <div style="width: 97%">
      <table class="datatable" width="100%">
        <col style="width:10%"/>
        <col style="width:52%"/>
        <col style="width:38%"/>
        <thead>
        <tr>
          <th scope="col" class="scope-col"><spring:message code="label.table.backup.filename"/></th>
          <th scope="col" class="scope-col"><spring:message code="label.table.backup.timestamp"/></th>
          <th scope="col" class="scope-col"><spring:message code="label.table.backup.user"/></th>
        </tr>
        </thead>
        <tbody>
        <%--id="backup" name="qascript.history" type="BackupDto">--%>
        <c:forEach varStatus="index" items="${qascript.history}">
          <tr class="${index.intValue() % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
            <td align="center">
              <a href="<bean:write name="webRoot"/>/${Constants.QUERY_FOLDER}/${Constants.BACKUP_FOLDER_NAME}/<bean:write name="backup" property="fileName" />"
                 title="<bean:write name="backup" property="fileName" />">
                <bean:write name="backup" property="fileName"/>
              </a>
            </td>
            <td>
              <bean:write name="backup" property="timestamp"/>
            </td>
            <td>
              <bean:write name="backup" property="user"/>
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

</div>
