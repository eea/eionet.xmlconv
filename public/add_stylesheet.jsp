
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
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
<HTML lang=en><HEAD><TITLE>Stylesheets</TITLE>

<META http-equiv=Content-Type content="text/html; charset=UTF-8"><LINK href="eionet.css" type=text/css rel=stylesheet>
<SCRIPT language=JavaScript>
	
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

				
</SCRIPT>

<META content="MSHTML 5.50.4522.1800" name=GENERATOR></HEAD>
<BODY bgColor=#f0f0f0 leftMargin=0 topMargin=0 marginheight="0" marginwidth="0">

<%@ include file="header.jsp" %>


<TABLE cellSpacing=0 cellPadding=0 border=0 background=images/eionet_background.jpg>
  <TBODY>
  <TR vAlign=top height=500>
    <TD noWrap width=130>
      <P>
      <CENTER>		
		<%@ include file="menu.jsp" %>
	  </CENTER>
      <P></P>
      <P></P></TD>
    <TD>
	   <jsp:include page="location.jsp" flush='true'>
          <jsp:param name="name" value="Add stylesheet"/>
       </jsp:include>

      <DIV style="MARGIN-LEFT: 13px">

	  <BR>
		<table width=618>
	  		<tr valign="TOP">
				<td>
					<% if (err!= null) { %>
						<h2><FONT color="#FF0000"><%=err%></FONT></h2>
					<% } %>
					<h2>Add a new stylesheets</h2>
				</td>
				<td align="right"><td>
			</tr>
		</table>
		<BR>

		<FORM NAME="Upload" ACTION="main" METHOD="POST" ENCTYPE="multipart/form-data">
				
			<table width="auto" cellspacing="0">
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>XML Schema</b>&#160;</span>
					<td align="left">	
						<input type="text" class="smalltext" size="64" name="SCHEMA" value="<% if (schema_name != null) %><%=schema_name%><%;%>"></input>
					</td>
				</tr>
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>Output type</b>&#160;</span>
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
			<INPUT TYPE="hidden" name="ACTION" value="<%=Names.XSL_ADD_ACTION%>"/>
		</FORM>	

		 
	</DIV></TD></TR></TBODY></TABLE>
<FORM name="f" ACTION="main" METHOD="POST">
	<INPUT TYPE="hidden" name="ACTION" value=""/>
	<INPUT TYPE="hidden" name="PARAM" value=""/>
</FORM>

</BODY></HTML>
