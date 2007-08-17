<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<ed:breadcrumbs-push label="Uploaded schemas" level="1" />

<logic:present name="schemas.uploaded">
	<h1 class="documentFirstHeading">
		<bean:message key="label.schemas.uploaded"/>
	</h1>

	<div class="visualClear">&nbsp;</div>

	<logic:present name="schemas" name="schemas.uploaded" scope="session" property="schemas" >
		<div style="width: 97%">
			<table class="datatable" width="80%">
				<col style="width:10%"/>
				<col style="width:45%"/>
				<col style="width:45%"/>
				<thead>
					<tr>
						<th scope="col"><span title="Action"><bean:message key="label.table.uplSchema.action"/></span></th>
						<th scope="col"><span title="Schema"><bean:message key="label.table.uplSchema.schema"/></span></th>
						<th scope="col"><span title="Description"><bean:message key="label.table.uplSchema.description"/></span></th>
					</tr>
				</thead>
				<tbody>
					<logic:iterate indexId="index" id="schema" name="schemas.uploaded" scope="session" property="schemas" type="UplSchema">
						<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
							<td align="center" >
								<a href="schemaStylesheets?schema=<bean:write name="schema" property="schema" />">
									<img src="<bean:write name="webRoot"/>/images/properties.gif" alt="<bean:message key="label.table.stylesheet" />" title="view stylesheets" /></a>
								<logic:equal name="ssuPrm" value="true"  name="schemas.uploaded" scope="session" property="ssuPrm" >
									<a href="editUplSchemaForm?schemaId=<bean:write name="schema" property="id" />">
										<img src="<bean:write name="webRoot"/>/images/edit.gif" alt="<bean:message key="label.edit" />" title="edit schema" /></a>
								</logic:equal>
								<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssdPrm" >
									<a href="deleteUplSchema?schemaId=<bean:write name="schema" property="id" />"
									onclick='return schemaDelete("<bean:write name="schema" property="schema" />");'>
										<img src="<bean:write name="webRoot"/>/images/delete.gif" alt="<bean:message key="label.delete" />" title="delete schema" /></a>
								</logic:equal>
							</td>
							<td>
								<a  href="<bean:write name="schema" property="schema" />">
									<bean:write name="schema" property="schema" />
								</a>
							</td>
							<td>
									<bean:write name="schema" property="description" />
							</td>
						</tr>
					</logic:iterate>
					<tr>
						<td valign="top" colspan="3">
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</logic:present>
	<logic:notPresent name="schemas" name="schemas.uploaded" scope="session" property="schemas" >
		<div class="success">
			<bean:message key="label.uplSchema.noSchemas"/>
		</div>
	</logic:notPresent>
	<div class="visualClear">&nbsp;</div>


	<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssiPrm" >
		<div class="boxbottombuttons">
			<form action="addUplSchemaForm">
				<div>
					<input class="button" type="submit" value="<bean:message key="label.uplSchema.add" />"/>
				</div>
			</form>
		</div>
	</logic:equal>


</logic:present>



