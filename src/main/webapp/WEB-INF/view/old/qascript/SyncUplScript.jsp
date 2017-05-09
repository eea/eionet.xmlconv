<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<ed:breadcrumbs-push label="Update QA script from original location" level="3" />


    <h1 class="documentFirstHeading">
        <spring:message code="label.syncuplscript.title"/>
    </h1>

    <%-- include Error display --%>
    <tiles:insertDefinition name="Error" />

    <form:form action="/syncUplScript" method="post">

    <p>
        Do you want to store the remote script as a local copy?
    </p>
    <div>
       <logic:present name="user">
        <html:submit styleClass="button" property="action">
            <spring:message code="label.uplSchema.updatecopy"/>
        </html:submit>
        <html:cancel styleClass="button">
            <spring:message code="label.cancel"/>
        </html:cancel>
      </logic:present>
    </div>
    <p>
    File downloaded from: <bean:write name="SyncUplScriptForm" property="url" />
    </p>
    <pre><bean:write name="SyncUplScriptForm" property="scriptFile" /></pre>

    <div style="display:none">
          <html:hidden property="scriptId" />
          <html:hidden property="scriptFile" />
          <html:hidden property="fileName" />
          <html:hidden property="url" />
    </div>
    </form:form>
    <div class="visualClear">&nbsp;</div>





