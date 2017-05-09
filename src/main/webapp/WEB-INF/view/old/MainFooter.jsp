<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="tilesx" uri="http://tiles.apache.org/tags-tiles-extras" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>


<%
  String a = request.getContextPath();
  session.setAttribute("webRoot", a == null ? "" : a);
%>

</div> <!-- workarea -->
</div> <!-- container -->

<ed:breadcrumbs-list htmlid="portal-breadcrumbs" classStyle="breadcrumbitem" classStyleEnd="breadcrumbitemlast"
                     delimiter=""/>

<%--<tiles:importAttribute name="showFooter" />--%>
<c:if test="${showFooter == true}">
  <div id="pagefoot">
    <p>
      <a href="http://www.eea.europa.eu" style="font-weight:bold">European Environment Agency</a>
      <br/>Kgs. Nytorv 6, DK-1050 Copenhagen K, Denmark - Phone: +45 3336 7100
    </p>
  </div>
</c:if>
