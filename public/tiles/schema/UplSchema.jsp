<%@ page contentType="text/html; charset=UTF-8" 
  import="java.util.List"
  import="java.util.Iterator"
  import="eionet.gdem.dto.*"
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>

<ed:breadcrumbs-push label="Uploaded schemas" level="1" />

<logic:present name="schemas.uploaded">
	<h1 class="documentFirstHeading">
		<bean:message key="label.schemas.uploaded"/>
	</h1>
	
	<div class="visualClear">&nbsp;</div>

	<div style="width: 97%">
		<table class="sortable" align="center" width="60%">
			<tr>
				<th scope="col"><span title="Schema"><bean:message key="label.table.uplSchema.schema"/></span></th>
				<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssdPrm" >				
				<th scope="col"><span title="Action"><bean:message key="label.table.uplSchema.action"/></span></th>
				</logic:equal>
			</tr>
				<logic:iterate indexId="index" id="schema" name="schemas.uploaded" scope="session" property="schemas" type="UplSchema">
				<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
					<td width="55%">
						<a href="<bean:write name="schema" property="schema" />">
							<bean:write name="schema" property="schema" />
						</a>
					</td>
					<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssdPrm" >
					<td width="5%" align="center">
						<a href="deleteUplSchema.do?schemaId=<bean:write name="schema" property="id" />"
						onclick='return schemaDelete("<bean:write name="schema" property="schema" />");'>
							<img src="<bean:write name="webRoot"/>/images/delete.gif" alt="<bean:message key="label.delete" />" title="delete schema" width="15" height="15"/>
						</a>
					</td>
    				</logic:equal>
				</tr>
				</logic:iterate>
				<tr>
					<td valign="top" colspan="2">
					</td>
				</tr>
		</table>
	</div>
	
	<div class="visualClear">&nbsp;</div>
	

	<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssiPrm" >
		
	<div class="boxbottombuttons">
	<form action="addUplSchemaForm.do">
		<input class="button" type="submit" value="<bean:message key="label.uplSchema.add" />"/>
	</form>
	</div>
	
	</logic:equal>

	
</logic:present>



