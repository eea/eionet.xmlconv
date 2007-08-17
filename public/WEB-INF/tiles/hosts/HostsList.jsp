<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

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

<div class="visualClear">&nbsp;</div>

<logic:present name="hosts.list">
	<div style="width:80%">
		<table class="datatable" width="100%">
			<col style="width:8%"/>
			<col style="width:46%"/>
			<col style="width:46%"/>
			<thead>
				<tr>
					<th scope="col" title="Action">&nbsp;</th>
					<th scope="col"><bean:message key="label.hosts.host"/></th>
					<th scope="col"><bean:message key="label.hosts.username"/></th>
				</tr>
			</thead>
			<tbody>
				<logic:iterate indexId="index" id="host" name="hosts.list">
					<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
						<td>
							<ed:hasPermission username="username" acl="host" permission="d">
								<a href="delete?id=<bean:write name="host" property="id" />"
													onclick='return hostDelete("<bean:write name="host" property="hostname" />");'>
													<html:img page="/images/delete.gif" altKey="label.delete" title="delete stylesheet"/></a>
							</ed:hasPermission>
						</td>
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
	</div>
</logic:present>

<div class="visualClear">&nbsp;</div>
