<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li id="currenttab">
        <spring:message code="label.config.db" var="configDB"/>
        <span style="color: black; text-decoration: none;" title='${configDB}'>
          ${configDB}</span>
      </li>
      <li>
        <a href="/config/system" title="label.config.system" style="color: black; text-decoration: none;">
          <spring:message code="label.config.system"/>
        </a>
      </li>
      <li>
        <a href="/config/purge" title="label.config.purge" style="color: black; text-decoration: none;">
          <spring:message code="label.config.purge"/>
        </a>
      </li>
      <li>
        <a href="/config/ldap" title="label.config.ldap" style="color: black; text-decoration: none;">
          <spring:message code="label.config.ldap"/>
        </a>
      </li>
      <li>
        <a href="/config/basex" title="label.config.basexserver" style="color: black; text-decoration: none;">
          <spring:message code="label.config.basexserver"/>
        </a>
      </li>
    </ul>
  </div>

  <ed:breadcrumbs-push label="DB configuration" level="1"/>
  <h1><spring:message code="label.config.db.admin"/></h1>

  <form:form servletRelativeAction="/config/database" method="post" modelAttribute="form">
    <table class="formtable">
      <col style="width:25%"/>
      <col style="width:75%"/>
      <tr>
        <td>
          <label class="question" for="dbUrl"><spring:message code="label.config.db.url"/></label>
        </td>
        <td>
          <form:input path="url" maxlength="255" style="width: 30em;" id="dbUrl"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="user"><spring:message code="label.config.db.user"/></label>
        </td>
        <td>
          <form:input path="user" maxlength="255" style="width: 30em;" styleId="user"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="password"><spring:message code="label.config.db.password"/></label>
        </td>
        <td>
          <form:password showPassword="false" path="password" maxlength="255" style="width: 30em;" styleId="password"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <button type="submit" class="button">
              <spring:message code="label.config.db.update"/>
          </button>
        </td>
      </tr>
    </table>
  </form:form>

</div>
