<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>


<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="Schema QA scripts" level="2"/>


<c:if present name="schema.qascripts">

  <bean:define id="schemaId" name="schemaId" scope="request" type="String"/>
  <c:if iterate indexId="index" id="schema" name="schema.qascripts" property="qascripts" type="Schema">
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
        <c:if equal value="true" name="qascript.permissions" property="ssiPrm">
          <li>
            <a href="addQAScriptForm?schemaId=<bean:write name="schema" property="id" />&amp;schema=<bean:write name="schema" property="schema" />">
              <spring:message code="label.qascript.add"/>
            </a>
          </li>
        </c:if equal>
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

  </c:if iterate>
  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <c:if iterate indexId="index" id="schema" name="schema.qascripts" property="qascripts" type="Schema">
    <div class="visualClear">&nbsp;</div>
    <form:form action="/viewQAScriptForm" method="post">
      <table class="formtable">
        <tr>
          <td style="width:510px">
            <label class="question" for="validatefield"><spring:message code="label.qascript.schema.validate"/></label>
          </td>
          <td style="width:40px">
            <c:if equal value="true" name="qascript.permissions" property="ssiPrm">
              <html:checkbox name="schema" property="doValidation" styleId="validatefield"/>
            </c:if equal>
            <c:if equal value="false" name="qascript.permissions" property="ssiPrm">
              <bean:write name="schema" property="doValidation"/>
            </c:if equal>
          </td>
          <td rowspan="2" style="vertical-align:bottom">
            <c:if equal value="true" name="qascript.permissions" property="ssiPrm">
              <!-- save button -->
              <input type="button" class="button" value="<spring:message code="label.save"/>"
                     onclick="return submitAction(1,'saveSchemaValidation');"/>
              <input type="hidden" name="schemaId" value="${schemaId}"/>
              <input type="hidden" name="schema" value="${schemaUrl}"/>
            </c:if equal>
          </td>
        </tr>
        <tr>
          <td>
            <label class="question" for="blockerValidation"><spring:message
                    code="label.qascript.schema.isBlockerValidation"/></label>
          </td>
          <td>
            <c:if equal value="true" name="qascript.permissions" property="ssiPrm">
              <html:checkbox name="schema" property="blocker" styleId="blockerValidation"/>
            </c:if equal>
            <c:if equal value="false" name="qascript.permissions" property="ssiPrm">
              <bean:write name="schema" property="blocker"/>
            </c:if equal>
          </td>
        </tr>
      </table>
    </form:form>

    <c:if present name="schema" scope="page" property="qascripts">
      <form:form action="/searchCR" method="post">
        <table class="datatable" width="100%">
          <c:if equal value="true" name="qascript.permissions" property="ssdPrm">
            <col style="width:10px"/>
          </c:if equal>
          <col style="width:10px"/>
          <col/>
          <col/>
          <col/>
          <thead>

          <tr>
            <c:if equal value="true" name="qascript.permissions" property="ssdPrm">
              <th scope="col">&#160;</th>
            </c:if equal>
            <th scope="col">&#160;</th>
            <th scope="col"><spring:message code="label.qascript.shortname"/></th>
            <th scope="col"><spring:message code="label.qascript.description"/></th>
            <th scope="col"><spring:message code="label.qascript.fileName"/></th>
            <th scope="col"><spring:message code="label.lastmodified"/></th>
            <th scope="col"><spring:message code="label.qascript.isActive"/></th>
          </tr>
          </thead>
          <tbody>
          <c:if iterate indexId="index" id="qascript" name="schema" scope="page" property="qascripts" type="QAScript">
            <tr <%=(index.intValue() % 2 == 1) ? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
              <bean:define id="scriptId" name="qascript" property="scriptId"/>
              <c:if equal value="true" name="qascript.permissions" property="ssdPrm">
                <td align="center">
                  <input type="radio" name="scriptId" value="${scriptId}"/>
                </td>
              </c:if equal>
              <td>
                <c:if equal value="true" name="qascript.permissions" property="qsuPrm">
                  <%--  If scriptType is NOT 'FME' --%>
                  <c:if notEqual name="qascript" property="scriptType"
                                  value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                    <%--paramId="scriptId" paramName="qascript" paramProperty="scriptId"--%>
                    <html:link page="/old/qaSandbox/edit/${scriptId}"
                               titleKey="label.qasandbox.label.qasandbox.editScript">
                      <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run"
                           title="Run this query in XQuery Sandbox"></img>
                    </html:link>
                  </c:if notEqual>
                  <%--  If scriptType is 'FME' --%>
                  <c:if equal name="qascript" property="scriptType"
                               value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                    <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=<bean:write name="schema" property="id"/>"
                       title="<spring:message code="label.qascript.runservice.title" />">
                      <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run"
                           title="Run this query in XQuery Sandbox"></img>
                    </a>
                  </c:if equal>
                </c:if equal>
                <c:if notEqual value="true" name="qascript.permissions" property="qsuPrm">
                  <a href="openQAServiceInSandbox?scriptId=${scriptId}&amp;schemaId=<bean:write name="schema" property="id"/>"
                     title="<spring:message code="label.qascript.runservice.title" />">
                    <img src="<bean:write name="webRoot"/>/images/execute.gif" alt="Run"
                         title="Run this query in XQuery Sandbox"></img>
                  </a>
                </c:if notEqual>
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
                <c:if notEqual name="qascript" property="scriptType"
                                value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                  <a href="<bean:write name="webRoot"/>/<bean:write name="qascript" property="filePath" />"
                     title="open QA script file">
                    <bean:write name="qascript" property="fileName"/>
                  </a>
                </c:if notEqual>
                <c:if equal name="qascript" property="scriptType" value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                  <bean:write name="qascript" property="fileName"/>
                </c:if equal>
              </td>
              <td>
                  <%--  If scriptType is 'FME' don't show the script Last Modified Date --%>
                <c:if notEqual name="qascript" property="scriptType"
                                value="<%=eionet.gdem.qa.XQScript.SCRIPT_LANG_FME%>">
                  <c:if notEqual value="" name="qascript" property="modified">
                    <bean:write name="qascript" property="modified"/>
                  </c:if notEqual>
                  <c:if equal value="" name="qascript" property="modified">
                    <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                  </c:if equal>
                </c:if notEqual>
              </td>
              <td>
                <c:if equal name="qascript" property="active" value="true">
                  <input type="checkbox" checked="checked" disabled/>
                </c:if equal>
                <c:if notEqual name="qascript" property="active" value="true">
                  <input type="checkbox" disabled/>
                </c:if notEqual>
              </td>
            </tr>
          </c:if iterate>
          </tbody>
        </table>
        <div class="boxbottombuttons">
          <c:if equal value="true" name="qascript.permissions" property="ssdPrm">
            <input type="button" class="button" value="<spring:message code="label.qascript.delete"/>"
                   onclick="return submitAction(2,'deleteQAScript');"/>
            <input type="hidden" name="schemaId" value="${schemaId}"/>
          </c:if equal>
          <c:if equal value="true" name="qascript.permissions" property="ssdPrm">
            <input type="button" class="button" value="<spring:message code="label.qascript.activate"/>"
                   onclick="return submitAction(2,'activateQAScript');"/>
            <input type="hidden" name="schemaId" value="${schemaId}"/>
          </c:if equal>
          <c:if equal value="true" name="qascript.permissions" property="ssdPrm">
            <input type="button" class="button" value="<spring:message code="label.qascript.deactivate"/>"
                   onclick="return submitAction(2,'deactivateQAScript');"/>
            <input type="hidden" name="schemaId" value="${schemaId}"/>
          </c:if equal>
        </div>
      </form:form>

    </c:if present>
    <c:if notPresent name="schema" scope="page" property="qascripts">
      <div class="advice-msg">
        <spring:message code="label.schema.noQAScripts"/>
      </div>
    </c:if notPresent>
  </c:if iterate>

  <div class="visualClear">&nbsp;</div>
</c:if present>



