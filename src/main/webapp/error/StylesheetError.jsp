<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
<head>
  <title></title>
  <style type="text/css" media="screen">
    <!--
    @import url(<c:url value="../css/main.css"/>);
    -->
  </style>
  <style type="text/css">
    body {
      margin: 0;
      padding: 0;
      background-color: #f0f0f0;
    }

    h1 {
      font-family: "Trebuchet MS", Verdana, Geneva, Arial, Helvetica, sans-serif;
      font-size: 18px;
      color: #006E6F;
      margin: 0;
      margin-left: 4px;
      background-image: url(../images/logoErr.gif);
      background-repeat: no-repeat;
      height: 25px;
      padding: 6px 0 0 36px;
    }
  </style>

</head>
<body style="background-image: none;">
<div style=" height: 31px; border-bottom: 1px solid #FFB755;">
  <h1 style="float:left;">XML Services</h1>

</div>
<c:if test="${dcm.errors}">
  <div class="error-msg">
    <div id="message" name="dcm.errors">${"message"}</div>
  </div>
</c:if>
</body>
</html>

