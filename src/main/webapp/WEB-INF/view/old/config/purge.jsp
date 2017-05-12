<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<html:xhtml/>
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
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<bean:message key="label.config.purge"/>'><bean:message
              key="label.config.purge"/></span></li>
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


  <ed:breadcrumbs-push label="Purge" level="1"/>
  <h1><bean:message key="label.config.purge.title"/></h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <html:form action="/purge" method="post">
    <table class="formtable">
      <col style="width:25%"/>
      <col style="width:75%"/>
      <tr>
        <td>
          <label for="nofDays" class="question"><bean:message key="label.config.purge.nofdays"/></label>
        </td>
        <td>
          <html:text property="nofDays" maxlength="10" style="width: 10em;" styleId="nofDays"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <html:submit styleClass="button">
            <bean:message key="label.config.purge.submit"/>
          </html:submit>
        </td>
      </tr>

    </table>
  </html:form>

</div>
