<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.io.PrintWriter"%>
<%


	
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>XML Services</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
    <script type="text/javascript" src="util.js"></script>

</head>
<body>
	<div id="pagehead">
		<%@ include file="header.jsp" %>

   	<jsp:include page="location.jsp" flush='true'>
       	<jsp:param name="name" value="XML Services error"/>
   	</jsp:include>
	</div>
	<%@ include file="menu.jsp" %>

	<div id="workarea">
		<h1>Error page!</h1>
		<% if (err!= null) { %>
			<div id="errormessage"><%=err%></div>
		<% } %>
		<%
    	Exception e=(Exception)session.getAttribute("gdem.exception");
    	String message="";
    	if (e==null){
    		message="unknown error";
    	}
    	else{
				message=e.toString();
				if (message!=null && message.length()>0) {
			}
			%>
			<span id="errormessage">
					Message:<br>
					<%=message%>
			</span>
			<br/><br/>
			<div class="sub_title">Stack Trace:</div><br>
	    <pre><% e.printStackTrace(new PrintWriter(out)); %></pre>
			<%}%>
	</div>
<%@ include file="footer.jsp" %>
</body>
</html>
