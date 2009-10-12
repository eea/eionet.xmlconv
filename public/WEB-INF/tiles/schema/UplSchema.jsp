<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

<ed:breadcrumbs-push label="XML Schemas" level="1" />

<logic:present name="schemas.uploaded">

	<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssiPrm" >
		<div id="operations">
		  <ul>
		   <li><a href="addUplSchemaForm"><bean:message key="label.uplSchema.add" /></a></li>
			</ul>
		</div>
	</logic:equal>

	<h1 class="documentFirstHeading">
		<bean:message key="label.schemas.uploaded"/>
	</h1>

	<%-- include Error display --%>
	<tiles:insert definition="Error" />


	<logic:present name="schemas" name="schemas.uploaded" scope="session" property="schemas" >
		<html:form action="/deleteUplSchema" method="post">
			<table class="datatable" width="100%">
				<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssdPrm" >
					<col style="width:5%"/>
				</logic:equal>
				<col/>
				<col style="width:5%"/>
				<col style="width:45%"/>
				<thead>
					<tr>
						<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssdPrm" >
							<th scope="col"></th>
						</logic:equal>
						<th scope="col"><span title="Schema"><bean:message key="label.table.uplSchema.schema"/></span></th>
						<th scope="col"></th>
						<th scope="col"><span title="Description"><bean:message key="label.table.uplSchema.description"/></span></th>
					</tr>
				</thead>
				<tbody>
					<logic:iterate indexId="index" id="schema" name="schemas.uploaded" scope="session" property="schemas" type="UplSchema">
						<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
							<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssdPrm" >
								<td align="center" >
									<bean:define id="schemaId"  name="schema" property="schemaId" />
									<html:radio property="schemaId" value="${schemaId}" /> 
								</td>
							</logic:equal>
							<td>
								<a href="viewSchemaForm?schemaId=<bean:write name="schema" property="schemaId" />" title="view XML Schema properties">
									<bean:write name="schema" property="schemaUrl"  />
								</a>
							</td>
							<td align="center" >
								<logic:notEmpty name="schema" property="uplSchemaFile">
									<a href="<bean:write name="schema" property="uplSchemaFileUrl" />">
										<img src="<bean:write name="webRoot"/>/images/file.gif" alt="<bean:message key="label.uplSchema.schemaFile" />" title="view uploaded schema" /></a>
								</logic:notEmpty>
							</td>
							<td>
									<bean:write name="schema" property="description" />
							</td>
						</tr>
					</logic:iterate>
				</tbody>
			</table>
			<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssdPrm" >
				<div class="boxbottombuttons">
	    			<input type="button"  class="button" value="<bean:message key="label.schema.delete"/>" onclick="return submitAction(1,'deleteUplSchema?deleteSchema=true');" />
	    		</div>
			</logic:equal>						
		</html:form>
	</logic:present>
	
	<logic:notPresent name="schemas" name="schemas.uploaded" scope="session" property="schemas" >
		<div class="success">
			<bean:message key="label.uplSchema.noSchemas"/>
		</div>
	</logic:notPresent>
	<div class="visualClear">&nbsp;</div>


</logic:present>

