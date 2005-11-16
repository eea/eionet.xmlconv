<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
<div id="stylesheet" class="box"> 
	<div class="boxleft"> 
		<div class="boxtop">
			<div></div>
		</div> 

		<ed:breadcrumbs-push label="Edit Schema" level="2" />
		<h4><bean:message key="label.title.uplSchema.edit"/></h4> 

		<div class="boxcontent">
	
			<html:form action="/editUplSchema" method="post">
			  <table cellpadding="0" cellspacing="0" border="0" align="center">
			    <tr>
			      <td>
			        <bean:message key="label.uplSchema.schemaFile"/>:
			      </td>
			      <td>&nbsp;</td>
			      <td>
						<a target="blank" href="<bean:write name="EditUplSchemaForm" property="schema" />" title="<bean:write name="EditUplSchemaForm" property="schema" />">						
							<bean:write name="EditUplSchemaForm" property="schema" />
						</a>&#160;		        	  		        
						<html:hidden  property="idSchema" />
			      </td>
			    </tr>
			    <tr>
			      <td colspan="3">&nbsp;</td>
			    </tr>
			    <tr>
			      <td>
			        <bean:message key="label.uplSchema.description"/>:
			      </td>
			      <td>&nbsp;</td>
			      <td>
			        <html:textarea property="description"  rows="3" cols="30" style="width:400px"/>
			      </td>
			    </tr>
			    <tr>
			      <td colspan="3">&nbsp;</td>
			    </tr>
			    <tr>
			      <td colspan="3" align="center">
			        <html:submit styleClass="button">
			        	<bean:message key="label.ok"/>
			        </html:submit>
			        <html:cancel styleClass="button">
			        	<bean:message key="label.cancel"/>
			        </html:cancel>		        
			      </td>
			    </tr>
			  </table>
			</html:form>
			
		</div>
	<div class="boxbottom">
		<div></div>
	</div> 
	</div>
</div>

