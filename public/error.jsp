<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ page import="java.io.PrintWriter"%>
<%


	
%>
<ed:breadcrumbs-push label="XML Services error" level="1" />
<tiles:insert definition="MainHeader"/>

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
<tiles:insert definition="MainFooter"/>