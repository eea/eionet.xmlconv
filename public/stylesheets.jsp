<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/loose.dtd">
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
		function openApp(appName) {
			document.forms["f"].app.value=appName;
			document.forms["f"].ACTION.value="";
			document.forms["f"].ACL.value="/";
			document.forms["f"].submit();
		}
		function doLogin(appName) {
			window.open("appLogin.jsp?app=" + appName,"login","height=200,width=300,status=no,toolbar=no,scrollbars=no,resizable=no,menubar=no,location=no");
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
        	<jsp:param name="name" value="Stylesheets"/>
        </jsp:include>

    	<div style="MARGIN-LEFT: 13px">

		<br/>
		
		<% if (err!= null) { %>
			<h4><%=err%></h4>
		<% } %>
	
		<h2>Stylesheets of <%=name%></h2>

		<%
		boolean ssiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "i");
		if (ssiPrm){%>
			<div style="float:right">
				<img onClick="add_stylesheet.submit();" height="18" width="38" src="images/add.png" alt="Add new stylesheet">
				<form name="add_stylesheet" action="main" method="POST">
					<input type="hidden" name="ID" value="<%=id%>"></input>
					<input type="hidden" name="ACTION" value="<%=Names.SHOW_ADDXSL_ACTION%>"></input>
				</form>
			</div>
		<%}%>
		
		<br/>

	    <table cellSpacing="5">
    	   	<head>
					
    	   	<%
				boolean ssdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "d");
				boolean convPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_TESTCONVERSION_PATH, "x");
			%>
        		<tr>
			  		<th align="middle" width="40">Type</th>
          			<th align="left" width="355"Description</th>
          			<th align="left" width="200"Stylesheet</th>
       				<%
					if (ssdPrm){%>
    	     			<th align="middle">&#160;</th>
					<%}%>    	     			
				</tr>
    	   	</thead>
    	   	<tbody>
    	   	<%
    	   	

				for (int i=0; i<stylesheets.size(); i++){
					HashMap hash = (HashMap)stylesheets.get(i);
					String convert_id = (String)hash.get("convert_id");
					String xsl = (String)hash.get("xsl");
					String type = (String)hash.get("content_type_out");
    	   			String description = (String)hash.get("description");
    	   			%>
					<tr height="5">
						<td align="middle" style="padding-left:5;padding-right:10" <% if (i % 2 != 0) %> bgcolor="#D3D3D3" <%;%>>
	         				<%
							if (convPrm){%>
								<a title="Test conversion" href="main?ACTION=<%=Names.SHOW_TESTCONVERSION_ACTION%>&amp;ID=<%=convert_id%>"><%=type%></a>
							<%}else{%>
								<%=type%>
							<%}%>
						</td>
						<td align="left" style="padding-left:5;padding-right:10" <% if (i % 2 != 0) %> bgcolor="#D3D3D3" <%;%>><%=description%></td>
						<td align="left" style="padding-left:5;padding-right:10" <% if (i % 2 != 0) %> bgcolor="#D3D3D3" <%;%>><a target="blank" href="<%=Names.XSL_FOLDER%><%=xsl%>"><%=xsl%></a></td>
 	         			<td align="middle">
	         				<%
							if (ssdPrm){%>
								<img onClick="ss_<%=convert_id%>.submit();" height="15" width="15" src="images/delete.png" title="Delete stylesheet"></img>
							<%}%>
 	         			</td>
						<form name="ss_<%=convert_id%>" action="main" method="POST">
							<input type="hidden" name="ACTION" value="<%=Names.XSL_DEL_ACTION%>"></input>
							<input type="hidden" name="XSL_DEL_ID" value="<%=convert_id%>"></input>
							<input type="hidden" name="ID" value="<%=id%>"></input>
						</form>		
					</tr>
					<%
    	   		}
    	   	%>
			</tbody>
		 </table>
		 
  	  </div>
    </td>
  </tr>
  </tbody>
</table>

<form name="f" action="main" method="POST">
	<input type="hidden" name="ACTION" value=""/>
	<input type="hidden" name="PARAM" value=""/>
</form>

</body>
</html>
