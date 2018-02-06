<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li>
        <a href="/scripts/${scriptId}" titleKey="label.qascript.tab.title" style="color: black; text-decoration: none;">
          <spring:message code="label.qascript.tab.title"/>
        </a>
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

  <div class="visualClear">&nbsp;</div>

  <c:choose>
    <c:when test="${!empty history}">
      <div style="width: 97%">
        <table class="datatable results" width="100%">
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
          <c:forEach varStatus="i" items="${history}" var="backup">
            <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
              <td align="center">
                <a href="${Constants.QUERY_FOLDER}/${Constants.BACKUP_FOLDER_NAME}/${backup.fileName}"
                   title="${backup.fileName}">
                  ${backup.fileName}
                </a>
              </td>
              <td>
                ${backup.timestamp}
              </td>
              <td>
                ${backup.user}
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
    </c:when>
    <c:otherwise>
      <div>There is no history for script with id ${scriptId}.</div>
    </c:otherwise>
  </c:choose>
</div>
