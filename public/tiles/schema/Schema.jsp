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

<html:xhtml/>
<div id="schema" class="box"> 
	<div class="boxleft"> 
		<div class="boxtop"><div>
		</div>
	</div> 

			<ed:breadcrumbs-push label="XML Schema or DTD" level="1" />
			<h4><bean:message key="label.schema.edit"/></h4> 

		<div class="boxcontent">

		<html:form action="/schemaElemForm" method="post" >
		  <table cellpadding="0" cellspacing="0" border="0" align="center">
		    <tr>
		      <td>
    		    <html:hidden property="schemaId" />
		        <bean:message key="label.schema.location"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:text property="schema" maxlength="50" style="width: 30em;" />		        
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <tr>
		      <td>
		        <bean:message key="label.schema.description"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:text property="description" maxlength="50" style="width: 30em;" />		        
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    <bean:define id="schemaProp" name="schema.rootElemets" scope="session" property="schema" />
		    <logic:equal  value="true"  name="schemaProp" scope="page" property="isDTD" >
		    <tr>
		      <td>
		        <bean:message key="label.elem.dtdid"/>:
		      </td>
		      <td>&nbsp;</td>
		      <td>
		        <html:text property="dtdId" maxlength="50" style="width: 30em;" />		        
		      </td>
		    </tr>
		    <tr>
		      <td>&nbsp;</td>
		    </tr>
		    </logic:equal>	
		  </table>
		  
		  <div class="visualClear">&nbsp;</div>
		  
			<logic:equal name="xsduPrm" value="true"  name="schema.rootElemets" scope="session" property="xsduPrm" >				
			<div class="boxbottombuttons">
   			    <input type="button" styleClass="button" class="button" value="<bean:message key="label.schema.save"/>" onclick="return submitAction('schemaUpdate.do');">
			</div>
		    </logic:equal>	
		  <div class="visualClear">&nbsp;</div>
		  
		  <table class="sortable" align="center" width="80%">
			<tr>
				<th scope="col"><span title="Element name"><bean:message key="label.schema.table.element"/></span></th>
				<th scope="col"><span title="Namespace"><bean:message key="label.schema.table.namespace"/></span></th>
				<th scope="col"><span title="Action"><bean:message key="label.schema.table.action"/></span></th>				
			</tr>
			
				<logic:iterate indexId="index" id="elem" name="schema.rootElemets" scope="session" property="rootElem" type="RootElem">						

				<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
					<td width="45%">
						<bean:write name="elem" property="name" />
					</td>
					<td width="45%">					
							<bean:write name="elem" property="namespace" />					
					</td>
					<td width="10%">
						<logic:equal name="ssdPrm" value="true"  name="schema.rootElemets" scope="session" property="xsduPrm" >
						<a href="deleteElem.do?elemId=<bean:write name="elem" property="elemId" />"
						onclick='return elementDelete("<bean:write name="elem" property="name" />");'>
							<img src="<bean:write name="webRoot"/>/images/delete.gif" alt="<bean:message key="label.delete" />" title="delete root element" width="15" height="15"/>
						</a>	
						</logic:equal>		
					</td>
				</tr>
				</logic:iterate>				
				<logic:equal name="xsduPrm" value="true"  name="schema.rootElemets" scope="session" property="xsduPrm" >
				<tr>
					<td width="45%">
						 <html:text property="elemName" maxlength="50" style="width: 30em;" />
					</td>
					<td width="45%">					
						  <html:text property="namespace" maxlength="50" style="width: 30em;" />
					</td>
					<td width="10%">
						<input type="button" styleClass="button" class="button" value="<bean:message key="label.element.add"/>" onclick="return submitAction('elementAdd.do');">
					</td>
				</tr>
				<tr>
					<td valign="top" colspan="2">
					</td>
				</tr>
			   </logic:equal>	
		</table>
		  
		</html:form>
		
		</div>
		<div class="boxbottom"><div></div></div> 
	</div>
</div>

