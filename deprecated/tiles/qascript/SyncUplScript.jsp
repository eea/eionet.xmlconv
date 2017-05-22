<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<ed:breadcrumbs-push label="Update QA script from original location" level="3" />


    <h1 class="documentFirstHeading">
        <bean:message key="label.syncuplscript.title"/>
    </h1>

    <%-- include Error display --%>
    <tiles:insert definition="Error" />

    <html:form action="/syncUplScript" method="post">

    <p>
        Do you want to store the remote script as a local copy?
    </p>
    <div>
       <logic:present name="user">
        <html:submit styleClass="button" property="action">
            <bean:message key="label.uplSchema.updatecopy"/>
        </html:submit>
        <html:cancel styleClass="button">
            <bean:message key="label.cancel"/>
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
    </html:form>
    <div class="visualClear">&nbsp;</div>





