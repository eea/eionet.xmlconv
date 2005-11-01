<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-nested.tld" prefix="nested" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>

<ed:breadcrumbs-push label="XML Services" url="/index.jsp" level="0"/>
<%
String a=request.getContextPath(); 
session.setAttribute("webRoot",a==null?"":a);
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>XML Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css" media="screen">
	<!-- @import url(<c:url value="/css/main.css"/>); -->
</style>
<style type="text/css" media="screen">
	<!-- @import url(<c:url value="/css/portlet.css"/>); -->
</style>
<style type="text/css" media="screen">
	<!-- @import url(<c:url value="/css/wdsColumns.css"/>); -->
</style>
<link type="text/css" media="print" href="<c:url value="/css/print.css"/>" rel="stylesheet"></link>
<!--[if IE]>
<style type="text/css" media="screen">
	@import url(<c:url value="/css/portlet-ie.css"/>);
</style>
<link type="text/css" media="print" href="<c:url value="/css/print-ie.css"/>" rel="stylesheet"></link>
<script type="text/javascript" src="<c:url value="/scripts/ie_minwidth.js"/>"></script>
<![endif]-->

<!--[if IE 5]>
<style type="text/css" media="screen">
	@import url(<c:url value="/css/portlet-ie5.css"/>);
</style>
<![endif]-->
<link rel="shortcut icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" ></link>
<script type="text/javascript" src="<c:url value="/scripts/admin.js"/>"></script>
<script type="text/javascript" src="<c:url value="/scripts/user.js"/>"></script>
<script type="text/javascript">
	parentLocation='<%=request.getRequestURI()%>';
   	applicationRoot='<%=request.getContextPath()%>';
</script>
</head>
<body>
<div id="visual-portal-wrapper">
	<div id="portal-top">
		<div id="portal-logo">
			<div style="width:100%">	
				<ed:ui-renderer id="header"/>
			</div>
		</div>
		<h5 class="hiddenStructure">Navigation</h5>

		<div id="portal-breadcrumbs">
		   	  <a class="breadcrumbSep" href="http://www.eionet.eu.int/" title="European Environment Information and Observation Network (EIONET)">EIONET</a>
		</div>
		<div class="visualClear"></div>
		<br/>
		<!-- The wrapper div. It contains the three columns. -->
		<div id="portal-columns" class="visualColumnHideNone">
		
			<!-- start of the main and left columns -->
			<div id="visual-column-wrapper">
				<!-- start of main content block -->
				<div id="portal-column-content">
					<div id="content" class="documentEditable">
						<h5 class="hiddenStructure">Views</h5>
						<div class="documentContent" id="region-content">
							<div>
