<%@ page import="eionet.gdem.conversion.ssr.Names" %>
<div id="pagehead">
  <div id="identification">
    <a href="/"><img src="images/logo.png" alt="Logo" id="logo" border="0" /></a>
    <div class="sitetitle">GDEM Stylesheet Repository</div>
    <div class="sitetagline">Ver 1.0  beta.</div>
  </div>
  <div class="breadcrumbtrail">
   <div class="breadcrumbhead">You are here:</div>
   <div class="breadcrumbitem"><a href="http://www.eionet.eu.int">EIONET</a></div>

  <%
     String oHName=request.getParameter("name");
     if (oHName==null) {  %>
   <div class="breadcrumbitemlast">Stylesheet Repository</div>
  <% } %>
  <%  if (oHName!=null) { %>
   <div class="breadcrumbitem"><a href='main'>Stylesheet Repository</a></div>
   <div class="breadcrumbitemlast"><%=oHName%></div>
  <% } %>
   <div class="breadcrumbtail"></div>
  </div>
</div> <!-- pagehead -->
