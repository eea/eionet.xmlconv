<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names,java.util.Vector,java.util.Hashtable"%>
<%

		DbModuleIF dbM= GDEMServices.getDbModule();

%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Hosts</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
    <script type="text/javascript" src="util.js"></script>
    
</head>
<body>

<jsp:include page="location.jsp" flush='true'>
<jsp:param name="name" value="Hosts"/>
</jsp:include>
<%@ include file="menu.jsp" %>
<div id="workarea">
		<%
				Vector list=null;
				if (hovPrm)
					list = dbM.getHosts(null);
				else
					err="You are not allowed to view host information";
			
				if (list==null) list=new Vector();
  	
				boolean hodPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_HOST_PATH, "d");
				boolean hoiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_HOST_PATH, "i");
				boolean houPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_HOST_PATH, "u");
		%>
		
		<% if (err!= null) { %>
			<h4><%=err%></h4>
		<% } %>
	
		<h1>Hosts</h1>
		<%

		
		%>		
		<div id="operations">
				<%
				
				if (hoiPrm){
				%>
				<ul>
					<li><a href="<%=Names.HOST_JSP%>" title="Add a new host">Add host</a></li>
 				</ul>
				<%}
				%>
		</div>
		<br/>
		<span>Conversion service is using the following credentials for accessing to defined hosts.</span>
		<br/>
		<div id="main_table">
		<table border="0" cellspacing="1" cellpadding="2" width="100%">
		<thead>
			<tr>
				<th width="60%">Host name</th>
				<th align="center">User name</th>
				<%
				if (hodPrm){
				%>
	 	  			<th align="center">&#160;</th>
				<%}
				%>    	     			
			</tr>
		</thead>
		<tbody>
		<%		
				for (int i=0; i<list.size(); i++){
					Hashtable host = (Hashtable)list.get(i);
					String host_id = (String)host.get("host_id");
					String host_name = (String)host.get("host_name");
					String user_n = (String)host.get("user_name");
					
    	   			%>
					<tr height="5">
						<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
       				<%
							if (houPrm){%>
								<a title="Host properties" href="<%=Names.HOST_JSP%>?ID=<%=host_id%>"><%=host_name%></a>
							<%}else{%>
								<%=host_name%>
							<%}%>
						</td>
						<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>><%=user_n%></td>
       			<td align="middle" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
	         				<%
							if (hodPrm){%>
								<img onclick="ho_<%=host_id%>.submit();" height="15" width="15" src="images/delete.png" title="Delete host credentials"></img>
							<%}%>
 	         	</td>
						<form name="ho_<%=host_id%>" action="main" method="post">
							<input type="hidden" name="ACTION" value="<%=Names.HOST_DEL_ACTION%>" />
							<input type="hidden" name="ID" value="<%=host_id%>" />
						</form>		
					</tr>
					<%
    	   		}
    	   	%>
				</tbody>
		 	</table>
		 	<br/>
		 	<br/>
	</div>					
	</div>
<%@ include file="footer.jsp" %>
</body>
</html>
