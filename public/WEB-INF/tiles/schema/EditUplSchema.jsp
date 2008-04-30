<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
		<ed:breadcrumbs-push label="Edit Schema" level="2" />
		<h1><bean:message key="label.title.uplSchema.edit"/></h1> 
	
		<%-- include Error display --%>
		<tiles:insert definition="Error" />
		
			<html:form action="/editUplSchema" method="post" enctype="multipart/form-data">
			  <table class="datatable">
				<col style="width:16%"/>
				<col style="width:84%"/>
			    <tr>
  				  <th scope="row" class="scope-row">
			        <bean:message key="label.uplSchema.url"/>:
			      </th>
			      <td>
					<a  href="<bean:write name="EditUplSchemaForm" property="schemaUrl" />">
						<bean:write name="EditUplSchemaForm" property="schemaUrl" />
					</a>
			      </td>
			    </tr>
				<tr>
  				  <th scope="row" class="scope-row">
			        <bean:message key="label.uplSchema.schemaFile"/>:
			      </th>
			      <td>
			      	<logic:notEmpty  name="EditUplSchemaForm" property="schemaFileName">
						<a  href="<bean:write name="EditUplSchemaForm" property="uplSchemaFileUrl" />" title="<bean:write name="EditUplSchemaForm" property="uplSchemaFileUrl" />">						
							<bean:write name="EditUplSchemaForm" property="schemaFileName" />
						</a>&#160;		        	  		        
						<logic:present name="EditUplSchemaForm" property="lastModified">
							&#160;&#160;(<bean:message key="label.lastmodified"/>: <bean:write property="lastModified" name="EditUplSchemaForm"/>)
						</logic:present>
						<logic:equal name="ssdPrm" value="true"  name="schemas.uploaded" scope="session" property="ssdPrm" >
							<a href="deleteUplSchema?schemaId=<bean:write name="EditUplSchemaForm" property="schemaId" />&amp;schemaFile=<bean:write name="EditUplSchemaForm" property="schemaFileName" />"
							onclick='return schemaDelete("<bean:write name="EditUplSchemaForm" property="schemaFileName" />");'>
								<img src="<bean:write name="webRoot"/>/images/delete.gif" alt="<bean:message key="label.delete" />" title="delete schema" /></a>
						</logic:equal>
			      	</logic:notEmpty>
						<html:hidden  property="schemaId" />
						<html:hidden  property="uplSchemaId" />
						<html:hidden  property="schemaUrl" />
						<html:hidden  property="schemaFileName" />
						<br/><br/>
				        <html:file property="schemaFile" size="20" style="width:400px"/>
			      </td>
			    </tr>
			    <tr>
			      <td colspan="2">&nbsp;</td>
			    </tr>
			    <tr>
			      <td colspan="2" align="center">
			        <html:submit styleClass="button">
			        	<bean:message key="label.upload"/>
			        </html:submit>
			        <html:cancel styleClass="button">
			        	<bean:message key="label.cancel"/>
			        </html:cancel>		        
			      </td>
			    </tr>
			  </table>
			</html:form>
