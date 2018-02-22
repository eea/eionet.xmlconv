<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Schema stylesheets" level="2"/>

<c:set var="permissions" scope="page" value="${sessionScope['stylesheet.permissions']}" />

<tiles:insertDefinition name="SchemaTabs">
  <tiles:putAttribute name="selectedTab" value="conversions"/>
</tiles:insertDefinition>

<c:if test="${!empty conversions.handCodedStylesheets}">
  <form:form action="/searchCR" method="post">
    <form:errors path="*" cssClass="error-msg" element="div"/>

    <%--<bean:define id="schemaUrl" name="schema" scope="request" type="String"/>--%>
    <%--id="schema" name="schema.stylesheets" property="handCodedStylesheets" type="Schema">--%>
    <c:forEach varStatus="index" items="${conversions.handCodedStylesheets}" var="conversion">
      <c:if test="${conversions.handcoded}">
        <%--<bean:define id="id" name="schema" property="id"/>--%>
        <c:if test="${permissions.ssiPrm}">
          <div id="operations">
            <ul>
              <li>
                <a href="/schemas/${schemaId}/conversions/add">
                  <spring:message code="label.stylesheet.add"/>
                </a>
              </li>
            </ul>
          </div>
        </c:if>
      </c:if>
      <h1 class="documentFirstHeading">
        <spring:message code="label.schema.stylesheets"/>&nbsp;${schemaUrl}
      </h1>

    </c:forEach>

    <%--id="schema" name="schema.stylesheets" property="handCodedStylesheets" type="Schema">--%>
    <c:forEach varStatus="index" items="${conversions.handCodedStylesheets}" var="schema">
      <div class="visualClear">&nbsp;</div>

      <c:if test="${!empty schema.stylesheets}">
        <table class="datatable results" width="100%">
          <c:if test="${permissions.ssdPrm}">
            <col style="width:10px"/>
          </c:if>
          <col style="width:10px"/>
          <col/>
          <col/>
          <col/>
          <col/>
          <thead>
          <tr>
            <c:if test="${permissions.ssdPrm}">
              <th scope="col">&#160;</th>
            </c:if>
            <th scope="col">&#160;</th>
            <th scope="col"><spring:message code="label.table.stylesheet.type"/></th>
            <th scope="col"><spring:message code="label.table.stylesheet.description"/></th>
            <th scope="col"><spring:message code="label.table.stylesheet.stylesheet"/></th>
            <th scope="col"><spring:message code="label.table.stylesheet.modified"/></th>
          </tr>
          </thead>
          <tbody>
          <%--id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">--%>
          <c:forEach varStatus="i" items="${schema.stylesheets}" var="stylesheet">
            <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
              <%--<bean:define id="convId" name="stylesheet" property="convId"/>--%>
              <c:if test="${permissions.ssdPrm}">
                <td align="center">
                  <input type="radio" name="conversionId" value="${stylesheet.convId}"/>
                </td>
              </c:if>
              <td>
                <a href="searchCR?conversionId=${stylesheet.convId}&amp;schemaUrl=${stylesheet.convId}">
                  <img src="/images/execute.gif" alt="Run" title="Run conversion" /></a>
              </td>
              <td align="center">
                <c:if test="${!stylesheet.ddConv}">
                  <a href="/conversions/${stylesheet.convId}" title="View conversion properties">
                    ${stylesheet.type}
                  </a>
                </c:if>
                <c:if test="${stylesheet.ddConv}">
                  ${stylesheet.type}
                </c:if>
              </td>
              <td>
                ${stylesheet.description}
              </td>
              <td>
                <c:choose>
                  <c:when test="${!stylesheet.ddConv}">
                    <a href="/${stylesheet.xsl}" class="link-xsl">
                      ${stylesheet.xslFileName}
                    </a>&#160;
                  </c:when>
                  <c:otherwise>
                    <a href="${stylesheet.xsl}" class="link-xsl">
                      ${stylesheet.xslFileName}
                    </a>&#160;
                  </c:otherwise>
                </c:choose>
              </td>
              <td align="center">
                <c:if test="${stylesheet.ddConv}">
                  Generated
                </c:if>
                <c:if test="${!stylesheet.ddConv}">
                  <c:choose>
                    <c:when test="${empty stylesheet.modified}">
                      <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                    </c:when>
                    <c:otherwise>
                      ${stylesheet.modified}
                    </c:otherwise>
                  </c:choose>
                </c:if>
              </td>
            </tr>
          </c:forEach>
          <tr>
            <td valign="top" colspan="7">
            </td>
          </tr>
          </tbody>
        </table>
        <div class="boxbottombuttons">
          <c:if test="${permissions.ssdPrm}">
            <button type="submit" class="button" value="delete">
              <spring:message code="label.stylesheet.delete"/>
            </button>
          </c:if>
          <input type="hidden" name="schemaUrl" value="${schemaUrl}"/>
        </div>
      </c:if>
      <c:if test="${empty schema.stylesheets}">
        <div class="advice-msg">
          <spring:message code="label.schema.noStylesheets"/>
        </div>
      </c:if>
    </c:forEach>

    <div class="visualClear">&nbsp;</div>
  </form:form>
</c:if>