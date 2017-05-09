<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<ed:breadcrumbs-push label="Update XML schema cached copy" level="3"/>


<h1 class="documentFirstHeading">
  <spring:message code="label.syncuplschema.title"/>
</h1>

<%-- include Error display --%>
<tiles:insertDefinition name="Error"/>

<form:form action="/syncUplSchema" method="post">

  <p>
    Do you want to store the remote schema as a cached copy?
  </p>
  <div>
    <c:if present name="user">
      <html:submit styleClass="button" property="action">
        <spring:message code="label.uplSchema.updatecopy"/>
      </html:submit>
      <html:cancel styleClass="button">
        <spring:message code="label.stylesheet.cancel"/>
      </html:cancel>
    </c:if present>
  </div>
  <p>
    File downloaded from: <bean:write name="SyncUplSchemaForm" property="schemaUrl"/>
  </p>
  <pre><bean:write name="SyncUplSchemaForm" property="schemaFile"/></pre>

  <div style="display:none">
    <html:hidden property="schemaId"/>
    <html:hidden property="schemaUrl"/>
    <html:hidden property="uplSchemaId"/>
    <html:hidden property="uplSchemaFileName"/>
  </div>
</form:form>
<div class="visualClear">&nbsp;</div>





