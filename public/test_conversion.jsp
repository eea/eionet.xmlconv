<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/loose.dtd">
<%@ page import="java.util.HashMap, eionet.gdem.db.DbModuleIF, eionet.gdem.db.DbUtils, eionet.gdem.ssr.Names" %>



<%
	//get stylesheet id from parameter
	String id = request.getParameter("ID");
    id = (id == null ? "" : id);
	
	

	DbModuleIF dbM= DbUtils.getDbModule();

	String schema_name=null;
	String output_type=null;
	String description=null;
	String xsl=null;

	if (!id.equals("")){
		HashMap ss = dbM.getStylesheetInfo(id);
		if (ss==null){
			%>
			<b>Couldn't find XML schema!</b> <%
			return;
		}
		xsl = (String)ss.get("xsl");
		output_type=(String)ss.get("content_type_out");
		description=(String)ss.get("description");
		schema_name=(String)ss.get("xml_schema");
	}
	
%>
<html lang=en>
<head>
	<title>Stylesheets</title>
   	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <link type="text/css" rel="stylesheet" href="eionet.css">
	<script language="JavaScript" src="util.js"></script>
	<script language="JavaScript">
		
		detectBrowser();	
	
		function convert(){
			var file = document.forms["TestConversion"].elements["XML_FILE"].value;
			if (file == ""){
				alert("File location is not specified!");
				return;
			}
		
			sUrl = "main?ACTION=<%=Names.EXECUTE_TESTCONVERSION_ACTION%>&format=<%=id%>&url=" + file;
			//alert(sUrl);
			window.open(sUrl,"conversion","height=600,width=800,status=yes,toolbar=yes,scrollbars=yes,resizable=yes,menubar=yes,location=yes");
		}

			
	</script>

</head>
<body>

<%@ include file="header.jsp" %>


<table cellSpacing="0" cellPadding="0" border="0">
  <tbody>
  <tr valign="top">
	<td nowrap="true" width="130">
      	<p><center>
  	      <%@ include file="menu.jsp" %>
        </center></P>
	</td>
    <td width="100%">
	   <jsp:include page="location.jsp" flush='true'>
          <jsp:param name="name" value="Test conversion"/>
       </jsp:include>

      <div style="MARGIN-LEFT: 13px">

	  <br/>

	  <% if (err!= null) { %>
 	  	<h4><%=err%></h4>
	  <% } %>
  	  
	  <h2>Test conversion</h2>

	  <br/>	

		<form name="TestConversion" action="main" method="POST">
				
			<table cellspacing="0">
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>XML Schema</b>&#160;</span>
					<td align="left">	
						<a target="blank" href="<%=schema_name%>"><%=schema_name%></a>
					</td>
				</tr>
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>Output type</b>&#160;</span>
					<td align="left">	
						<%=output_type%>
					</td>
				</tr>
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>Description</b>&#160;</span>
					<td align="left">	
						<%=description%>
					</td>
				</tr>
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>XSL File</b>&#160;</span>
					<td align="left">	
						<a target="blank" href="<%=Names.XSL_FOLDER%><%=xsl%>"><%=xsl%></a>
					</td>
				</tr>
				<tr>
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>Source XML File URL</b>&#160;</span>
					</td>
					<td align="left">
						<input type="text" class="smalltext" name="XML_FILE" size="53"/>
					</td>
				</tr>
				<tr height="10"><td colspan="2"></td></tr>
				<%
					boolean convPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_TESTCONVERSION_PATH, "x");
					if (convPrm){
						%>
						<tr>
							<td></td>
							<td align="left">
								<input name="TEST" type="button" class="mediumbuttonb" value="Convert" onclick="convert()"></input>&#160;&#160;
							</td>
						</tr>
					<%}%>
			</table>
			<input type="hidden" name="ACTION" value="<%=Names.SHOW_TESTCONVERSION_ACTION%>"/>
		</form>	

		 
		</div>
	  </td>
	</tr>
	</tbody>
</table>

</body>
</html>
