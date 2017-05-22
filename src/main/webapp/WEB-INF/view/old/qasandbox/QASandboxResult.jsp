<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">

  <ed:breadcrumbs-push label="QA sandbox result" level="2"/>
  <h1><spring:message code="label.qasandboxresult.title"/></h1>




  <div id="sanbox_result">
    <%--filter="false"--%>
    ${QASandboxForm.result}
  </div>
  <br/>
  <br/>
  <form:form action="/qaSandboxForm?reset=false" method="post">
    <button type="submit" styleClass="button" property="action">
      <spring:message code="label.qasandboxresult.back"/>
    </button>
  </form:form>

</div>
