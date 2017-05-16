<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>


<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li>
        <a href="/do/dbForm" titleKey="label.config.db" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.db"/>
        </a>
      </li>
      <li>
        <a href="/do/systemForm" titleKey="label.config.system" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.system"/>
        </a>
      </li>
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<spring:message code="label.config.purge"/>'><bean:message
              key="label.config.purge"/></span></li>
      <li>
        <a href="/do/ldapForm" titleKey="label.config.ldap" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.ldap"/>
        </a>
      </li>
      <li>
        <a href="/do/basexForm" titleKey="label.config.basexserver" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.basexserver"/>
        </a>
      </li>
    </ul>
  </div>


  <ed:breadcrumbs-push label="Purge" level="1"/>
  <h1><spring:message code="label.config.purge.title"/></h1>




  <form:form action="/purge" method="post">
    <table class="formtable">
      <col style="width:25%"/>
      <col style="width:75%"/>
      <tr>
        <td>
          <label for="nofDays" class="question"><spring:message code="label.config.purge.nofdays"/></label>
        </td>
        <td>
          <form:input path="nofDays" maxlength="10" style="width: 10em;" styleId="nofDays"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <button type="submit" class="button">
            <spring:message code="label.config.purge.submit"/>
          </button>
        </td>
      </tr>

    </table>
  </form:form>

</div>
