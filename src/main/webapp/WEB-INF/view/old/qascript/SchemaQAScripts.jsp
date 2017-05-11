<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>


<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="Schema QA scripts" level="2"/>


<c:if test="${schema.qascripts}">

  <bean:define id="schemaId" name="schemaId" scope="request" type="String"/>
  <%--id="schema" name="schema.qascripts" property="qascripts" type="Schema">--%>
  <c:forEach varStatus="index" items="${schema.qascripts.qascript}">
    <bean:define id="schemaUrl" name="schema" property="schema"/>
    <div id="tabbedmenu">
      <ul>
        <li>
          <html:link page="/old/schemas/${schemaId}" titleKey="label.tab.title.schema" onclick="return submitTab(this);"
                     style="color: black; text-decoration: none;">
            <spring:message code="label.tab.title.schema"/>
          </html:link>
        </li>
        <li>
          <html:link page="/old/schemas/schemaStylesheets?schema=${schemaUrl}" titleKey="label.tab.title.xsl"
                     onclick="return submitTab(this);" style="color: black; text-decoration: none;">
            <spring:message code="label.tab.title.xsl"/>
          </html:link>
        </li>
        <li id="currenttab">
          <span style="color: black; text-decoration: none;"
                title='<spring:message code="label.tab.title.scripts"/>'><spring:message
                  code="label.tab.title.scripts"/></span>
        </li>
      </ul>
    </div>
    <div id="operations">
      <ul>
        <c:if test="${qascript.permissions == 'ssiPrm'}">
          <li>
            <a href="addQAScriptForm?schemaId=<bean:write name="schema" property="id" />&amp;schema=<bean:write name="schema" property="schema" />">
              <spring:message code="label.qascript.add"/>
            </a>
          </li>
        </c:if>
        <li>
            <%--paramId="schemaId" paramName="schema" paramProperty="id"--%>
          <html:link page="/old/qaSandbox/run/${schemaId}" titleKey="label.qascript.runservice.title">
            <spring:message code="label.qascript.runservice"/>
          </html:link>
        </li>
      </ul>
    </div>
    <h1 class="documentFirstHeading">
      <spring:message code="label.schema.qascripts"/>&nbsp;<bean:write name="schema" property="schema"/>
    </h1>

  </c:forEach>
  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <%--id="schema" name="schema.qascripts" property="qascripts" type="Schema">--%>
  <c:forEach varStatus="index" items="${schema.qascripts.qascripts}">
    <div class="visualClear">&nbsp;</div>
    <form:form action="/viewQAScriptForm" method="post">
      <table class="formtable">
        <tr>
          <td style="width:510px">
            <label class="question" for="validatefield"><spring:message code="label.qascript.schema.validate"/></label>
          </td>
          <td style="width:40px">
            <c:choose>
              <c:when test="${qascript.permissions == 'ssiPrm'}">
                <html:checkbox name="schema" property="doValidation" styleId="validatefield"/>
              </c:when>
              <c:otherwise>
                <bean:write name="schema" property="doValidation"/>
              </c:otherwise>
            </c:choose>
          </td>
          <td rowspan="2" style="vertical-align:bottom">
            <c:if test="${qascript.permissions == 'ssiPrm'}">
              <!-- save button -->
              <input type="button" class="button" value="<spring:message code="label.save"/>"
                     onclick="return submitAction(1,'saveSchemaValidation');"/>
              <input type="hidden" name="schemaId" value="${schemaId}"/>
              <input type="hidden" name="schema" value="${schemaUrl}"/>
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
              <c:when test="${qascript.permissions == 'ssiPrm'}">
                <html:checkbox name="schema" property="blocker" styleId="blockerValidation"/>
              </c:when>
              <c:otherwise>
                <bean:write name="schema" property="blocker"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </table>
    </form:form>

    <c:if test="${schema.qascripts}">
      <form:form action="/searchCR" method="post">
        <table class="datatable" width="100%">
          <c:if test="${qascript.permissions == 'ssdPrm'}">
            <col style="width:10px"/>
          </c:if>
          <col style="width:10px"/>
          <col/>
          <col/>
          <col/>
          <thead>

          <tr>
            <c:if test="${qascript.permissions == 'ssdPrm'}">
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
    <%--id="qascript" name="schema" scope="page" property="qascripts" type="QAScript">--%>
          <c:forEach varStatus="index" items="${schema.qascripts}">
            <tr class="${index.intValue() % 2 == 1 ? 'zebraeven' : 'zebraodd'}
              <bean:define id="scriptId" name="qascript" property="scriptId"/>
              <c:if test="${qascript.permissions == 'ssdPrm'}">
                <td align="center">
                  <input type="radio" name="scriptId" value="${scriptId}"/>
                </td>
              </c:if>
              <td>
                <c:choose>
                <c:when test="${qascript.permissions == 'qsuPrm'}">
                  <%--  If scriptType is NOT 'FME' --%>
                  <c:if test="${!qascript.scriptType == eionet.gdem.qa.XQScript.SCRIPT_LANG_FME}">
                    <%--paramId="scriptId" paramName="qascript" paramProperty="scriptId"--%>
                    <html:link page="/old/qaSandbox/edit/${scriptId}"
                               titleKey="label.qasandbox.label.qasandbox.editScript">
                      <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run"
                           title="Run this query in XQuery Sandbox"></img>
                    </html:link>
                  </c:if>
                  <%--  If scriptType is 'FME' --%>
                  <c:if test="${qascript.scriptType == eionet.gdem.qa.XQScript.SCRIPT_LANG_FME}">
                    <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=<bean:write name="schema" property="id"/>"
                       title="<spring:message code="label.qascript.runservice.title" />">
                      <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run"
                           title="Run this query in XQuery Sandbox"></img>
                    </a>
                  </c:if>
                </c:when>
                <c:otherwise>
                  <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=<bean:write name="schema" property="id"/>"
                     title="<spring:message code="label.qascript.runservice.title" />">
                    <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run"
                         title="Run this query in XQuery Sandbox"></img>
                  </a>
                </c:otherwise>
                </c:choose>
              </td>
              <td>
                <a href="viewQAScriptForm?scriptId=<bean:write name="qascript" property="scriptId" />"
                   title="view QAScript properties">
                  <bean:write name="qascript" property="shortName"/>
                </a>
              </td>
              <td>
                <bean:write name="qascript" property="description"/>
              </td>
              <td>
                  <%--  If scriptType is 'FME' don't show the link to the local script file --%>
                <c:choose>
                  <c:when test="${qascript.scriptType == eionet.gdem.qa.XQScript.SCRIPT_LANG_FME}">
                    <bean:write name="qascript" property="fileName"/>
                  </c:when>
                  <c:otherwise>
                    <a href="<bean:write name="webRoot"/>/<bean:write name="qascript" property="filePath" />"
                       title="open QA script file">
                      <bean:write name="qascript" property="fileName"/>
                    </a>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                  <%--  If scriptType is 'FME' don't show the script Last Modified Date --%>
                <c:if test="${qascript.scriptType == eionet.gdem.qa.XQScript.SCRIPT_LANG_FME}">
                        <c:choose>
                          <c:when test="${qascript.modified}">
                            <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                          </c:when>
                          <c:otherwise>
                            <bean:write name="qascript" property="modified"/>
                          </c:otherwise>
                        </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${qascript.active == true}">
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
          <c:if test="${qascript.permissions == 'ssdPrm'}">
            <input type="button" class="button" value="<spring:message code="label.qascript.delete"/>"
                   onclick="return submitAction(2,'deleteQAScript');"/>
            <input type="hidden" name="schemaId" value="${schemaId}"/>
          </c:if>
          <c:if test="${qascript.permissions == 'ssdPrm'}">
            <input type="button" class="button" value="<spring:message code="label.qascript.activate"/>"
                   onclick="return submitAction(2,'activateQAScript');"/>
            <input type="hidden" name="schemaId" value="${schemaId}"/>
          </c:if>
          <c:if test="${qascript.permissions == 'ssdPrm'}">
            <input type="button" class="button" value="<spring:message code="label.qascript.deactivate"/>"
                   onclick="return submitAction(2,'deactivateQAScript');"/>
            <input type="hidden" name="schemaId" value="${schemaId}"/>
          </c:if>
        </div>
      </form:form>

    </c:if>
    <c:if test="${!schema.qascripts}">
      <div class="advice-msg">
        <spring:message code="label.schema.noQAScripts"/>
      </div>
    </c:if>
  </c:forEach>

  <div class="visualClear">&nbsp;</div>
</c:if>



