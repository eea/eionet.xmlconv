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
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<bean:message key="label.config.system"/>'><bean:message
              key="label.config.system"/></span></li>
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
      <li>
        <html:link page="/do/basexForm" titleKey="label.config.basexserver" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <bean:message key="label.config.basexserver"/>
        </html:link>
      </li>
    </ul>
  </div>


  <ed:breadcrumbs-push label="System configuration" level="1"/>
  <h1><bean:message key="label.config.system.admin"/></h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <form:form servletRelativeAction="system" method="post">
    <table class="formtable">
      <col style="width:25%"/>
      <col style="width:75%"/>
      <tr>
        <td>
          <label for="qaTimeout" class="question"><bean:message key="label.config.system.qa.timeout"/></label>
        </td>
        <td>
          <html:text property="qaTimeout" maxlength="20" style="width: 30em;" styleId="qaTimeout"/>
        </td>
      </tr>
      <tr>
        <td>
          <label for="cmdXGawk" class="question"><bean:message key="label.config.system.qa.xgawk"/></label>
        </td>
        <td>
          <html:text property="cmdXGawk" maxlength="255" style="width: 30em;" styleId="cmdXGawk"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <html:submit styleClass="button">
            <bean:message key="label.config.system.save"/>
          </html:submit>
        </td>
      </tr>

    </table>
  </form:form>

</div>
