<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %><%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<html:xhtml/>

<ed:breadcrumbs-push label="All QA Scripts" level="1"/>


<logic:equal value="true" name="qascript.permissions" property="ssiPrm">
  <div id="operations">
    <ul>
      <li>
        <html:link page="/old/qaScripts/add"><spring:message code="label.qascript.add"/></html:link>
      </li>
    </ul>
  </div>
</logic:equal>

<h1 class="documentFirstHeading">
  <spring:message code="label.qascript.title"/>
</h1>

<%-- include Error display --%>
<tiles:insertDefinition name="Error"/>

<div class="visualClear">&nbsp;</div>

<logic:present name="qascript.qascriptList" property="qascripts">
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
      <logic:iterate indexId="index" id="schema" name="qascript.qascriptList" property="qascripts" type="Schema">
        <tr <%=(index.intValue() % 2 == 1) ? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
          <td title="<bean:write name="schema" property="schema"/>">
            <html:link page="/old/schemas/${schema.id}/qaScripts" title="view QA scripts for this XML Schema">
              <bean:write name="schema" property="schema"/>
            </html:link>
          </td>
          <td>
            <logic:present name="schema" property="qascripts">
              <logic:iterate id="qascript" name="schema" scope="page" property="qascripts" type="QAScript">
                <html:link page="/old/qaScripts/${schema.id}" titleKey="label.qascript.tab.title">
                  <bean:write name="qascript" property="shortName"/>
                </html:link>
                &#160;
              </logic:iterate>
            </logic:present>
          </td>
        </tr>
      </logic:iterate>
      <tr>
        <td valign="top" colspan="3">
        </td>
      </tr>
      </tbody>
    </table>
  </div>

  <div class="visualClear">&nbsp;</div>

</logic:present>



