<%--<%@ page buffer="100kb" pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="XML Services" url="/index.jsp" level="0"/>

<%
  String a = request.getContextPath();
  session.setAttribute("webRoot", a == null ? "" : a);
  response.setHeader("Pragma", "No-cache");
  response.setHeader("Cache-Control", "no-cache");
  response.setHeader("Cache-Control", "no-store");
  response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
  <title>
    <tiles:importAttribute name="title" scope="request"/>
    <spring:message code="${requestScope['title']}"/>
  </title>
  <sec:csrfMetaTags/>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <!--  EEA central styles -->
  <%--<link rel="stylesheet" type="text/css" href="//www.eionet.europa.eu/styles/eionet2007/print.css" media="print"/>--%>
  <%--<link rel="stylesheet" type="text/css" href="//www.eionet.europa.eu/styles/eionet2007/handheld.css" media="handheld"/>--%>
  <%--<link rel="stylesheet" type="text/css" href="//www.eionet.europa.eu/styles/eionet2007/screen.css" media="screen"/>--%>

  <link rel="stylesheet" type="text/css" href="/css/print.css" media="print"/>
  <link rel="stylesheet" type="text/css" href="/css/handheld.css" media="handheld"/>
  <link rel="stylesheet" type="text/css" href="/css/screen.css" media="screen"/>
  <!--  local style -->
  <link rel="stylesheet" type="text/css" href="/css/xmlconv.css" media="screen" />
  <link rel="stylesheet" type="text/css" href="/webjars/foundation/6.4.2/dist/css/foundation.css" media="screen"/>
  <link rel="stylesheet" type="text/css" href="/css/main.css" media="screen"/>
  <link rel="shortcut icon" href="/images/favicon.ico" type="image/x-icon"/>
  <!-- jQuery javascripts and css-->
  <link href="/webjars/jquery-ui/jquery-ui.css" rel="stylesheet" type="text/css"/>
  <script type="text/javascript" src="/webjars/jquery/jquery.min.js"></script>
  <script type="text/javascript" src="/webjars/jquery-ui/jquery-ui.js"></script>
  <script type="text/javascript" src="/webjars/jquery-validation/jquery.validate.js"></script>
  <!-- local javascripts -->
  <script type="text/javascript" src="/js/admin.js"></script>
  <script type="text/javascript" src="/js/pageops.js"></script>
  <%--<script type="text/javascript" src="/js/foundation.js"></script>--%>
  <tiles:insertAttribute name="specific_header" ignore="true"/>
</head>

<body>
  <tiles:insertAttribute name="header"/>
  <tiles:insertAttribute name="messages"/>
  <tiles:insertAttribute name="body"/>
  <tiles:insertAttribute name="footer"/>
</body>

</html>
