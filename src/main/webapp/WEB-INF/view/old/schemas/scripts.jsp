<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Schema QA scripts" level="2"/>

<c:set var="permissions" scope="page" value="${sessionScope['qascript.permissions']}" />

<tiles:insertDefinition name="SchemaTabs">
  <tiles:putAttribute name="selectedTab" value="scripts"/>
</tiles:insertDefinition>

<c:if test="${!empty scripts}">

  <%--<bean:define id="schemaId" name="schemaId" scope="request" type="String"/>--%>
  <%--id="schema" name="schema.qascripts" property="qascripts" type="Schema">--%>
  <c:forEach varStatus="i" items="${scripts.qascripts}" var="schema">
    <%--<bean:define id="schemaUrl" name="schema" property="schema"/>--%>

    <div id="operations">
      <ul>
        <c:if test="${permissions.ssiPrm}">
          <li>
            <a href="/schemas/${schemaId}/scripts/add">
              <spring:message code="label.qascript.add"/>
            </a>
          </li>
        </c:if>
        <li>
            <%--paramId="schemaId" paramName="schema" paramProperty="id"--%>
          <a href="/qaSandbox/run/${schemaId}" titleKey="label.qascript.runservice.title">
            <spring:message code="label.qascript.runservice"/>
          </a>
        </li>
      </ul>
    </div>
    <h1 class="documentFirstHeading">
      <spring:message code="label.schema.qascripts"/>&nbsp;${schema.schema}
    </h1>

  </c:forEach>

  <c:forEach varStatus="i" items="${scripts.qascripts}" var="schema">
    <div class="visualClear">&nbsp;</div>
    <form:form action="/viewQAScriptForm" method="post" modelAttribute="form">
      <table class="formtable">
        <tr>
          <td style="width:510px">
            <label class="question" for="validatefield"><spring:message code="label.qascript.schema.validate"/></label>
          </td>
          <td style="width:40px">
            <c:choose>
              <c:when test="${permissions.ssiPrm}">
                <form:checkbox path="doValidation" id="validatefield"/>
              </c:when>
              <c:otherwise>
                ${schema.doValidation}
              </c:otherwise>
            </c:choose>
          </td>
          <td rowspan="2" style="vertical-align:bottom">
            <c:if test="${permissions.ssiPrm}">
              <!-- save button -->
              <%--onclick="return submitAction(1,'saveSchemaValidation');"/>--%>
              <button type="submit" class="button" value="save">
                  <spring:message code="label.save"/>
              </button>
              <input type="hidden" name="schemaId" value="${schema.id}"/>
              <input type="hidden" name="schema" value="${schema.schema}"/>
            </c:if>
          </td>
        </tr>
        <tr>
          <td>
            <label class="question" for="blockerValidation"><spring:message
                    code="label.qascript.schema.isBlockerValidation"/></label>
          </td>
          <td>
            <c:choose>
              <c:when test="${scripts.ssiPrm}">
                <form:checkbox path="blocker" id="blockerValidation"/>
              </c:when>
              <c:otherwise>
                ${schema.blocker}
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </table>
    </form:form>

    <c:if test="${!empty scripts.qascripts}">
      <form:form action="/searchCR" method="post" modelAttribute="scriptForm">
        <table class="datatable results" width="100%">
          <c:if test="${permissions.ssdPrm}">
            <col style="width:10px"/>
          </c:if>
          <col style="width:10px"/>
          <col/>
          <col/>
          <col/>
          <thead>

          <tr>
            <c:if test="${permissions.ssdPrm}">
              <th scope="col">&#160;</th>
            </c:if>
            <th scope="col">&#160;</th>
            <th scope="col"><spring:message code="label.qascript.shortname"/></th>
            <th scope="col"><spring:message code="label.qascript.description"/></th>
            <th scope="col"><spring:message code="label.qascript.fileName"/></th>
            <th scope="col"><spring:message code="label.lastmodified"/></th>
            <th scope="col"><spring:message code="label.qascript.isActive"/></th>
          </tr>
          </thead>
          <tbody>

          <c:forEach varStatus="i" items="${schema.qascripts}" var="script">
            <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
              <%--<bean:define id="scriptId" name="qascript" property="scriptId"/>--%>
              <c:if test="${permissions.ssdPrm}">
                <td align="center">
                  <form:radiobutton path="scriptId" value="${script.scriptId}"/>
                </td>
              </c:if>
              <td>
                <c:choose>
                  <c:when test="${permissions.qsuPrm}">
                    <%--  If scriptType is NOT 'FME' --%>
                    <c:choose>
                      <c:when test="${script.scriptType != 'fme'}">
                        <a href="/qaSandbox/edit/${scriptId}" titleKey="label.qasandbox.label.qasandbox.editScript">
                          <img src="/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox" />
                        </a>
                      </c:when>
                      <c:otherwise>
                        <spring:message code="label.qascript.runservice.title" var="title" />
                        <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=${schema.id}" title="${title}">
                          <img src="/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox" />
                        </a>
                      </c:otherwise>
                    </c:choose>
                  </c:when>
                  <c:otherwise>
                    <spring:message code="label.qascript.runservice.title" var="title"/>
                    <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=${schema.id}" title="${title}">
                      <img src="/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox" />
                    </a>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <a href="/scripts/${script.scriptId}" title="View QAScript properties">
                  ${script.shortName}
                </a>
              </td>
              <td>
                ${script.description}
              </td>
              <td>
                <%--  If scriptType is 'FME' don't show the link to the local script file --%>
                <c:choose>
                  <c:when test="${script.scriptType == 'fme'}">
                    ${script.fileName}
                  </c:when>
                  <c:otherwise>
                    <a href="${script.filePath}" title="open QA script file">
                      ${script.fileName}
                    </a>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <%--  If scriptType is 'FME' don't show the script Last Modified Date --%>
                <c:if test="${script.scriptType == 'fme'}">
                  <c:choose>
                    <c:when test="${script.modified}">
                      <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                    </c:when>
                    <c:otherwise>
                      ${script.modified}
                    </c:otherwise>
                  </c:choose>
               </c:if>
              </td>
              <td>
                <c:choose>
                  <c:when test="${script.active}">
                    <input type="checkbox" checked="checked" disabled/>
                  </c:when>
                  <c:otherwise>
                    <input type="checkbox" disabled/>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
        <div class="boxbottombuttons">
          <c:if test="${permissions.ssdPrm}">
            <%--onclick="return submitAction(2,'deleteQAScript');"/>--%>
            <button type="button" class="button" value="delete">
              <spring:message code="label.qascript.delete"/>
            </button>
            <input type="hidden" name="schemaId" value="${schemaId}"/>
          </c:if>
          <c:if test="${permissions.ssdPrm}">
            <%--onclick="return submitAction(2,'activateQAScript');"/>--%>
            <button type="button" class="button" value="active">
              <spring:message code="label.qascript.activate"/>
            </button>
            <input type="hidden" name="schemaId" value="${schemaId}"/>
          </c:if>
          <c:if test="${permissions.ssdPrm}">
            <%--onclick="return submitAction(2,'deactivateQAScript');"/>--%>
            <button type="submit" class="button" value="deactivate">
              <spring:message code="label.qascript.deactivate"/>
            </button>
            <input type="hidden" name="schemaId" value="${schemaId}"/>
          </c:if>
        </div>
      </form:form>

    </c:if>
    <c:if test="${empty scripts.qascripts}">
      <div class="advice-msg">
        <spring:message code="label.schema.noQAScripts"/>
      </div>
    </c:if>
  </c:forEach>

  <div class="visualClear">&nbsp;</div>
</c:if>
