<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">

  <ed:breadcrumbs-push label="QA sandbox result" level="2"/>
  <h1><spring:message code="label.qasandboxresult.title"/></h1>




  <div id="sanbox_result">
    <bean:write name="QASandboxForm" property="result" filter="false"/>
  </div>
  <br/>
  <br/>
  <form:form action="/qaSandboxForm?reset=false" method="post">
    <html:submit styleClass="button" property="action">
      <spring:message code="label.qasandboxresult.back"/>
    </html:submit>
  </form:form>

</div>
