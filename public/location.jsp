<%@ page import="eionet.gdem.conversion.ssr.Names" %>
<%
		String display_name =(String)getServletConfig().getServletContext().getServletContextName();
%>
<div id="pagehead">
  <div id="identification">
    <a href="/"><img src="images/logo.png" alt="Logo" id="logo" border="0" /></a>
    <div class="sitetitle">GDEM Services</div>
    <div class="sitetagline"><%=display_name%></div>
  </div>
  <div class="breadcrumbtrail">
   <div class="breadcrumbhead">You are here:</div>
   <div class="breadcrumbitem"><a href="http://www.eionet.eu.int">EIONET</a></div>

  <%
     String oHName=request.getParameter("name");
     if (oHName==null) {  %>
   <div class="breadcrumbitemlast">GDEM Services</div>
  <% } %>
  <%  if (oHName!=null) { %>
   <div class="breadcrumbitem"><a href='main'>GDEM Services</a></div>
   <div class="breadcrumbitemlast"><%=oHName%></div>
  <% } %>
   <div class="breadcrumbtail"></div>
  </div>
</div> <!-- pagehead -->
