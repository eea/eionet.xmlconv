<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/loose.dtd">
<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, eionet.gdem.db.DbModuleIF, eionet.gdem.db.DbUtils, eionet.gdem.ssr.Names, eionet.gdem.ssr.SecurityUtil,com.tee.uit.security.AppUser" %>

<%
	//AppUser user = SecurityUtil.getUser(request);
	
	
	DbModuleIF dbM= DbUtils.getDbModule();
	Vector list = dbM.getSchemas(null);
	if (list==null) list=new Vector();
	
%>
<html lang=en>
<head>
	<title>Stylesheets</title>
   	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <link type="text/css" rel="stylesheet" href="eionet.css">
	<script language="JavaScript" src="util.js"></script>
	<script language="JavaScript">
	
		detectBrowser();	

		function openPage(action) {
			document.forms["f"].ACTION.value=action;
			document.forms["f"].submit();
		}
		function openXSD(action, id) {
			document.forms["f"].ACTION.value=action;
			document.forms["f"].ID.value=id;
			document.forms["f"].submit();
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
          <jsp:param name="name" value="Conversions"/>
        </jsp:include>

      
    	<div style="MARGIN-LEFT: 13px">
		<br/>
	  
		<% if (err!= null) { %>
			<h4><%=err%></h4>
	  	<% } %>
			<h2>Conversions</h2>
		<%
		boolean ssiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "i");
		if (ssiPrm){
		%>
			<div style="float:right">
				<img onClick="add_stylesheet.submit();" height="18" width="38" src="images/add.png" alt="Add new Stylesheet">
				<form name="add_stylesheet" action="main" method="POST">
					<input type="hidden" name="ID"></input>
					<input type="hidden" name="ACTION" value="<%=Names.SHOW_ADDXSL_ACTION%>"></input>
				</form>
			</div>
		<%}%>
		<br/>
		
		<table cellSpacing="5">
          <thead>
            <%
			boolean ssdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "d");
			%>
            <tr>
		      <th align="left" width="*">XML Schema</th>
              <th align="left" width="200">Stylesheets</th>
			  <%
				if (ssdPrm){%>
     			  <th align="middle" width="15">&#160;</th>
     		  <%}%>
		    </tr>
		   </thead>
		   <tbody>
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
 	         			<td align="middle">
 	         				<%
							if (ssdPrm){%>
								<img onClick="ss_<%=id%>.submit();" height="15" width="15" src="images/delete.png" title="Delete schema and all it's stylesheets"></img>
							<%}%>
 	         			</td>
						<form name="ss_<%=id%>" action="main" method="POST">
							<input type="hidden" name="ACTION" value="<%=Names.XSD_DEL_ACTION%>"></input>
							<input type="hidden" name="XSD_DEL_ID" value="<%=id%>"></input>
						</form>
					</tr>
					<%

				}
				%>
			</tbody>
		</table>

	</div></td></tr></tbody></table>

	<form name="f" action="main" method="GET">
		<input type="hidden" name="ACTION" value=""/>
		<input type="hidden" name="ID" value=""/>
	</form>

	</body>
</html>
