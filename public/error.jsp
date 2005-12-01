<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ page import="java.io.PrintWriter"%>
<%


	
%>
<ed:breadcrumbs-push label="XML Services error" level="1" />
<tiles:insert definition="TmpHeader"/>

	<%@ include file="menu.jsp" %>

	<div id="workarea">
		<h1>Error page!</h1>
		<% if (err!= null) { %>
			<div id="errormessage" class="error"><%=err%></div>
		<% } %>
		<%
    	Exception e=(Exception)session.getAttribute("gdem.exception");
    	String message="";
    	if (e==null){
    		message="unknown error";
    	}
    	else{
				//message=e.toString();
				message = e.getMessage();
				if (message!=null && message.length()>0) {
			}
			%>
			<table cellpadding="0" cellspacing="0" border="0" align="center">
				  <tr>
					    <td>			
							<div class="error">
							<span id="errormessage">
									<%=message%>
							</span>
							</div>
						</td>
					</tr>
			</table>
			<br/><br/>
			<!--  <div class="sub_title">Stack Trace:</div><br>-->
	    <!--<pre><%// e.printStackTrace(new PrintWriter(out)); %></pre>-->
	    
			<%}%>
	</div>
<tiles:insert definition="TmpFooter"/>