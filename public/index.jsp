
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, eionet.gdem.db.DbModuleIF, eionet.gdem.db.DbUtils, eionet.gdem.ssr.Names, eionet.gdem.ssr.SecurityUtil,com.tee.uit.security.AppUser" %>

<%
	//AppUser user = SecurityUtil.getUser(request);
	
	
	DbModuleIF dbM= DbUtils.getDbModule();
	Vector list = dbM.getSchemas(null);
	if (list==null) list=new Vector();
	
%>
<HTML lang=en><HEAD><TITLE>Stylesheets</TITLE>

<META http-equiv=Content-Type content="text/html; charset=UTF-8"><LINK href="eionet.css" type=text/css rel=stylesheet>
<SCRIPT language=JavaScript>
	
	function openPage(action) {
		document.forms["f"].ACTION.value=action;
		document.forms["f"].submit();
	}
	function openXSD(action, id) {
		document.forms["f"].ACTION.value=action;
		document.forms["f"].ID.value=id;
		document.forms["f"].submit();
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
      <P></P>
    </TD>
    <TD>
	    <jsp:include page="location.jsp" flush='true'>
          <jsp:param name="name" value="Conversions"/>
        </jsp:include>

      
      <DIV style="MARGIN-LEFT: 13px">

	  <BR>

	    <table width=615>
		  <tr valign="TOP">
			<td>
				<% if (err!= null) { %>
					<h2><FONT color="#FF0000"><%=err%></FONT></h2>
				<% } %>
				<h2>Conversions</h2>
			</td>
			<%
			boolean ssiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "i");
			if (ssiPrm){
				%><td align="right">
					<FORM name="add_stylesheet" action="main" method="POST">
						<INPUT TYPE=hidden NAME="ID"></INPUT><IMG onClick="add_stylesheet.submit();" height=18 width=38 src="images/add.png" alt="Add new Stylesheet">
						<INPUT TYPE=hidden NAME="ACTION" value="<%=Names.SHOW_ADDXSL_ACTION%>"></INPUT>
					</FORM>
				<td>
			<%}%>
		  </tr>
		</table>
		<br>
		<TABLE cellSpacing=5 width=615>
          <TBODY>
            <%
			boolean ssdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "d");
			%>
            <TR>
		      <TD align=left width=* bgColor=#646666><SPAN class=head0 ><FONT   color=#ffffff>XML Schema</FONT></SPAN></TD>
              <TD align=left width=200 bgColor=#646666><SPAN class=head0 ><FONT   color=#ffffff>Stylesheets</FONT></SPAN></TD>
			  <%
				if (ssdPrm){%>
     			  <TD align=middle bgColor=#646666>&#160;</TD>
     		  <%}%>
		    </TR>
				<%
						
						
				for (int i=0; i<list.size(); i++){
					HashMap schema = (HashMap)list.get(i);
					String name = (String)schema.get("xml_schema");
					String id = (String)schema.get("schema_id");
					String schema_descr = (String)schema.get("description");
					if (schema_descr==null) schema_descr="";
					if (!schema.containsKey("stylesheets")) continue;
					Vector stylesheets = (Vector)schema.get("stylesheets");
						
					%>
					<tr>
						<td align="left" style="padding-left:5;padding-right:10" <% if (i % 2 != 0) %> bgcolor="#D3D3D3" <%;%>>
							<a href="javascript:openXSD('<%=Names.SHOW_STYLESHEETS_ACTION%>', <%=id%>)" title="<%=schema_descr%>"><%=name%></a>
						</td>
						<td align="left" style="padding-left:5;padding-right:10" <% if (i % 2 != 0) %> bgcolor="#D3D3D3" <%;%>>
						<%
						for (int j=0; j<stylesheets.size(); j++){
							HashMap stylesheet = (HashMap)stylesheets.get(j);
							String xsl = (String)stylesheet.get("xsl");
							String type = (String)stylesheet.get("content_type_out");
							String xsl_descr = (String)stylesheet.get("description");
							
							if (j>0) %>,&#160;<%
							%><a target="blank" href="<%=Names.XSL_FOLDER%><%=xsl%>" title="<%=xsl_descr%>"><%=type%></a><%
							
						}
						%>
						</td>
 	         			<TD align=middle>
 	         				<%
							if (ssdPrm){%>
								<img onClick="ss_<%=id%>.submit();" height=15 width=15 src="images/delete.png" title="Delete schema and all it's stylesheets"></img>
							<%}%>
 	         			</TD>
						<FORM name="ss_<%=id%>" ACTION="main" method="POST">
							<INPUT type="hidden" name="ACTION" value="<%=Names.XSD_DEL_ACTION%>"></INPUT>
							<INPUT type="hidden" name="XSD_DEL_ID" value="<%=id%>"></INPUT>
						</FORM>		
					</tr>
					<%

				}
				%>
			</TBODY>
		</TABLE>



</DIV></TD></TR></TBODY></TABLE>

<FORM name="f" ACTION="main" METHOD="GET">
	<INPUT TYPE="hidden" name="ACTION" value=""/>
	<INPUT TYPE="hidden" name="ID" value=""/>
</FORM>

</BODY></HTML>
