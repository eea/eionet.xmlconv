<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
		<ed:breadcrumbs-push label="Edit XML File" level="2" />
		<h1><bean:message key="label.title.uplXmlFile.edit"/></h1> 
	
		<%-- include Error display --%>
		<tiles:insert definition="Error" />
		
			<html:form action="/editUplXmlFile" method="post">
			  <table class="datatable">
				<col style="width:16%"/>
				<col style="width:84%"/>
				<tr>
  				  <th scope="row" class="scope-row">
			        <bean:message key="label.uplXmlFile.xmlfile"/>:
			      </th>
			      <td>
						<a  href="<bean:write name="EditUplXmlFileForm" property="xmlfile" />" title="<bean:write name="EditUplXmlFileForm" property="xmlfile" />">						
							<bean:write name="EditUplXmlFileForm" property="xmlfile" />
						</a>&#160;		        	  		        
						<html:hidden  property="xmlfileId" />
			      </td>
			    </tr>
			    <tr>
  				  <th scope="row" class="scope-row">
			        <bean:message key="label.uplXmlFile.title"/>:
			      </th>
			      <td>
			        <html:textarea property="title"  rows="3" cols="30" style="width:400px"/>
			      </td>
			    </tr>
			    <tr>
  				  <th scope="row" class="scope-row">
			        <bean:message key="label.lastmodified"/>:
			      </th>
			      <td>
					<logic:notEqual name="fileExists" value=""  name="EditUplXmlFileForm" property="lastModified" >
						<bean:write name="EditUplXmlFileForm" property="lastModified" />
					</logic:notEqual>
					<logic:equal name="fileNotExists" value=""  name="EditUplXmlFileForm" property="lastModified" >
						<bean:message key="label.fileNotFound"/>
					</logic:equal>
			      </td>
			    </tr>
			    <tr>
			      <td colspan="2">&nbsp;</td>
			    </tr>
			    <tr>
			      <td colspan="2" align="center">
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
