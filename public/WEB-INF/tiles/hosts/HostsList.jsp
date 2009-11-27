<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>

<logic:present name="user">
	<bean:define id="username" name="user" scope="session"/>
</logic:present>

<html:xhtml/>

<ed:breadcrumbs-push label="Hosts" level="1" />
<ed:hasPermission username="username" acl="host" permission="i">
	<div id="operations">
		<ul><li><html:link page="/do/hosts/add">Add host</html:link>	</li></ul>
	</div>
</ed:hasPermission>

<h1 class="documentFirstHeading">
	<bean:message key="label.hosts.title"/>
</h1>

<%-- include Error display --%>
<tiles:insert definition="Error" />

<div class="visualClear">&nbsp;</div>

<logic:present name="hosts.list">
	<html:form action="/hosts/delete" method="post">
	<div style="width:80%">
		<table class="datatable" width="100%">
			<col style="width:5%"/>
			<col style="width:47%"/>
			<col style="width:47%"/>
			<thead>
				<tr>
					<ed:hasPermission username="username" acl="host" permission="d">
						<th scope="col">&nbsp;</th>
					</ed:hasPermission>
					<th scope="col"><bean:message key="label.hosts.host"/></th>
					<th scope="col"><bean:message key="label.hosts.username"/></th>
				</tr>
			</thead>
			<tbody>
				<logic:iterate indexId="index" id="host" name="hosts.list">
					<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
						<ed:hasPermission username="username" acl="host" permission="d">
							<td>
								<bean:define id="hostId" name="host" property="id" />
								<input type="radio" name="id" value="${hostId}" />
							</td>
						</ed:hasPermission>
						<td>
							<ed:hasPermission username="username" acl="host" permission="u">
								<html:link page="/do/hosts/edit" paramId="id" paramName="host" paramProperty="id" titleKey="label.hosts.edit">
									<bean:write name="host" property="hostname" />
								</html:link>
							</ed:hasPermission>
						</td>
						<td>
							<bean:write name="host" property="username" />
						</td>
					</tr>
				</logic:iterate>
			</tbody>
		</table>
		<div class="boxbottombuttons">
			<ed:hasPermission username="username" acl="host" permission="d">
		        <html:submit styleClass="button" property="action">
		        	<bean:message key="label.delete"/>
		        </html:submit>
				<!--input type="button"  class="button" value="<bean:message key="label.delete"/>" onclick="return submitAction(1,'/do/hosts/delete');" /-->
			</ed:hasPermission>
		</div>
	</div>
	</html:form>
</logic:present>

<div class="visualClear">&nbsp;</div>
