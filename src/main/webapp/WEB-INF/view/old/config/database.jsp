<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<spring:message code="label.config.db"/>'><bean:message
              key="label.config.db"/></span></li>
      <li>
        <a href="/config/system" titleKey="label.config.system" style="color: black; text-decoration: none;">
          <spring:message code="label.config.system"/>
        </a>
      </li>
      <li>
        <a href="/config/purge" titleKey="label.config.purge" style="color: black; text-decoration: none;">
          <spring:message code="label.config.purge"/>
        </a>
      </li>
      <li>
        <a href="/config/ldap" titleKey="label.config.ldap" style="color: black; text-decoration: none;">
          <spring:message code="label.config.ldap"/>
        </a>
      </li>
      <li>
        <a href="/config/basex" titleKey="label.config.basexserver" style="color: black; text-decoration: none;">
          <spring:message code="label.config.basexserver"/>
        </a>
      </li>
    </ul>
  </div>

  <ed:breadcrumbs-push label="DB configuration" level="1"/>
  <h1><spring:message code="label.config.db.admin"/></h1>

  <form:form action="/db" method="post">
    <table class="formtable">
      <col style="width:25%"/>
      <col style="width:75%"/>
      <tr>
        <td>
          <label class="question" for="dbUrl"><spring:message code="label.config.db.url"/></label>
        </td>
        <td>
          <form:input path="dbUrl" maxlength="255" style="width: 30em;" styleId="dbUrl"/>
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
          <form:password path="password" maxlength="255" style="width: 30em;" styleId="password"/>
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
