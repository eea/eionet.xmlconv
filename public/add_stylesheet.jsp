<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Stylesheets</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />

    <script type="text/javascript" src="util.js"></script>
</head>
<body>

<jsp:include page="location.jsp" flush='true'>
<jsp:param name="name" value="Add stylesheet"/>
</jsp:include>
    <%@ include file="menu.jsp" %>
<div id="workarea">

			<% if (err!=null){
				%>
				<span id="errormessage"><%=err%></span>
			<%
			}
			%>
			<h1>Add a new stylesheet</h1>

		<form name="Upload" action="main?ACTION=<%=Names.XSL_ADD_ACTION%>" method="post" enctype="multipart/form-data">
				
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
						<label for="contenttypefield">Output type</label>
					</td>
					<td align="left">	
						<select class="small" name="CONTENT_TYPE" id="contenttypefield" title="content type of resilt file">
							<%
							for (int j=0;j<convTypes.size();j++){
								Hashtable convtype=(Hashtable)convTypes.get(j);
								String conv_type=(String)convtype.get("conv_type");
								%>
								<option value="<%=conv_type%>" <% if (conv_type.equals("HTML")) %>selected="selected"<% ; %>><%=conv_type%></option>
								<%
							}
							%>
							<!--option value="HTML">HTML</option>
							<option value="PDF">PDF</option>
							<option value="EXCEL">EXCEL</option>
							<option value="XML">XML</option>
							<option value="SQL">SQL</option-->
						</select>
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
				<tr>
					<td align="right" style="padding-right:5">
						<label for="filefield">XSL File</label>
					</td>
					<td align="left">
						<input type="file" class="textfield" name="FILE_INPUT" id="filefield" size="53"/>
					</td>
				</tr>
				<tr><td colspan="2"></td></tr>
				<tr>
					<td></td>
					<td align="left">
						<input name="SUBMIT" type="submit" class="mediumbuttonb" value="Upload"/>&#160;&#160;
						<input name="RESET" type="reset" class="mediumbuttonb" value="Clear" />
					</td>
				</tr>
			</table>
			<input type="hidden" name="ACTION" value="<%=Names.XSL_ADD_ACTION%>"/>
		</form>	

	</div>
<form name="f" action="main" method="post">
	<input type="hidden" name="ACTION" value=""/>
	<input type="hidden" name="PARAM" value=""/>
</form>
<%@ include file="footer.jsp" %>
</body></html>
