<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %><%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
    <div style="width:100%;">

        <ed:breadcrumbs-push label="QA sandbox result" level="2" />
        <h1><spring:message code="label.qasandboxresult.title"/></h1>

        <%-- include Error display --%>
        <tiles:insertDefinition name="Error" />

        <div id="sanbox_result">
            <bean:write name="QASandboxForm" property="result"  filter="false" />
        </div>
        <br/>
        <br/>
        <form:form action="/qaSandboxForm?reset=false" method="post">
            <html:submit styleClass="button" property="action" >
                <spring:message code="label.qasandboxresult.back"/>
            </html:submit>
        </form:form>

    </div>
