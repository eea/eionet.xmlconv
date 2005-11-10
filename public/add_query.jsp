<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ page import="java.util.HashMap, java.util.Vector, java.util.Hashtable, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names" %>



<%
	//get schema from parameter
	String id = request.getParameter("ID");
    id = (id == null ? "" : id);
	
	

	DbModuleIF dbM= GDEMServices.getDbModule();

	String schema_name=null;

	if (!id.equals("")){
		Vector list = dbM.getSchemas(id);
		if (list==null) list=new Vector();
		if (list.size()==0){%>
			<b>Couldn't find XML schema!</b> <%
			return;
		}
		
		HashMap schema = (HashMap)list.get(0);
		schema_name = (String)schema.get("xml_schema");
	}

	Vector convTypes = dbM.getConvTypes();
	if (convTypes==null) convTypes = new Vector();
	
%>
<ed:breadcrumbs-push label="Add query" level="1" />
<tiles:insert definition="MainHeader"/>



<%@ include file="menu.jsp" %>

<div id="workarea">

		<% if (err!= null) { %>
			<h1><font color="#FF0000"><%=err%></font></h1>
		<% } %>
			<h1>Add a new query</h1>

		<form name="Upload" action="main?ACTION=<%=Names.QUERY_ADD_ACTION%>" method="post" enctype="multipart/form-data">
				
			<table cellspacing="0">
				<tr valign="top">
					<td align="left">	
						<label for="schemafield">XML Schema</label>
					</td>
					<td align="left">	
						<input type="text" id="schemafield" class="textfield" size="64" name="SCHEMA" value="<% if (schema_name != null) %><%=schema_name%><%;%>" />
					</td>
				</tr>
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<label for="shortnamefield">Short Name</label>
					</td>
					<td align="left">	
						<input type="text" id="shortnamefield" class="textfield" size="64" name="SHORT_NAME" value="" />
					</td>
				</tr>
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<label for="descriptionfield">Description</label>
					</td>
					<td align="left">	
						<textarea class="small" rows="2" cols="55" name="DESCRIPTION" id="descriptionfield"></textarea>
					</td>
				</tr>
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<label for="contenttypefield">Content type</label>
					</td>
					<td align="left">	
						<select class="small" name="CONTENT_TYPE" id="contenttypefield" title="content type of resilt file">
							<option value="HTML">HTML</option>
							<option value="XML">XML</option>
							<option value="TXT">TEXT</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right" style="padding-right:5">
						<label for="filefield">XQuery File</label>
					</td>
					<td align="left">
						<input type="file" class="textfield" name="FILE_INPUT" id="filefield" size="53"/>
					</td>
				</tr>
				<tr><td colspan="2"></td></tr>
				<tr>
					<td></td>
					<td align="left">
						<input name="SUBMIT" type="submit" class="button" value="Upload"/>&#160;&#160;
						<input name="RESET" type="reset" class="button" value="Clear" />
					</td>
				</tr>
			</table>
			<input type="hidden" name="ACTION" value="<%=Names.QUERY_ADD_ACTION%>"/>
		</form>	

	</div>
<form name="f" action="main" method="post">
	<input type="hidden" name="ACTION" value=""/>
	<input type="hidden" name="PARAM" value=""/>
</form>
<tiles:insert definition="MainFooter"/>