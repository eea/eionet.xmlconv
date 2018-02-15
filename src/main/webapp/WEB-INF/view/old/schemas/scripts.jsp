<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Schema QA scripts" level="2"/>

<c:set var="permissions" scope="page" value="${sessionScope['qascript.permissions']}"/>

<tiles:insertDefinition name="SchemaTabs">
  <tiles:putAttribute name="selectedTab" value="scripts"/>
</tiles:insertDefinition>

<c:if test="${permissions.ssiPrm}">
  <div id="operations">
    <ul>
      <li><a href="/schemas/${schemaId}/scripts/add"><spring:message code="label.qascript.add"/></a></li>
      <li><a href="/qaSandbox/run/${schemaId}" title="label.qascript.runservice.title"><spring:message code="label.qascript.runservice"/></a></li>
    </ul>
  </div>
</c:if>

<%--paramId="schemaId" paramName="schema" paramProperty="id"--%>


<c:if test="${!empty scripts}">

  <%--<bean:define id="schemaId" name="schemaId" scope="request" type="String"/>--%>
  <%--id="schema" name="schema.qascripts" property="qascripts" type="Schema">--%>
  <c:forEach varStatus="i" items="${scripts.qascripts}" var="schema">
    <%--<bean:define id="schemaUrl" name="schema" property="schema"/>--%>


    <%--<h1 class="documentFirstHeading">--%>

    <%--</h1>--%>

  </c:forEach>

  <c:forEach varStatus="i" items="${scripts.qascripts}" var="schema">
    <div class="visualClear">&nbsp;</div>
    <form:form action="/schemas" method="post" modelAttribute="schemaForm">
      <fieldset class="fieldset">
        <legend><spring:message code="label.schema.qascripts"/>&nbsp;${schema.schema}</legend>
        <div class="row">
          <div class="columns small-4">
            <label class="question" for="validatefield"><spring:message code="label.qascript.schema.validate"/></label>
          </div>
          <div class="columns small-6">
            <c:choose>
              <c:when test="${permissions.ssiPrm}">
                <form:checkbox path="doValidation" id="validatefield"/>
              </c:when>
              <c:otherwise>
                ${schema.doValidation}
              </c:otherwise>
            </c:choose>
          </div>
          <div class="columns small-2">
            <c:if test="${permissions.ssiPrm}">
              <button type="submit" class="button" name="save" value="save">
                <spring:message code="label.save"/>
              </button>
              <input type="hidden" name="schemaId" value="${schema.id}"/>
              <input type="hidden" name="schema" value="${schema.schema}"/>
            </c:if>
          </div>
        </div>
        <div class="row">
          <div class="columns small-4">
            <label class="question" for="blockerValidation">
              <spring:message code="label.qascript.schema.isBlockerValidation"/>
            </label>
          </div>
          <div class="columns small-8">
            <c:choose>
              <c:when test="${scripts.ssiPrm}">
                <form:checkbox path="blocker" id="blockerValidation"/>
              </c:when>
              <c:otherwise>
                ${schema.blocker}
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </fieldset>
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
            <c:set var="scriptId" value="${script.scriptId}"/>
            <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
              <c:if test="${permissions.ssdPrm}">
                <td align="center">
                  <form:radiobutton path="scriptId" value="${script.scriptId}"/>
                </td>
              </c:if>
              <td>
                <c:choose>
                  <c:when test="${permissions.qsuPrm}">
                    <c:choose>
                      <c:when test="${script.scriptType != 'fme'}">
                        <a href="/qaSandbox/edit/${scriptId}" title="label.qasandbox.label.qasandbox.editScript">
                          <img src="/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox"/>
                        </a>
                      </c:when>
                      <c:otherwise>
                        <spring:message code="label.qascript.runservice.title" var="title"/>
                        <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=${schema.id}"
                           title="${title}">
                          <img src="/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox"/>
                        </a>
                      </c:otherwise>
                    </c:choose>
                  </c:when>
                  <c:otherwise>
                    <spring:message code="label.qascript.runservice.title" var="title"/>
                    <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=${schema.id}" title="${title}">
                      <img src="/images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox"/>
                    </a>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <a href="/scripts/${script.scriptId}" title="View QAScript properties">${script.shortName}</a>
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
                    <a href="/${script.filePath}" title="open QA script file">
                        ${script.fileName}
                    </a>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${empty script.modified}">
                    <c:if test="${script.scriptType != 'fme'}">
                      <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                    </c:if>
                  </c:when>
                  <c:otherwise>
                    ${script.modified}
                  </c:otherwise>
                </c:choose>
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
        <%--<div class="boxbottombuttons">--%>
          <%----%>
        <%--</div>--%>
        <c:if test="${permissions.ssdPrm}">
          <button type="button" class="button" name="delete">
            <spring:message code="label.qascript.delete"/>
          </button>
          <input type="hidden" name="schemaId" value="${schemaId}"/>
        </c:if>
        <c:if test="${permissions.ssdPrm}">
          <button type="button" class="button" name="activate">
            <spring:message code="label.qascript.activate"/>
          </button>
          <input type="hidden" name="schemaId" value="${schemaId}"/>
        </c:if>
        <c:if test="${permissions.ssdPrm}">
          <button type="submit" class="button" name="deactivate">
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
