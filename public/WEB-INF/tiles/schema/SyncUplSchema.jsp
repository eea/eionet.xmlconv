<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<ed:breadcrumbs-push label="Update XML schema cached copy" level="3" />


	<h1 class="documentFirstHeading">
		<bean:message key="label.syncuplschema.title"/>
	</h1>

	<%-- include Error display --%>
	<tiles:insert definition="Error" />

	<html:form action="/syncUplSchema" method="post">

	<p>
		Do you want to store the remote schema as a cached copy?
	</p>
	<div>
   	<logic:present name="user">
        <html:submit styleClass="button" property="action">
        	<bean:message key="label.uplSchema.updatecopy"/>
        </html:submit>
        <html:cancel styleClass="button">
        	<bean:message key="label.stylesheet.cancel"/>
	    </html:cancel>
      </logic:present>
	</div>
	<p>
	File downloaded from: <bean:write name="SyncUplSchemaForm" property="schemaUrl" />
	</p>
	<pre>
		<bean:write name="SyncUplSchemaForm" property="schemaFile" />
	</pre>
	
	<div style="display:none">
   	   <html:hidden property="schemaId" />
   	   <html:hidden property="schemaUrl" />
   	   <html:hidden property="uplSchemaId" />       		        	 		        	
   	   <html:hidden property="uplSchemaFileName" />       		        	 		        	
	</div>
	</html:form>
	<div class="visualClear">&nbsp;</div>





