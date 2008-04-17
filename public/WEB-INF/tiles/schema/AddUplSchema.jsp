<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

		<ed:breadcrumbs-push label="Upload Schema" level="1" />
		<h1><bean:message key="label.title.uplSchema.add"/></h1> 

		<%-- include Error display --%>
		<tiles:insert definition="Error" />

			<html:form action="/addUplSchema" method="post" enctype="multipart/form-data">
			  <table class="datatable">
				<col style="width:16%"/>
				<col style="width:84%"/>
				<tr>
  				  <th scope="row" class="scope-row">
			        <bean:message key="label.uplSchema.schemaFile"/>:
			      </th>
			      <td>
			        <html:file property="schema"  />
			      </td>
			    </tr>
			    <tr>
  				  <th scope="row" class="scope-row">
			        <bean:message key="label.uplSchema.url"/>:
			      </th>
			      <td>
			        <html:text property="schemaUrl" style="width:400px"/>
			      </td>
			    </tr>
			    <tr>
  				  <th scope="row" class="scope-row">
			        <bean:message key="label.uplSchema.description"/>:
			      </th>
			      <td>
			        <html:textarea property="description"  rows="3" cols="30" style="width:400px"/>
			      </td>
			    </tr>
			    <tr>
			      <td colspan="2">&nbsp;</td>
			    </tr>
			    <tr>
			      <td colspan="2" align="center">
			        <html:submit styleClass="button">
			        	<bean:message key="label.uplSchema.upload"/>
			        </html:submit>
			        <html:cancel styleClass="button">
			        	<bean:message key="label.cancel"/>
			        </html:cancel>		        
			      </td>
			    </tr>
			  </table>
			</html:form>		
