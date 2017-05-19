<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

</div> <!-- workarea -->
</div> <!-- container -->

<ed:breadcrumbs-list htmlid="portal-breadcrumbs" classStyle="breadcrumbitem" classStyleEnd="breadcrumbitemlast"
                     delimiter=""/>

<tiles:importAttribute name="showFooter" />
<c:if test="${showFooter}">
  <div id="pagefoot">
    <p>
      <a href="http://www.eea.europa.eu" style="font-weight:bold">European Environment Agency</a>
      <br/>Kgs. Nytorv 6, DK-1050 Copenhagen K, Denmark - Phone: +45 3336 7100
    </p>
  </div>
</c:if>
