<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/loose.dtd">
<%@ page import="java.util.HashMap, java.util.Vector, eionet.gdem.db.DbModuleIF, eionet.gdem.db.DbUtils, eionet.gdem.ssr.Names" %>



<%
	//get schema from parameter
	String id = request.getParameter("ID");
    id = (id == null ? "" : id);
	
	

	DbModuleIF dbM= DbUtils.getDbModule();

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
	
%>
<html lang="en">
<head>
	<title>Stylesheets</title>
   	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <link type="text/css" rel="stylesheet" href="eionet.css">
	<script language="JavaScript" src="util.js"></script>
	<script language="JavaScript">
		
		detectBrowser();	
	
		function submitForm(){

		
			var file = document.forms["Upload"].elements["FILE_INPUT"].value;
			var schema = document.forms["Upload"].elements["SCHEMA"].value;
			var description = document.forms["Upload"].elements["DESCRIPTION"].value;
			var content_type = document.forms["Upload"].elements["CONTENT_TYPE"].value;
			var action = document.forms["Upload"].elements["ACTION"].value;
			var ok = true;

		
			if (file == ""){
				alert("File location is not specified!");
				ok = false;
			}
		
	
			if (ok == true){
			
				var qryStr = "?SCHEMA=" + schema + "&DESCRIPTION=" + description + "&CONTENT_TYPE=" + content_type + "&ACTION=" + action;
			
				document.forms["Upload"].action = document.forms["Upload"].action + qryStr;
				//alert(document.forms["Upload"].action);
				document.forms["Upload"].submit();
			}
		}
		
	</script>
</head>
<body>

<%@ include file="header.jsp" %>


<table cellSpacing="0" cellPadding="0" border="0">
  <tr valign="top">
   	<td nowrap="true" width="130">
      	<p><center>
  	      <%@ include file="menu.jsp" %>
        </center></P>
    </td>
    <td width="100%">
	   	<jsp:include page="location.jsp" flush='true'>
        	<jsp:param name="name" value="Add stylesheet"/>
       	</jsp:include>

		<div style="MARGIN-LEFT: 13px">

	   <br/>
		<% if (err!= null) { %>
			<h2><font color="#FF0000"><%=err%></font></h2>
		<% } %>
			<h2>Add a new stylesheet</h2>
		<br>

		<form name="Upload" action="main" method="POST" enctype="multipart/form-data">
				
			<table cellSpacing="0">
				<tr valign="top">
					<td align="left">	
						<span class="smallfont"><b>XML Schema</b>&#160;
					</td>
					<td align="left">	
						<input type="text" class="smalltext" size="64" name="SCHEMA" value="<% if (schema_name != null) %><%=schema_name%><%;%>"></input>
					</td>
				</tr>
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>Output type</b>&#160;</span>
					</td>
					<td align="left">	
						<select class="small" name="CONTENT_TYPE">
							<option value="HTML">HTML</option>
							<option value="PDF">PDF</option>
							<option value="EXCEL">EXCEL</option>
							<option value="XML">XML</option>
						</select>
					</td>
				</tr>
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>Description</b>&#160;</span>
					<td align="left">	
						<textarea class="small" rows="2" cols="55" name="DESCRIPTION"></textarea>
					</td>
				</tr>
				<tr>
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>XSL File</b>&#160;</span>
					</td>
					<td align="left">
						<input type="file" class="smalltext" name="FILE_INPUT" size="53"/>
					</td>
				</tr>
				<tr height="10"><td colspan="2"></td></tr>
				<tr>
					<td></td>
					<td align="left">
						<input name="SUBMIT" type="button" class="mediumbuttonb" value="Upload" onclick="submitForm()"></input>&#160;&#160;
						<input name="RESET" type="reset" class="mediumbuttonb" value="Clear"></input>
					</td>
				</tr>
			</table>
			<input TYPE="hidden" name="ACTION" value="<%=Names.XSL_ADD_ACTION%>"/>
		</form>	

		 
	</div></td></tr></tbody></table>
<form name="f" action="main" method="POST">
	<input type="hidden" name="ACTION" value=""/>
	<input type="hidden" name="PARAM" value=""/>
</form>

</body></html>
