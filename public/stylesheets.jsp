
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
<%@ page import="java.util.HashMap, java.util.Vector, eionet.gdem.db.DbModuleIF, eionet.gdem.db.DbUtils, eionet.gdem.ssr.Names" %>

<%!private HashMap schema=null;%>


<%
	//get schema id from parameter
	String id = (String)request.getParameter(Names.SCHEMA_ID);
	if (id == null || id.length()==0){ 
		id = (String)request.getAttribute(Names.SCHEMA_ID); //if stylesheet is added
		if (id == null || id.length()==0){ 
			id="0";
		}
		
	}
	DbModuleIF dbM= DbUtils.getDbModule();

	Vector list = dbM.getSchemas(id);
	if (list==null) list=new Vector();
	
	String name = "";
	String schema_desc = null;
	Vector stylesheets = null;

	if (list.size()>0){
	
		schema = (HashMap)list.get(0);
		name = (String)schema.get("xml_schema");
		schema_desc = (String)schema.get("description");
		stylesheets = (Vector)schema.get("stylesheets");
	}
	if (stylesheets==null) stylesheets=new Vector();
%>
<HTML lang=en><HEAD><TITLE>Stylesheets</TITLE>

<META http-equiv=Content-Type content="text/html; charset=UTF-8"><LINK href="eionet.css" type=text/css rel=stylesheet>
<SCRIPT language=JavaScript>
	
	function openPage(action) {
		document.forms["f"].ACTION.value=action;
		document.forms["f"].submit();
	}
	function openApp(appName) {
		document.forms["f"].app.value=appName;
		document.forms["f"].ACTION.value="";
		document.forms["f"].ACL.value="/";
		document.forms["f"].submit();
	}
	function doLogin(appName) {
		window.open("appLogin.jsp?app=" + appName,"login","height=200,width=300,status=no,toolbar=no,scrollbars=no,resizable=no,menubar=no,location=no");
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
          <jsp:param name="name" value="Stylesheets"/>
       </jsp:include>

      <DIV style="MARGIN-LEFT: 13px">

	  <BR>
		<table width=618>
	  		<tr valign="TOP">
				<td>
					<% if (err!= null) { %>
						<h2><FONT color="#FF0000"><%=err%></FONT></h2>
					<% } %>
					<h2>Stylesheets of <%=name%></h2>
				</td>
				<%
				boolean ssiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "i");
				if (ssiPrm){%>
					<td align="right">
						<FORM name="add_stylesheet" action="main" method="POST">
							<INPUT TYPE=hidden NAME="ID" value=<%=id%>></INPUT><IMG onClick="add_stylesheet.submit();" height=18 width=38 src="images/add.png" alt="Add new stylesheet">
							<INPUT TYPE=hidden NAME="ACTION" value="<%=Names.SHOW_ADDXSL_ACTION%>"></INPUT>
						</FORM>
					<td>
				<%}%>
			</tr>
		</table>
		<BR>

	    <TABLE cellSpacing=5 width=615>
    	   	<TBODY>
					
    	   	<%
				boolean ssdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "d");
				boolean convPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_TESTCONVERSION_PATH, "x");
			%>
        		<TR>
			  		<TD align=middle width=40 bgColor=#646666 style="padding-left:5;padding-right:10"><SPAN class=head0 ><FONT   color=#ffffff>Type</FONT></SPAN></TD>
          			<TD align=left width=355 bgColor=#646666 style="padding-left:5;padding-right:10"><SPAN class=head0 ><FONT   color=#ffffff>Description</FONT></SPAN></TD>
          			<TD align=left width=200 bgColor=#646666 style="padding-left:5;padding-right:10"><SPAN class=head0 ><FONT   color=#ffffff>Stylesheet</FONT></SPAN></TD>
       				<%
					if (ssdPrm){%>
    	     			<TD align=middle bgColor=#646666>&#160;</TD>
					<%}%>    	     			
				</TR>
    	   	<%

				for (int i=0; i<stylesheets.size(); i++){
					HashMap hash = (HashMap)stylesheets.get(i);
					String convert_id = (String)hash.get("convert_id");
					String xsl = (String)hash.get("xsl");
					String type = (String)hash.get("content_type_out");
    	   			String description = (String)hash.get("description");
    	   			%>
					<TR height=5>
						<TD align="middle" style="padding-left:5;padding-right:10" <% if (i % 2 != 0) %> bgcolor="#D3D3D3" <%;%>>
	         				<%
							if (convPrm){%>
								<a title="Test conversion" href="main?ACTION=<%=Names.SHOW_TESTCONVERSION_ACTION%>&ID=<%=convert_id%>"><%=type%></a>
							<%}else{%>
								<%=type%>
							<%}%>
						</TD>
						<TD align="left" style="padding-left:5;padding-right:10" <% if (i % 2 != 0) %> bgcolor="#D3D3D3" <%;%>><%=description%></TD>
						<TD align="left" style="padding-left:5;padding-right:10" <% if (i % 2 != 0) %> bgcolor="#D3D3D3" <%;%>><a target="blank" href="<%=Names.XSL_FOLDER%><%=xsl%>"><%=xsl%></a></TD>
 	         			<TD align=middle>
	         				<%
							if (ssdPrm){%>
								<img onClick="ss_<%=convert_id%>.submit();" height=15 width=15 src="images/delete.png" title="Delete stylesheet"></img>
							<%}%>
 	         			</TD>
						<FORM name="ss_<%=convert_id%>" ACTION="main" method="POST">
							<INPUT type="hidden" name="ACTION" value="<%=Names.XSL_DEL_ACTION%>"></INPUT>
							<INPUT type="hidden" name="XSL_DEL_ID" value="<%=convert_id%>"></INPUT>
							<INPUT type="hidden" name="ID" value="<%=id%>"></INPUT>
						</FORM>		
					</TR>
					<%
    	   		}
    	   	%>
			</TBODY>
		 </TABLE>
		 
	</DIV></TD></TR></TBODY></TABLE>

<FORM name="f" ACTION="main" METHOD="POST">
	<INPUT TYPE="hidden" name="ACTION" value=""/>
	<INPUT TYPE="hidden" name="PARAM" value=""/>
</FORM>

</BODY></HTML>
