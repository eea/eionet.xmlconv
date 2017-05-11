<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="Schema stylesheets" level="2"/>

<c:if test="${schema.stylesheets}">
  <form:form action="/searchCR" method="post">

    <bean:define id="schemaUrl" name="schema" scope="request" type="String"/>
    <%--id="schema" name="schema.stylesheets" property="handCodedStylesheets" type="Schema">--%>
    <c:forEach varStatus="index" items="${schema.stylesheets.handCodedStylesheets}">
      <c:if test="${schema.stylesheets == 'handcoded'}">
        <bean:define id="id" name="schema" property="id"/>
        <div id="tabbedmenu">
          <ul>
            <li>
              <html:link page="/old/schemas/${id}" titleKey="label.tab.title.schema" onclick="return submitTab(this);"
                         style="color: black; text-decoration: none;">
                <spring:message code="label.tab.title.schema"/>
              </html:link>
            </li>
            <li id="currenttab">
              <span style="color: black; text-decoration: none;"
                    title='<spring:message code="label.tab.title.xsl"/>'><spring:message
                      code="label.tab.title.xsl"/></span>
            </li>
            <li>
              <html:link page="/old/schemas/${id}/qaScripts" titleKey="label.tab.title.scripts"
                         onclick="return submitTab(this);" style="color: black; text-decoration: none;">
                <spring:message code="label.tab.title.scripts"/>
              </html:link>
            </li>
          </ul>
        </div>
        <c:if test="${stylesheet.permissions =='ssiPrm'}">
          <div id="operations">
            <ul>
              <li><a href="addStylesheetForm?schema=<bean:write name="schema" property="schema" />"><spring:message
                      code="label.stylesheet.add"/></a></li>
            </ul>
          </div>
        </c:if>
      </c:if>
      <h1 class="documentFirstHeading">
        <spring:message code="label.schema.stylesheets"/>&nbsp;<bean:write name="schema" property="schema"/>
      </h1>

    </c:forEach>

    <%-- include Error display --%>
    <tiles:insertDefinition name="Error"/>
    <%--id="schema" name="schema.stylesheets" property="handCodedStylesheets" type="Schema">--%>
    <c:forEach varStatus="index" items="schema.stylesheets.handCodedStylesheets">
      <div class="visualClear">&nbsp;</div>

      <c:if test="${schema.stylesheets}">
        <table class="datatable" width="100%">
          <c:if test="${stylesheet.permissions == 'ssdPrm'}">
            <col style="width:10px"/>
          </c:if>
          <col style="width:10px"/>
          <col/>
          <col/>
          <col/>
          <col/>
          <thead>
          <tr>
            <c:if test="${stylesheet.permissions == 'ssdPrm'}">
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
          <c:forEach varStatus="index" items="${schema.stylesheets}">
            <tr class="${index.intValue() % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
              <bean:define id="convId" name="stylesheet" property="convId"/>
              <c:if test="${stylesheet.permissions == 'ssdPrm'}">
                <td align="center">
                  <input type="radio" name="conversionId" value="${convId}"/>
                </td>
              </c:if>
              <td>
                <a href="searchCR?conversionId=${convId}&amp;schemaUrl=${schemaUrl}"><img
                        src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run"
                        title="Run conversion"></img></a>
              </td>
              <td align="center">
                <c:if test="${stylesheet.ddConv}">
                  <a href="stylesheetViewForm?stylesheetId=<bean:write name="stylesheet" property="convId" />"
                     title="view stylehseet properties">
                    <bean:write name="stylesheet" property="type"/>
                  </a>
                </c:if>
                <c:if test="${stylesheet == 'ddConv'}">
                  <bean:write name="stylesheet" property="type"/>
                </c:if>
              </td>
              <td>
                <bean:write name="stylesheet" property="description"/>
              </td>
              <td>
                <c:choose>
                  <c:when test="${stylesheet.ddconv}">
                    <a href="<bean:write name="webRoot"/>/<bean:write name="stylesheet" property="xsl" />"
                       class="link-xsl">
                      <bean:write name="stylesheet" property="xslFileName"/>
                    </a>&#160;
                  </c:when>
                  <c:otherwise>
                    <a href="<bean:write name="stylesheet" property="xsl" />" class="link-xsl">
                      <bean:write name="stylesheet" property="xslFileName"/>
                    </a>&#160;
                  </c:otherwise>
                </c:choose>
              </td>
              <td align="center">
                <c:if test="${stylesheet.ddConv}">
                  Generated
                </c:if>
                <c:if test="${stylesheet.ddConv}">
                  <c:choose>
                    <c:when test="${stylesheet.modified}">
                      <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                    </c:when>
                    <c:otherwise>
                      <bean:write name="stylesheet" property="modified"/>
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
          <c:if test="${stylesheet.permissions =='ssdPrm'}">
            <input type="button" class="button" value="<spring:message code="label.stylesheet.delete"/>"
                   onclick="return submitAction(1,'deleteStylesheet');"/>
          </c:if>

          <input type="hidden" name="schemaUrl" value="${schemaUrl}"/>
        </div>
      </c:if>
      <c:if test="${!schema.stylesheets}">
        <div class="advice-msg">
          <spring:message code="label.schema.noStylesheets"/>
        </div>
      </c:if>
    </c:forEach>

    <div class="visualClear">&nbsp;</div>
  </form:form>
</c:if>



