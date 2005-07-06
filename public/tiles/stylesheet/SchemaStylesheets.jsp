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

<ed:breadcrumbs-push label="Stylesheets" level="1" />

<logic:present name="schema.stylesheets">

<logic:iterate indexId="index" id="schema" name="schema.stylesheets" scope="session" property="handCodedStylesheets" type="Schema">

	<h1 class="documentFirstHeading">
		<bean:message key="label.schema.stylesheets"/>&nbsp;<bean:write name="schema" property="schema" />
	</h1>
	
	<div class="visualClear">&nbsp;</div>

<logic:equal  value="true"  name="schema.stylesheets" scope="session" property="handcoded" >
	<div>
		<a href="schemaElemForm.do?schemaId=<bean:write name="schema" property="id"/>">
			<bean:message key="label.schema.info"/>
		</a>
	</div>
	<div class="visualClear">&nbsp;</div>
</logic:equal>

	<div style="width: 97%">
		<table class="sortable" align="center" width="100%">
			<tr>
				<th scope="col"><span title="Type"><bean:message key="label.table.stylesheet.type"/></span></th>
				<th scope="col"><span title="Description"><bean:message key="label.table.stylesheet.description"/></span></th>				
				<th scope="col"><span title="Stylesheet"><bean:message key="label.table.stylesheet.stylesheet"/></span></th>
				<th scope="col"><span title="Modified"><bean:message key="label.table.stylesheet.modified"/></span></th>				
				<th scope="col"><span title="Action"><bean:message key="label.table.stylesheet.action"/></span></th>
			</tr>
			
				<logic:iterate indexId="index" id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">						

				<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
					<td width="5%" align="center">

					<logic:equal  value="true"  name="stylesheet" property="ddConv" >
						<bean:write name="stylesheet" property="type" />
					</logic:equal>
					<logic:notEqual  value="true"  name="stylesheet" property="ddConv" >						
						<a href="stylesheetEditForm.do?stylesheetId=<bean:write name="stylesheet" property="convId" />">
							<bean:write name="stylesheet" property="type" />
						</a>						
					</logic:notEqual>

					</td>
					<td width="20%">
						<bean:write name="stylesheet" property="xsl_descr" />
					</td>
					<td width="45%">					
						<a href="<bean:write name="stylesheet" property="xsl" />">
							<bean:write name="stylesheet" property="xsl" />					
						</a>						
					</td>
					<td width="20%" align="center">
						<bean:write name="stylesheet" property="modified" />															
					</td>
					<td width="10%">
						<a href="conversiob.do?schema=<bean:write name="schema" property="schema" />&amp;id=<bean:write name="stylesheet" property="convId" />"  >
							<img height="15" width="24" src="images/run.png" alt="Run"/>
						</a>			
						<logic:equal name="ssdPrm" value="true"  name="schema.stylesheets" scope="session" property="ssdPrm" >
						<logic:equal name="ssdPrm" value="false"  name="stylesheet"  property="ddConv" >
						&nbsp;
						<a href="deleteStylesheet.do?stylesheetId=<bean:write name="stylesheet" property="convId" />"
						onclick='return stylesheetDelete("<bean:write name="stylesheet" property="xsl" />");'>
							<img src="<bean:write name="webRoot"/>/images/delete.gif" alt="<bean:message key="label.delete" />" title="delete stylesheet" width="15" height="15"/>
						</a>	
						</logic:equal>								
						</logic:equal>		
					</td>
				</tr>
				</logic:iterate>				
				<tr>
					<td valign="top" colspan="2">
					</td>
				</tr>
		</table>
	</div>
	
</logic:iterate>

	<div class="visualClear">&nbsp;</div>

	<logic:equal name="ssiPrm" value="true"  name="schema.stylesheets" scope="session" property="ssiPrm" >
		
	<div class="boxbottombuttons">
	<form action="addStylesheetForm.do">
		<input class="button" type="submit" value="<bean:message key="label.stylesheet.add" />"/>
	</form>
	</div>
	
	</logic:equal>

</logic:present>



