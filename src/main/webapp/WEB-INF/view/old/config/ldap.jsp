<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>


<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li>
        <a href="/config/database" title="label.config.db" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.db"/>
        </a>
      </li>
      <li>
        <a href="/config/system" title="label.config.system" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.system"/>
        </a>
      </li>
      <li>
        <a href="/config/purge" title="label.config.purge" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.purge"/>
        </a>
      </li>
      <li id="currenttab">
        <spring:message code="label.config.ldap" var="configLdap"/>
        <span style="color: black; text-decoration: none;" title="${configLdap}">
          ${configLdap}</span>
      </li>
      <li>
        <a href="/config/basex" title="label.config.basexserver" style="color: black; text-decoration: none;">
          <spring:message code="label.config.basexserver"/>
        </a>
      </li>
    </ul>
  </div>

  <ed:breadcrumbs-push label="LDAP configuration" level="1"/>
  <h1><spring:message code="label.config.ldap.admin"/></h1>

  <form:form action="/config/ldap" method="post" modelAttribute="configLdap">
    <table class="formtable">
      <col style="width:26%"/>
      <col style="width:74%"/>
      <tr>
        <td>
          <label class="question" for="url"><spring:message code="label.config.ldap.url"/></label>
        </td>
        <td>
          <form:input path="url" maxlength="255" style="width: 30em;" styleId="url"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="context"><spring:message code="label.config.ldap.context"/></label>
        </td>
        <td>
          <form:input path="context" maxlength="255" style="width: 30em;" styleId="context"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="userDir"><spring:message code="label.config.ldap.userDir"/></label>
        </td>
        <td>
          <form:input path="userDir" maxlength="255" style="width: 30em;" styleId="userDir"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="attrUid"><spring:message code="label.config.ldap.attrUid"/></label>
        </td>
        <td>
          <form:input path="attrUid" maxlength="255" style="width: 30em;" styleId="attrUid"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <button type="submit" class="button">
            <spring:message code="label.config.ldap.save"/>
          </button>
        </td>
      </tr>
    </table>
  </form:form>
</div>
