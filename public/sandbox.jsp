<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="eionet.gdem.Constants, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices"%>
<%
	DbModuleIF dbM= GDEMServices.getDbModule();
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>XQuery Sandbox</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
    <script type="text/javascript" src="util.js"></script>
    
</head>
<body>

<jsp:include page="location.jsp" flush='true'>
	<jsp:param name="name" value="XQuery Sandbox"/>
</jsp:include>
<%@ include file="menu.jsp" %>
<div id="workarea">
<h1>XQuery Sandbox</h1>
<div id="main_table">
	<table border="0" cellspacing="1" cellpadding="2" width="100%"><tr><td>
	<form name="f" action="sandbox" method="post">
		<textarea name="XQSCRIPT" align="left" rows="25" style="width:99%" wrap="soft"></textarea>
		<br/>
		<input type="submit" value=" GO " />
	</form>
	</td></tr></table>
</div>
</div>
<%@ include file="footer.jsp" %>
</body>
</html>
