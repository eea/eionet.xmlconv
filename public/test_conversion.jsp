
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
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
<HTML lang=en><HEAD><TITLE>Stylesheets</TITLE>

<META http-equiv=Content-Type content="text/html; charset=UTF-8"><LINK href="eionet.css" type=text/css rel=stylesheet>
<SCRIPT language=JavaScript>
	function convert(){
		var file = document.forms["TestConversion"].elements["XML_FILE"].value;
		if (file == ""){
			alert("File location is not specified!");
			return;
		}
		
		sUrl = "main?ACTION=<%=Names.EXECUTE_TESTCONVERSION_ACTION%>&format=<%=id%>&url=" + file;
		window.open(sUrl,"conversion","height=600,width=800,status=yes,toolbar=yes,scrollbars=yes,resizable=yes,menubar=yes,location=yes");
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
          <jsp:param name="name" value="Test conversion"/>
       </jsp:include>

      <DIV style="MARGIN-LEFT: 13px">

	  <BR>
		<table width=618>
	  		<tr valign="TOP">
				<td>
					<% if (err!= null) { %>
						<h2><FONT color="#FF0000"><%=err%></FONT></h2>
					<% } %>
					<h2>Test conversion</h2>
				</td>
				<td align="right"><td>
			</tr>
		</table>
		<BR>

		<FORM NAME="TestConversion" ACTION="main" METHOD="POST">
				
			<table width="auto" cellspacing="0">
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
			<INPUT TYPE="hidden" name="ACTION" value="<%=Names.SHOW_TESTCONVERSION_ACTION%>"/>
		</FORM>	

		 
	</DIV></TD></TR></TBODY></TABLE>

</BODY></HTML>
