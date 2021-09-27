<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
    <div id="tabbedmenu">
        <ul>
            <li>
                <a href="/scripts/${scriptId}" title="label.qascript.tab.title" style="color: black; text-decoration: none;">
                    <spring:message code="label.qascript.tab.title"/>
                </a>
            </li>
            <li>
                <a href="/scripts/${scriptId}/history" style="color: black; text-decoration: none;">
                    <spring:message code="label.qascript.history"/>
                </a>
            </li>
            <li id="currenttab"><span style="color: black; text-decoration: none;"
                                      title='<spring:message code="label.qascript.executionHistory"/>'><spring:message
                    code="label.qascript.executionHistory"/></span>
            </li>
        </ul>
    </div>

    <ed:breadcrumbs-push label="QA Script Execution History" level="3"/>
    <h1 class="documentFirstHeading">
        <spring:message code="label.qascript.executionHistory.title"/>
    </h1>

    <div class="visualClear">&nbsp;</div>

    <c:choose>
        <c:when test="${!empty history}">
            <p>Average duration for script with id ${scriptId} is <c:out value = "${averageDuration}"/>. The script has been executed <c:out value = "${numberOfExecutions}"/> times.</p>
            <div style="width: 97%">
                <table class="datatable results" width="100%">
                    <col style="width:30%"/>
                    <col style="width:10%"/>
                    <col style="width:30%"/>
                    <col style="width:10%"/>
                    <col style="width:10%"/>
                    <col style="width:10%"/>
                    <thead>
                    <tr>
                        <th scope="col" class="scope-col"><spring:message code="label.table.executionHistory.filename"/></th>
                        <th scope="col" class="scope-col"><spring:message code="label.table.executionHistory.type"/></th>
                        <th scope="col" class="scope-col"><spring:message code="label.table.executionHistory.duration"/></th>
                        <th scope="col" class="scope-col"><spring:message code="label.table.executionHistory.heavy"/></th>
                        <th scope="col" class="scope-col"><spring:message code="label.table.executionHistory.status"/></th>
                        <th scope="col" class="scope-col"><spring:message code="label.table.executionHistory.version"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach varStatus="i" items="${history}" var="entry">
                        <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
                            <td>
                                    ${entry.shortFileName}
                            </td>
                            <td>
                                    ${entry.scriptType}
                            </td>
                            <td>
                                    ${entry.durationFormatted}
                            </td>
                            <td>
                                    ${entry.markedHeavy}
                            </td>
                            <td>
                                    ${entry.jobStatus}
                            </td>
                            <td>
                                    ${entry.version}
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
            <div>There is no execution history info for script with id ${scriptId}.</div>
        </c:otherwise>
    </c:choose>
</div>
