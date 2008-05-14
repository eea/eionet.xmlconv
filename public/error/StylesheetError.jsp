<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
<head>
<title></title>
<style type="text/css" media="screen">
<!--
	@import url(<c:url value="../css/main.css"/>);
-->
</style>
<style type="text/css" media="screen">
<!--
	@import url(<c:url value="/css/portlet.css"/>);
-->
</style>
<style type="text/css">
body {
	margin: 0;
	padding: 0;
	background-color:#f0f0f0;}
h1 {
	font-family: "Trebuchet MS",Verdana,Geneva,Arial,Helvetica,sans-serif;
	font-size: 18px;
	color: #006E6F;
	margin: 0;
	margin-left: 4px;
	background-image: url(../images/logoErr.gif);
	background-repeat: no-repeat;
	height: 25px;
	padding: 6px 0 0 36px;}
</style>

</head>
<body style="background-image: none;">
<div style=" height: 31px; border-bottom: 1px solid #FFB755;">
  <h1 style="float:left;">XML Services</h1>
    
</div>
    <logic:present name="dcm.errors">
      <div class="error-msg">
		  <html:messages id="message" name="dcm.errors">
		     <bean:write name="message" filter="false"/>
		  </html:messages>
      </div>
	</logic:present>

</body>
</html>
		      
