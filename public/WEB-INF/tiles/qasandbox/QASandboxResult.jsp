<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
	<div style="width:100%;">
				
		<ed:breadcrumbs-push label="QA sandbox result" level="2" />
		<h1><bean:message key="label.qasandboxresult.title"/></h1> 
	
		<%-- include Error display --%>
		<tiles:insert definition="Error" />

		<div id="sanbox_result">
			<bean:write name="QASandboxForm" property="result"  filter="false" />
		</div>
		<br/>
		<br/>
		<html:form action="/qaSandboxForm?reset=false" method="post">
	        <html:submit styleClass="button" property="action" >
				<bean:write key="label.qasandboxresult.back"/>
			</html:submit>
		</html:form>

	</div>
