<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<logic:present name="user">
    <bean:define id="username" name="user" scope="session"/>
</logic:present>

<html:xhtml/>

        <ed:breadcrumbs-push label="Host details" level="2" />
        <h1 class="documentFirstHeading">
            <logic:empty name="HostForm" property="id">
                <spring:message code="label.hosts.add_title"/>
            </logic:empty>
            <logic:notEmpty name="HostForm" property="id">
                <spring:message code="label.hosts.edit"/>
            </logic:notEmpty>
        </h1>

        <%-- include Error display --%>
        <tiles:insertDefinition name="Error" />

            <form:form action="/hosts/save" method="post">
              <table class="formtable">
                <col class="labelcol"/>
                <col class="entrycol"/>
                <tr class="zebraeven">
                        <td>
                            <label class="question required" for="txtHost">
                                <spring:message code="label.hosts.host"/>
                            </label>
                        </td>
                        <td align="left">
                            <html:text property="host" size="70"  styleId="txtHost"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="question required" for="txtUsername">
                                <spring:message code="label.hosts.username"/>
                            </label>
                        </td>
                        <td align="left">
                            <html:text property="username" size="70"  styleId="txtUsername"/>
                        </td>
                    </tr>
                <tr class="zebraeven">
                        <td>
                            <label class="question" for="txtPassword">
                                <spring:message code="label.hosts.password"/>
                            </label>
                        </td>
                        <td align="left">
                            <html:text property="password" size="70"  styleId="txtPassword"/>
                        </td>
                    </tr>
                    <tr><td colspan="2">&#160;</td></tr>
                    <tr>
                        <td>&#160;</td>
                        <td>
                            <html:submit styleClass="button">Save</html:submit>
                            <html:cancel styleClass="button">
                                <spring:message code="label.cancel"/>
                            </html:cancel>
                        </td>
                    </tr>
                </table>
            </form:form>

