<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li>
        <a href="/config/database" titleKey="label.config.db" style="color: black; text-decoration: none;">
          <spring:message code="label.config.db"/>
        </a>
      </li>
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<spring:message code="label.config.system"/>'><bean:message
              key="label.config.system"/></span></li>
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

  <ed:breadcrumbs-push label="System configuration" level="1"/>
  <h1><spring:message code="label.config.system.admin"/></h1>

  <form:form servletRelativeAction="system" method="post">
    <table class="formtable">
      <col style="width:25%"/>
      <col style="width:75%"/>
      <tr>
        <td>
          <label for="qaTimeout" class="question"><spring:message code="label.config.system.qa.timeout"/></label>
        </td>
        <td>
          <form:input path="qaTimeout" maxlength="20" style="width: 30em;" styleId="qaTimeout"/>
        </td>
      </tr>
      <tr>
        <td>
          <label for="cmdXGawk" class="question"><spring:message code="label.config.system.qa.xgawk"/></label>
        </td>
        <td>
          <form:input path="cmdXGawk" maxlength="255" style="width: 30em;" styleId="cmdXGawk"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <button type="submit" styleClass="button">
            <spring:message code="label.config.system.save"/>
          </button>
        </td>
      </tr>

    </table>
  </form:form>

</div>
