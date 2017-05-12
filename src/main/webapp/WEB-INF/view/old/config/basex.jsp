<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<%--<html:xhtml />--%>
<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li>
        <html:link page="/do/dbForm" titleKey="label.config.db" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <bean:message key="label.config.db"/>
        </html:link>
      </li>
      <li>
        <html:link page="/do/systemForm" titleKey="label.config.system" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <bean:message key="label.config.system"/>
        </html:link>
      </li>
      <li>
        <html:link page="/do/purgeForm" titleKey="label.config.purge" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <bean:message key="label.config.purge"/>
        </html:link>
      </li>
      <li>
        <html:link page="/do/ldapForm" titleKey="label.config.ldap" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <bean:message key="label.config.ldap"/>
        </html:link>
      </li>
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<bean:message key="label.config.basexserver"/>'><bean:message
              key="label.config.basexserver"/></span></li>
    </ul>
  </div>

  <ed:breadcrumbs-push label="BaseX Server configuration" level="1"/>
  <h1><bean:message key="label.config.basexserver.title"/></h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>
  <form:form servletRelativeAction="basex" method="post">
    <table class="formtable">
      <col style="width:25%"/>
      <col style="width:75%"/>
      <tr>
        <td>
          <label class="question" for="host"><bean:message key="label.config.basexserver.host"/></label>
        </td>
        <td>
          <html:text property="host" maxlength="255" style="width: 30em;" styleId="host"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="port"><bean:message key="label.config.basexserver.port"/></label>
        </td>
        <td>
          <html:text property="port" maxlength="255" style="width: 30em;" styleId="port"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="user"><bean:message key="label.config.basexserver.user"/></label>
        </td>
        <td>
          <html:text property="user" maxlength="255" style="width: 30em;" styleId="user"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="password"><bean:message key="label.config.basexserver.password"/></label>
        </td>
        <td>
          <html:password property="password" maxlength="255" style="width: 30em;" styleId="password"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <button class="button" name="action" value="submit"><bean:message
                  key="label.config.basexserver.update"/></button>
        </td>
      </tr>
    </table>
  </form:form>
</div>
