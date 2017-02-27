<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%
pageContext.setAttribute("org.apache.struts.globals.XHTML", "true", 1);
%>
<ed:breadcrumbs-push label="Login" level="1"/>

<br/>

<table width="100%">
<tr>
<td align="center">
        <h1><bean:message key="label.login.message"/></h1>

        <%-- include Error display --%>
        <tiles:insertDefinition name="Error" />

            <html:form action="login" method="post" focus="username">
              <table class="datatable" style="width:300px">
                <col style="width:36%"/>
                <col style="width:64%"/>
                <tr>
                    <th scope="row" class="scope-row">
                        <bean:message key="label.login.username"/>:
                      </th>
                  <td>
                    <html:text property="username" size="15"/>
                  </td>
                </tr>
                <tr>
                    <th scope="row" class="scope-row">
                        <bean:message key="label.login.password"/>:
                      </th>
                  <td>
                    <html:password property="password" size="15"/>
                  </td>
                </tr>
                <tr>
                  <td colspan="3">&nbsp;</td>
                </tr>
                <tr>
                  <td colspan="3" align="center">
                    <html:submit styleClass="button">
                        <bean:message key="label.login.submit"/>
                    </html:submit>
                  </td>
                </tr>
              </table>
            </html:form>
</td>
</tr>
</table>
