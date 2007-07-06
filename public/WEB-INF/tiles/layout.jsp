<%@ page buffer="100kb" pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ page import="eionet.gdem.web.filters.EionetCASFilter" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
		<title>
			<tiles:importAttribute name="title" scope="request"/>
			<bean:message name="title"/>
		</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="stylesheet" type="text/css" href="http://www.eionet.europa.eu/styles/eionet2007/print.css" media="print" />
		<link rel="stylesheet" type="text/css" href="http://www.eionet.europa.eu/styles/eionet2007/handheld.css" media="handheld" />		
	    <link rel="stylesheet" type="text/css" href="http://www.eionet.europa.eu/styles/eionet2007/screen.css" media="screen" title="Eionet 2007 style" />
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
		<![endif]-->
		<!--[if IE 5]>
		<style type="text/css" media="screen">
			@import url(<c:url value="/css/portlet-ie5.css"/>);
		</style>
		<![endif]-->
		<link rel="shortcut icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" ></link>
		<script type="text/javascript" src="<c:url value="/scripts/admin.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/scripts/user.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/scripts/pageops.js"/>"></script>
		<script type="text/javascript">
			// <![CDATA[
				parentLocation='<%=request.getRequestURI()%>';
			   	applicationRoot='<%=request.getContextPath()%>';
			// ]]>
		</script>
		<% if (session.getAttribute(EionetCASFilter.CAS_FILTER_USER) == null )  {%>
		<script type="text/javascript" >
			// <![CDATA[
				function get_cookie( cookie_name )
				{
				  var results = document.cookie.match ( cookie_name + '=(.*?)(;|$)' );				
				  if ( results )
				    return ( unescape ( results[1] ) );
				  else
				    return null;
				}
				eionetLoginCookieValue = get_cookie("<%= EionetCASFilter.EIONET_LOGIN_COOKIE_NAME %>");
				if (eionetLoginCookieValue != null && eionetLoginCookieValue == "loggedIn"){	
					window.location="<%=EionetCASFilter.getEionetCookieCASLoginURL(request) %>";
				}
			// ]]>
		</script>
		<%}%>
	</head>

	<body>
		<%-- include header --%>
		<tiles:insert attribute="header"/>
		
		<%-- include Error display --%>
		<tiles:insert definition="Error" />
		    
		<%-- include body --%>
		<tiles:insert attribute="body"/>
		    
		<tiles:useAttribute id="showFooter" name="showFooter"/>
		<%-- include footer --%>
		<tiles:insert attribute="footer">
			<tiles:put name="showFooter" beanName="showFooter" />
		</tiles:insert>
	</body>
	
</html>