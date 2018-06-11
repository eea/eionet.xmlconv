<%@ page buffer="100kb" pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="XML Services" url="/index.jsp" level="0"/>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
  <title>
    <tiles:importAttribute name="title" scope="request"/>
    <spring:message code="${requestScope['title']}"/>
  </title>
  <sec:csrfMetaTags/>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <!--  EEA central styles -->
  <link rel="stylesheet" type="text/css" href="https://www.eionet.europa.eu/styles/eionet2007/print.css" media="print"/>
  <link rel="stylesheet" type="text/css" href="https://www.eionet.europa.eu/styles/eionet2007/handheld.css" media="handheld"/>
  <link rel="stylesheet" type="text/css" href="https://www.eionet.europa.eu/styles/eionet2007/screen.css" media="screen"/>

<%--  <link rel="stylesheet" type="text/css" href="/css/print.css" media="print"/>
  <link rel="stylesheet" type="text/css" href="/css/handheld.css" media="handheld"/>
  <link rel="stylesheet" type="text/css" href="/css/screen.css" media="screen"/>--%>
  <!--  local style -->
  <link rel="stylesheet" type="text/css" href="/css/xmlconv.css" media="screen" />
  <link rel="stylesheet" type="text/css" href="/webjars/foundation/dist/css/foundation.min.css" media="screen"/>
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
