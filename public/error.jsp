<%@page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ page import="java.io.PrintWriter"%>

<ed:breadcrumbs-push label="XML Services error" level="1" />

<tiles:insert definition="TmpHeader"/>

	<%@ include file="menu.jsp" %>

	<h1>Error page!</h1>
	
	<%
		if (err!= null) {
			%>
			<div id="errormessage" class="error"><%=err%></div><%
		}
		
		Exception e = (Exception)session.getAttribute("gdem.exception");
		String message = "unknown error";
		if (e!=null && e.getMessage()!=null)
			message = e.getMessage();
		else if (e!=null)
			message = e.toString();
	%>
	
	<div class="error-msg">
		<%=message%>
	</div>
	
<tiles:insert definition="TmpFooter"/>